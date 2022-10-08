package de.codescape.jira.plugins.multiplesubtasks.condition;

import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.issuetype.MockIssueType;
import com.atlassian.jira.plugin.webfragment.model.JiraHelper;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.user.ApplicationUser;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.ArrayList;
import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

public class MultipleSubtasksDialogForIssueConditionTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule().silent();

    @InjectMocks
    private MultipleSubtasksDialogForIssueCondition condition;

    @Mock
    private ApplicationUser user;

    @Mock
    private MutableIssue issue;

    @Mock
    private JiraHelper jiraHelper;

    @Mock
    private Project project;

    @Test
    public void shouldReturnFalseForSubtask() {
        expectIssueToBeSubtask(true);
        expectProjectHasSubtasks(true);
        assertThat(condition.shouldDisplay(user, issue, jiraHelper), is(equalTo(false)));
    }

    @Test
    public void shouldReturnFalseForProjectWithoutSubtasks() {
        expectIssueToBeSubtask(false);
        expectProjectHasSubtasks(false);
        assertThat(condition.shouldDisplay(user, issue, jiraHelper), is(equalTo(false)));
    }

    @Test
    public void shouldReturnTrueForIssueInProjectWithSubtasks() {
        expectIssueToBeSubtask(false);
        expectProjectHasSubtasks(true);
        assertThat(condition.shouldDisplay(user, issue, jiraHelper), is(equalTo(true)));
    }

    /* helper methods */

    private void expectIssueToBeSubtask(boolean isSubtask) {
        when(issue.isSubTask()).thenReturn(isSubtask);
    }

    private void expectProjectHasSubtasks(boolean projectHasSubtasks) {
        when(issue.getProjectObject()).thenReturn(project);
        Collection<IssueType> issueTypes = new ArrayList<>();
        issueTypes.add(new MockIssueType("110", "ISSUE", "Standard Issue", false));
        if (projectHasSubtasks) {
            issueTypes.add(new MockIssueType("111", "SUBTASK", "Sub Task", true));
        }
        when(project.getIssueTypes()).thenReturn(issueTypes);
    }

}
