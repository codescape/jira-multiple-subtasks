package de.codescape.jira.plugins.multiplesubtasks.condition;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.plugin.webfragment.conditions.AbstractIssueWebCondition;
import com.atlassian.jira.plugin.webfragment.model.JiraHelper;
import com.atlassian.jira.user.ApplicationUser;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * This condition ensures that the issue has a project with subtask issue types available.
 */
@Component
public class ProjectOfIssueHasSubtasksCondition extends AbstractIssueWebCondition {

    @Override
    public boolean shouldDisplay(ApplicationUser user, Issue issue, JiraHelper jiraHelper) {
        return Objects.requireNonNull(issue.getProjectObject()).getIssueTypes().stream().anyMatch(IssueType::isSubTask);
    }

}
