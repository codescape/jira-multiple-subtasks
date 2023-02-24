package de.codescape.jira.plugins.multiplesubtasks.model;

import com.atlassian.jira.issue.Issue;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class CreatedSubtaskTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    private Issue issue;

    @Test
    public void shouldAcceptIssuesWithoutWarnings() {
        CreatedSubtask createdSubtask = new CreatedSubtask(issue, null);
        assertThat(createdSubtask.getIssue(), is(equalTo(issue)));
        assertThat(createdSubtask.getWarningsMessage(), is(nullValue()));
    }

    @Test
    public void shouldAcceptIssuesWithEmptyListOfWarnings() {
        CreatedSubtask createdSubtask = new CreatedSubtask(issue, new ArrayList<>());
        assertThat(createdSubtask.getIssue(), is(equalTo(issue)));
        assertThat(createdSubtask.getWarningsMessage(), is(nullValue()));
    }

    @Test
    public void shouldAcceptIssuesWithListOfWarnings() {
        List<String> warnings = new ArrayList<>();
        warnings.add("One warning is good.");
        warnings.add("But a second is better.");
        CreatedSubtask createdSubtask = new CreatedSubtask(issue, warnings);
        assertThat(createdSubtask.getIssue(), is(equalTo(issue)));
        assertThat(createdSubtask.getWarnings(), hasSize(2));
        assertThat(createdSubtask.getWarnings(), hasItems(warnings.get(0), warnings.get(1)));
        assertThat(createdSubtask.getWarningsMessage(), allOf(
            startsWith("<ul>"),
            containsString("<li>" + warnings.get(0) + "</li>"),
            containsString("<li>" + warnings.get(1) + "</li>"),
            endsWith("</ul>")
        ));
    }

}
