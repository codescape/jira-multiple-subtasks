package de.codescape.jira.plugins.multiplesubtasks.condition;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.plugin.webfragment.conditions.AbstractIssueWebCondition;
import com.atlassian.jira.plugin.webfragment.model.JiraHelper;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.user.ApplicationUser;
import org.springframework.stereotype.Component;

/**
 * Condition to check whether subtasks can be created for the given issue. Decision is based on the following rules:
 * <p>
 * - issue is not a subtask itself and thus is allowed to have subtasks
 * - project the issue is in has sub-tasks configured
 */
@Component
public class DisplayDialogForIssueCondition extends AbstractIssueWebCondition {

    @Override
    public boolean shouldDisplay(ApplicationUser applicationUser, Issue issue, JiraHelper jiraHelper) {
        return issueIsNotSubTask(issue) && projectOfIssueHasSubTasks(issue);
    }

    // project of the issue itself has subtasks configured
    private boolean projectOfIssueHasSubTasks(Issue issue) {
        Project projectObject = issue.getProjectObject();
        if (projectObject == null) {
            return false;
        }
        return projectObject.getIssueTypes().stream().anyMatch(IssueType::isSubTask);
    }

    // the issue itself must not be a subtask to allow creation of subtasks for it
    private boolean issueIsNotSubTask(Issue issue) {
        return !issue.isSubTask();
    }

}
