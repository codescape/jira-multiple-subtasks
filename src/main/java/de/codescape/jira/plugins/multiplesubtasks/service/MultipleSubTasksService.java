package de.codescape.jira.plugins.multiplesubtasks.service;

import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.config.PriorityManager;
import com.atlassian.jira.config.SubTaskManager;
import com.atlassian.jira.exception.CreateException;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueFactory;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.priority.Priority;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

// TODO extract interface
// TODO create tests
@Component
public class MultipleSubTasksService {

    private final IssueService issueService;
    private final IssueFactory issueFactory;
    private final IssueManager issueManager;
    private final SubTaskManager subTaskManager;
    private final PriorityManager priorityManager;
    private final JiraAuthenticationContext jiraAuthenticationContext;
    private final SyntaxService syntaxService;

    @Autowired
    public MultipleSubTasksService(@ComponentImport IssueService issueService,
                                   @ComponentImport IssueFactory issueFactory,
                                   @ComponentImport IssueManager issueManager,
                                   @ComponentImport SubTaskManager subTaskManager,
                                   @ComponentImport PriorityManager priorityManager,
                                   @ComponentImport JiraAuthenticationContext jiraAuthenticationContext,
                                   SyntaxService syntaxService) {
        this.issueService = issueService;
        this.issueFactory = issueFactory;
        this.issueManager = issueManager;
        this.subTaskManager = subTaskManager;
        this.priorityManager = priorityManager;
        this.jiraAuthenticationContext = jiraAuthenticationContext;
        this.syntaxService = syntaxService;
    }

    // docs https://community.atlassian.com/t5/Answers-Developer-Questions/Auto-create-subtask-and-assign-to-users/qaq-p/530837
    public List<Issue> subTasksFromString(String issueKey, String input) {
        ArrayList<Issue> subTasks = new ArrayList<>();

        IssueService.IssueResult issueResult = issueService.getIssue(jiraAuthenticationContext.getLoggedInUser(), issueKey);
        MutableIssue parent = issueResult.getIssue();
        if (parent == null) {
            throw new RuntimeException("Parent issue not found.");
        }

        Project projectObject = parent.getProjectObject();
        if (projectObject == null) {
            throw new RuntimeException("Parent project not found.");
        }

        IssueType subTaskType = projectObject.getIssueTypes().stream().filter(IssueType::isSubTask).findFirst().orElse(null);
        if (subTaskType == null) {
            throw new RuntimeException("No sub task types found.");
        }

        syntaxService.parseString(input).forEach(subTaskRequest -> {
            MutableIssue newSubTask = issueFactory.getIssue();
            newSubTask.setParentObject(parent);
            newSubTask.setProjectObject(parent.getProjectObject());

            // summary
            newSubTask.setSummary(subTaskRequest.getSummary());

            // priority
            if (subTaskRequest.getPriority() != null) {
                Priority priority = priorityManager.getPriorities().stream()
                    .filter(availablePriority -> availablePriority.getName().equals(subTaskRequest.getPriority()))
                    .findFirst()
                    .orElse(parent.getPriority());
                newSubTask.setPriority(priority);
            } else {
                newSubTask.setPriority(parent.getPriority());
            }

            newSubTask.setIssueType(subTaskType);
            try {
                issueManager.createIssueObject(jiraAuthenticationContext.getLoggedInUser(), newSubTask);
                subTaskManager.createSubTaskIssueLink(parent, newSubTask, jiraAuthenticationContext.getLoggedInUser());
            } catch (CreateException e) {
                throw new RuntimeException(e);
            }
            subTasks.add(newSubTask);
        });

        return subTasks;
    }

}
