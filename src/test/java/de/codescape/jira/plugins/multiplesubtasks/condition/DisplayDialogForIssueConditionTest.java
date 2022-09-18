package de.codescape.jira.plugins.multiplesubtasks.condition;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.issuetype.IssueType;
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

public class DisplayDialogForIssueConditionTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @InjectMocks
    private DisplayDialogForIssueCondition condition;

    @Mock
    private Issue issue;

    @Mock
    private ApplicationUser applicationUser;

    @Mock
    private JiraHelper jiraHelper;

    @Mock
    private Project project;

    @Mock
    private IssueType issueType;

    /* shouldDisplay */

    @Test
    public void shouldDisplayWhenIssueIsNotASubtaskAndProjectHasSubtasksConfigured() {
        issueIsNotASubtask();
        projectHasSubtasksConfigured();

        assertThat(condition.shouldDisplay(applicationUser, issue, jiraHelper), is(true));
    }

    @Test
    public void shouldNotDisplayWhenIssueIsASubtaskAndProjectHasSubtasksConfigured() {
        issueIsASubtask();
        projectHasSubtasksConfigured();

        assertThat(condition.shouldDisplay(applicationUser, issue, jiraHelper), is(false));
    }

    @Test
    public void shouldNotDisplayWhenIssueIsANotSubtaskAndProjectHasNotSubtasksConfigured() {
        issueIsNotASubtask();
        projectHasNoSubtasksConfigured();

        assertThat(condition.shouldDisplay(applicationUser, issue, jiraHelper), is(false));
    }

    @Test
    public void shouldNotDisplayWhenIssueIsASubtaskAndProjectHasNoSubtasksConfigured() {
        issueIsASubtask();
        projectHasNoSubtasksConfigured();

        assertThat(condition.shouldDisplay(applicationUser, issue, jiraHelper), is(false));
    }

    /* helpers */

    private void projectHasNoSubtasksConfigured() {
        when(issue.getProjectObject()).thenReturn(project);
        ArrayList<IssueType> issueTypes = new ArrayList<>();
        issueTypes.add(issueType);
        when(project.getIssueTypes()).thenReturn(issueTypes);
        when(issueType.isSubTask()).thenReturn(false);
    }

    private void projectHasSubtasksConfigured() {
        when(issue.getProjectObject()).thenReturn(project);
        ArrayList<IssueType> issueTypes = new ArrayList<>();
        issueTypes.add(issueType);
        when(project.getIssueTypes()).thenReturn(issueTypes);
        when(issueType.isSubTask()).thenReturn(true);
    }

    private void issueIsNotASubtask() {
        when(issue.isSubTask()).thenReturn(false);
    }

    private void issueIsASubtask() {
        when(issue.isSubTask()).thenReturn(true);
    }

}
