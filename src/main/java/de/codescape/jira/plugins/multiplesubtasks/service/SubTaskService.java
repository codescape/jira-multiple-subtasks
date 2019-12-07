package de.codescape.jira.plugins.multiplesubtasks.service;

import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.config.SubTaskManager;
import com.atlassian.jira.exception.CreateException;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueFactory;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// TODO extract interface
// TODO create tests
@Component
public class SubTaskService {

    private IssueService issueService;
    private IssueFactory issueFactory;
    private IssueManager issueManager;
    private SubTaskManager subTaskManager;
    private JiraAuthenticationContext jiraAuthenticationContext;

    @Autowired
    public SubTaskService(@ComponentImport IssueService issueService,
                          @ComponentImport IssueFactory issueFactory,
                          @ComponentImport IssueManager issueManager,
                          @ComponentImport SubTaskManager subTaskManager,
                          @ComponentImport JiraAuthenticationContext jiraAuthenticationContext) {
        this.issueService = issueService;
        this.issueFactory = issueFactory;
        this.issueManager = issueManager;
        this.subTaskManager = subTaskManager;
        this.jiraAuthenticationContext = jiraAuthenticationContext;
    }

    // docs https://community.atlassian.com/t5/Answers-Developer-Questions/Auto-create-subtask-and-assign-to-users/qaq-p/530837
    public List<Issue> subTasksFromString(String issueKey, String input) {
        ArrayList<Issue> subTasks = new ArrayList<>();

        IssueService.IssueResult issueResult = issueService.getIssue(jiraAuthenticationContext.getLoggedInUser(), issueKey);
        MutableIssue parent = issueResult.getIssue();
        if (parent == null) {
            throw new RuntimeException("Parent issue not found.");
        }

        Collection<IssueType> issueTypes = parent.getProjectObject().getIssueTypes();
        IssueType subTaskType = issueTypes.stream().filter(issueType -> issueType.isSubTask()).findFirst().orElse(null);
        if (subTaskType == null) {
            throw new RuntimeException("No sub task types found.");
        }

        new BufferedReader(new StringReader(input)).lines().forEach(taskSummary -> {
            MutableIssue newSubTask = issueFactory.getIssue();
            newSubTask.setSummary(taskSummary);
            newSubTask.setParentObject(parent);
            newSubTask.setProjectObject(parent.getProjectObject());
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
