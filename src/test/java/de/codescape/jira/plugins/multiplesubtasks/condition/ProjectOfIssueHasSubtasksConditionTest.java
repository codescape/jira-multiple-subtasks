package de.codescape.jira.plugins.multiplesubtasks.condition;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.project.Project;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.ArrayList;
import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProjectOfIssueHasSubtasksConditionTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    private ProjectOfIssueHasSubtasksCondition condition;

    @Before
    public void before() {
        condition = new ProjectOfIssueHasSubtasksCondition();
    }

    @Test
    public void shouldReturnTrueForProjectWithSubtasks() {
        Issue issue = issueInProjectWithIssueTypes(standardIssueType(), subtaskIssueType());
        assertThat(condition.shouldDisplay(null, issue, null), is(true));
    }

    @Test
    public void shouldReturnFalseForProjectWithoutSubtasks() {
        Issue issue = issueInProjectWithIssueTypes(standardIssueType(), standardIssueType(), standardIssueType());
        assertThat(condition.shouldDisplay(null, issue, null), is(false));
    }

    private Issue issueInProjectWithIssueTypes(IssueType... issueTypes) {
        Project project = mock(Project.class);
        when(project.getIssueTypes()).thenReturn(new ArrayList<>(Arrays.asList(issueTypes)));

        Issue issue = mock(Issue.class);
        when(issue.getProjectObject()).thenReturn(project);

        return issue;
    }

    private IssueType subtaskIssueType() {
        IssueType issueType = mock(IssueType.class);
        when(issueType.isSubTask()).thenReturn(true);
        return issueType;
    }

    private IssueType standardIssueType() {
        IssueType issueType = mock(IssueType.class);
        when(issueType.isSubTask()).thenReturn(false);
        return issueType;
    }

}
