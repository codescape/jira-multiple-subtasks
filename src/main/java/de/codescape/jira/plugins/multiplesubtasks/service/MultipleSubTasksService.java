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

// TODO extract interface (?)
// TODO create tests
@Component
public class MultipleSubTasksService {

    private static final String INHERIT_MARKER = "@inherit";

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
    private final SubTasksSyntaxService subtasksSyntaxService;

    @Autowired
    public MultipleSubTasksService(@ComponentImport IssueService issueService,
                                   @ComponentImport IssueFactory issueFactory,
                                   @ComponentImport IssueManager issueManager,
                                   @ComponentImport SubTaskManager subTaskManager,
                                   @ComponentImport PriorityManager priorityManager,
                                   @ComponentImport AssigneeService assigneeService,
                                   @ComponentImport UserManager userManager,
                                   @ComponentImport ProjectComponentManager projectComponentManager,
                                   @ComponentImport LabelManager labelManager,
                                   @ComponentImport JiraAuthenticationContext jiraAuthenticationContext,
                                   SubTasksSyntaxService subtasksSyntaxService) {
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
    }

    // docs https://community.atlassian.com/t5/Answers-Developer-Questions/Auto-create-subtask-and-assign-to-users/qaq-p/530837
    public List<Issue> subTasksFromString(String issueKey, String inputString) {
        ArrayList<Issue> subTasksCreated = new ArrayList<>();

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
            MutableIssue newSubTask = issueFactory.getIssue();
            // parent issue
            newSubTask.setParentObject(parent);

            // project
            newSubTask.setProjectObject(parent.getProjectObject());

            // summary
            newSubTask.setSummary(subTaskRequest.getSummary());

            // description
            // use the optionally provided description
            if (subTaskRequest.getDescription() != null) {
                newSubTask.setDescription(subTaskRequest.getDescription());
            }

            // priority
            // try to find provided priority otherwise fall back to priority of parent issue
            if (subTaskRequest.getPriority() != null) {
                // TODO a priority scheme can be configured per project
                Priority priority = priorityManager.getPriorities().stream()
                    .filter(availablePriority -> availablePriority.getName().equals(subTaskRequest.getPriority()))
                    .findFirst()
                    .orElse(parent.getPriority());
                newSubTask.setPriority(priority);
            } else {
                newSubTask.setPriority(parent.getPriority());
            }

            // issueType
            // try to find provided issue type otherwise fall back and use first sub-task type found
            if (subTaskRequest.getIssueType() != null) {
                IssueType issueType = subTaskTypes.stream()
                    .filter(availableIssueType -> availableIssueType.getName().equals(subTaskRequest.getIssueType()))
                    .findFirst().orElse(null);
                newSubTask.setIssueType(issueType);
            }
            if (newSubTask.getIssueType() == null) {
                newSubTask.setIssueType(subTaskTypes.get(0));
            }

            // assignee
            // try to find provided assignee in the list of assignable users for current project and ignore users who are not assignable
            if (subTaskRequest.getAssignee() != null) {
                ApplicationUser assignee = assigneeService.findAssignableUsers(subTaskRequest.getAssignee(), projectObject).stream()
                    .findFirst().orElse(null);
                newSubTask.setAssignee(assignee);
            }

            // reporter
            // try to find provided reporter and otherwise use the current user
            ApplicationUser reporter = null;
            if (subTaskRequest.getReporter() != null) {
                reporter = userManager.getUserByName(subTaskRequest.getReporter());
            }
            newSubTask.setReporter(reporter != null ? reporter : jiraAuthenticationContext.getLoggedInUser());

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
                newSubTask.setComponent(components);
            }

            // create and link the sub-task to the parent issue
            try {
                issueManager.createIssueObject(jiraAuthenticationContext.getLoggedInUser(), newSubTask);
                subTaskManager.createSubTaskIssueLink(parent, newSubTask, jiraAuthenticationContext.getLoggedInUser());
                subTasksCreated.add(newSubTask);
            } catch (CreateException e) {
                throw new RuntimeException(e);
            }

            // label(s)
            // add optional multiple labels to the just created sub task (we need the ID of the sub task to add them)
            if (!subTaskRequest.getLabels().isEmpty()) {
                if (subTaskRequest.getLabels().contains(INHERIT_MARKER)) {
                    parent.getLabels().forEach(label ->
                        labelManager.addLabel(jiraAuthenticationContext.getLoggedInUser(), newSubTask.getId(), label.getLabel(), false)
                    );
                }
                subTaskRequest.getLabels().stream()
                    .filter(label -> !INHERIT_MARKER.equals(label))
                    .forEach(label ->
                        labelManager.addLabel(jiraAuthenticationContext.getLoggedInUser(), newSubTask.getId(), label, false)
                    );
            }
        });

        return subTasksCreated;
    }

}
