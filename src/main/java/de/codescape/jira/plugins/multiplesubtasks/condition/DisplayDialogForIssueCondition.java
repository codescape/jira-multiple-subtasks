package de.codescape.jira.plugins.multiplesubtasks.condition;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.plugin.webfragment.conditions.AbstractIssueWebCondition;
import com.atlassian.jira.plugin.webfragment.model.JiraHelper;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.workflow.condition.AbstractJiraCondition;
import org.springframework.stereotype.Component;

// TODO document class
// TODO implement tests
@Component
public class DisplayDialogForIssueCondition extends AbstractIssueWebCondition {

    @Override
    public boolean shouldDisplay(ApplicationUser applicationUser, Issue issue, JiraHelper jiraHelper) {
        return !issue.isSubTask();
    }

}
