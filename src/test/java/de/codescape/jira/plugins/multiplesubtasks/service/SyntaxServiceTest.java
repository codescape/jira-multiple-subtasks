package de.codescape.jira.plugins.multiplesubtasks.service;

import de.codescape.jira.plugins.multiplesubtasks.model.SubTask;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class SyntaxServiceTest {

    private SyntaxService syntaxService;

    @Before
    public void before() {
        syntaxService = new SyntaxService();
    }

    @Test
    public void shouldCreateSubTaskFromWellFormedLine() {
        String singleSubTask = "- create a single sub-task";

        List<SubTask> subTasks = syntaxService.parseString(singleSubTask);
        assertThat(subTasks.size(), is(equalTo(1)));
        assertThat(subTasks.get(0).getSummary(), is(equalTo("create a single sub-task")));
    }

    @Test
    public void shouldCreateSubTaskFromLineWithMissingWhitespaceSeparator() {
        String singleSubTask = "-sub-task with missing whitespace-separator";

        List<SubTask> subTasks = syntaxService.parseString(singleSubTask);
        assertThat(subTasks.size(), is(equalTo(1)));
        assertThat(subTasks.get(0).getSummary(), is(equalTo("sub-task with missing whitespace-separator")));
    }

    @Test
    public void shouldCreateSubTaskFromLineWithLeadingWhitespace() {
        String singleSubTask = "   - sub-task with leading whitespace";

        List<SubTask> subTasks = syntaxService.parseString(singleSubTask);
        assertThat(subTasks.size(), is(equalTo(1)));
        assertThat(subTasks.get(0).getSummary(), is(equalTo("sub-task with leading whitespace")));
    }

}
