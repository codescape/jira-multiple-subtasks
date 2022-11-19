package de.codescape.jira.plugins.multiplesubtasks.service;

import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.bc.project.component.ProjectComponent;
import com.atlassian.jira.bc.project.component.ProjectComponentManager;
import com.atlassian.jira.bc.user.search.AssigneeService;
import com.atlassian.jira.config.PriorityManager;
import com.atlassian.jira.config.SubTaskManager;
import com.atlassian.jira.exception.CreateException;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueFactory;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.label.LabelManager;
import com.atlassian.jira.issue.priority.Priority;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service to create multiple subtasks from the results of the {@link SubtasksSyntaxService} for a given issue.
 */
@Component
public class SubtasksCreationService {

    private static final String INHERIT_MARKER = "@inherit";
    private static final String CURRENT_MARKER = "@current";

    private final IssueService issueService;
    private final IssueFactory issueFactory;
    private final IssueManager issueManager;
    private final SubTaskManager subTaskManager;
    private final PriorityManager priorityManager;
    private final AssigneeService assigneeService;
    private final UserManager userManager;
    private final ProjectComponentManager projectComponentManager;
    private final LabelManager labelManager;
    private final JiraAuthenticationContext jiraAuthenticationContext;
    private final SubtasksSyntaxService subtasksSyntaxService;
    private final EstimateStringService estimateStringService;

    @Autowired
    public SubtasksCreationService(@ComponentImport IssueService issueService,
                                   @ComponentImport IssueFactory issueFactory,
                                   @ComponentImport IssueManager issueManager,
                                   @ComponentImport SubTaskManager subTaskManager,
                                   @ComponentImport PriorityManager priorityManager,
                                   @ComponentImport AssigneeService assigneeService,
                                   @ComponentImport UserManager userManager,
                                   @ComponentImport ProjectComponentManager projectComponentManager,
                                   @ComponentImport LabelManager labelManager,
                                   @ComponentImport JiraAuthenticationContext jiraAuthenticationContext,
                                   SubtasksSyntaxService subtasksSyntaxService,
                                   EstimateStringService estimateStringService) {
        this.issueService = issueService;
        this.issueFactory = issueFactory;
        this.issueManager = issueManager;
        this.subTaskManager = subTaskManager;
        this.priorityManager = priorityManager;
        this.assigneeService = assigneeService;
        this.userManager = userManager;
        this.projectComponentManager = projectComponentManager;
        this.labelManager = labelManager;
        this.jiraAuthenticationContext = jiraAuthenticationContext;
        this.subtasksSyntaxService = subtasksSyntaxService;
        this.estimateStringService = estimateStringService;
    }

    // docs https://community.atlassian.com/t5/Answers-Developer-Questions/Auto-create-subtask-and-assign-to-users/qaq-p/530837
    public List<Issue> subtasksFromString(String issueKey, String inputString) {
        ArrayList<Issue> subtasksCreated = new ArrayList<>();

        IssueService.IssueResult issueResult = issueService.getIssue(jiraAuthenticationContext.getLoggedInUser(), issueKey);
        MutableIssue parent = issueResult.getIssue();
        if (parent == null) {
            throw new RuntimeException("Parent issue not found.");
        }

        Project projectObject = parent.getProjectObject();
        if (projectObject == null) {
            throw new RuntimeException("Parent project not found.");
        }

        List<IssueType> subTaskTypes = projectObject.getIssueTypes().stream().filter(IssueType::isSubTask).collect(Collectors.toList());
        if (subTaskTypes.isEmpty()) {
            throw new RuntimeException("No sub-task types found.");
        }

        subtasksSyntaxService.parseString(inputString).forEach(subTaskRequest -> {
            MutableIssue newSubtask = issueFactory.getIssue();
            // parent issue
            newSubtask.setParentObject(parent);

            // project
            newSubtask.setProjectObject(parent.getProjectObject());

            // summary
            newSubtask.setSummary(subTaskRequest.getSummary());

            // description
            // use the optionally provided description
            if (subTaskRequest.getDescription() != null) {
                newSubtask.setDescription(subTaskRequest.getDescription().replaceAll("\\{n}", "\n"));
            }

            // priority
            // try to find provided priority otherwise fall back to priority of parent issue
            if (subTaskRequest.getPriority() != null) {
                // TODO a priority scheme can be configured per project
                Priority priority = priorityManager.getPriorities().stream()
                    .filter(availablePriority -> availablePriority.getName().equals(subTaskRequest.getPriority()))
                    .findFirst()
                    .orElse(parent.getPriority());
                newSubtask.setPriority(priority);
            } else {
                newSubtask.setPriority(parent.getPriority());
            }

            // issueType
            // try to find provided issue type otherwise fall back and use first subtask type found
            if (subTaskRequest.getIssueType() != null) {
                IssueType issueType = subTaskTypes.stream()
                    .filter(availableIssueType -> availableIssueType.getName().equals(subTaskRequest.getIssueType()))
                    .findFirst().orElse(null);
                newSubtask.setIssueType(issueType);
            }
            if (newSubtask.getIssueType() == null) {
                newSubtask.setIssueType(subTaskTypes.get(0));
            }

            // assignee
            // try to find provided assignee in the list of assignable users for current project and ignore users who are not assignable
            if (subTaskRequest.getAssignee() != null) {
                if (INHERIT_MARKER.equals(subTaskRequest.getAssignee())) {
                    newSubtask.setAssignee(parent.getAssignee());
                } else if (CURRENT_MARKER.equals(subTaskRequest.getAssignee())) {
                    newSubtask.setAssignee(jiraAuthenticationContext.getLoggedInUser());
                } else {
                    newSubtask.setAssignee(assigneeService.findAssignableUsers(subTaskRequest.getAssignee(), projectObject)
                        .stream().findFirst().orElse(null));
                }
            }

            // reporter
            // try to find provided reporter and otherwise use the current user
            ApplicationUser reporter = null;
            if (subTaskRequest.getReporter() != null) {
                reporter = userManager.getUserByName(subTaskRequest.getReporter());
            }
            newSubtask.setReporter(reporter != null ? reporter : jiraAuthenticationContext.getLoggedInUser());

            // component(s)
            // add optional components to the subtask and ignore components that do not exist
            if (!subTaskRequest.getComponents().isEmpty()) {
                Set<ProjectComponent> components = subTaskRequest.getComponents().stream()
                    .filter(component -> !INHERIT_MARKER.equals(component))
                    .map(component -> projectComponentManager.findByComponentName(projectObject.getId(), component))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
                if (subTaskRequest.getComponents().contains(INHERIT_MARKER)) {
                    components.addAll(parent.getComponents());
                }
                newSubtask.setComponent(components);
            }

            // estimate
            // parse the estimate and set the duration in seconds
            if (subTaskRequest.getEstimate() != null) {
                newSubtask.setEstimate(estimateStringService.estimateStringToSeconds(subTaskRequest.getEstimate()));
            }

            // create and link the subtask to the parent issue
            try {
                issueManager.createIssueObject(jiraAuthenticationContext.getLoggedInUser(), newSubtask);
                subTaskManager.createSubTaskIssueLink(parent, newSubtask, jiraAuthenticationContext.getLoggedInUser());
                subtasksCreated.add(newSubtask);
            } catch (CreateException e) {
                throw new RuntimeException(e);
            }

            // label(s)
            // add optional multiple labels to the just created subtask (we need the ID of the subtask to add them)
            if (!subTaskRequest.getLabels().isEmpty()) {
                if (subTaskRequest.getLabels().contains(INHERIT_MARKER)) {
                    parent.getLabels().forEach(label ->
                        labelManager.addLabel(jiraAuthenticationContext.getLoggedInUser(), newSubtask.getId(), label.getLabel(), false)
                    );
                }
                subTaskRequest.getLabels().stream()
                    .filter(label -> !INHERIT_MARKER.equals(label))
                    .forEach(label ->
                        labelManager.addLabel(jiraAuthenticationContext.getLoggedInUser(), newSubtask.getId(), label, false)
                    );
            }
        });

        return subtasksCreated;
    }

}
