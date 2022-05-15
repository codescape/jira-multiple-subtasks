package de.codescape.jira.plugins.multiplesubtasks.service;

import de.codescape.jira.plugins.multiplesubtasks.model.SubTask;
import de.codescape.jira.plugins.multiplesubtasks.model.SyntaxFormatException;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class SubTasksSyntaxServiceTest {

    private SubTasksSyntaxService subtasksSyntaxService;

    @Before
    public void before() {
        subtasksSyntaxService = new SubTasksSyntaxService();
    }

    // positive tests

    @Test
    public void shouldCreateSubTaskFromWellFormedLine() {
        String singleSubTask = "- create a single sub-task";

        List<SubTask> subTasks = subtasksSyntaxService.parseString(singleSubTask);
        assertThat(subTasks.size(), is(equalTo(1)));
        assertThat(subTasks.get(0).getSummary(), is(equalTo("create a single sub-task")));
    }

    @Test
    public void shouldCreateSubTaskFromLineWithMissingWhitespaceSeparator() {
        String singleSubTask = "-sub-task with missing whitespace-separator";

        List<SubTask> subTasks = subtasksSyntaxService.parseString(singleSubTask);
        assertThat(subTasks.size(), is(equalTo(1)));
        assertThat(subTasks.get(0).getSummary(), is(equalTo("sub-task with missing whitespace-separator")));
    }

    @Test
    public void shouldCreateSubTaskFromLineWithLeadingWhitespace() {
        String singleSubTask = "   - sub-task with leading whitespace";

        List<SubTask> subTasks = subtasksSyntaxService.parseString(singleSubTask);
        assertThat(subTasks.size(), is(equalTo(1)));
        assertThat(subTasks.get(0).getSummary(), is(equalTo("sub-task with leading whitespace")));
    }

    @Test
    public void shouldParseComplexCombinationOfSubTasks() {
        String complexCombinationOfSubTasks = "- ein Task ohne weitere Details\n" +
            "- ein Task mit einem Bearbeiter\n" +
            "  assignee: codescape\n" +
            "- ein Task mit Bearbeiter und Priorität\n" +
            "  assignee: codescape\n" +
            "  priority: 3\n" +
            "     \n" +
            "- ein Task mit etwas vielen Leerzeichen am Ende    \n" +
            "-       ein Task mit etwas vielen Leerzeichen am Anfang\n" +
            "- ein Task mit - Zeichen im Text\n" +
            " - ein Task mit Leerzeichen vor dem - Zeichen im Text";
        List<SubTask> subTasks = subtasksSyntaxService.parseString(complexCombinationOfSubTasks);
        assertThat(subTasks.size(), is(equalTo(7)));
    }

    @Test
    public void shouldCreateSubTaskIncludingAssignee() {
        String subTaskWithAssignee = "- sub-task with assignee\n" +
            "  assignee: codescape";

        List<SubTask> subTasks = subtasksSyntaxService.parseString(subTaskWithAssignee);
        assertThat(subTasks.size(), is(equalTo(1)));
        assertThat(subTasks.get(0).getSummary(), is(equalTo("sub-task with assignee")));
        assertThat(subTasks.get(0).getAssignee(), is(equalTo("codescape")));
    }

    @Test
    public void shouldCreateSubTaskIncludingPriority() {
        String subTaskWithAssignee = "- sub-task with priority\n" +
            "  priority: Critical";

        List<SubTask> subTasks = subtasksSyntaxService.parseString(subTaskWithAssignee);
        assertThat(subTasks.size(), is(equalTo(1)));
        assertThat(subTasks.get(0).getSummary(), is(equalTo("sub-task with priority")));
        assertThat(subTasks.get(0).getPriority(), is(equalTo("Critical")));
    }

    @Test
    public void shouldCreateSubTaskFromSummaryWithColon() {
        String subTaskWithColon = "- developer: implement logic";

        List<SubTask> subTasks = subtasksSyntaxService.parseString(subTaskWithColon);
        assertThat(subTasks.size(), is(equalTo(1)));
        assertThat(subTasks.get(0).getSummary(), is(equalTo("developer: implement logic")));
    }

    @Test
    public void shouldCreateSubTaskFromSummaryWithMultipleColons() {
        String subTaskWithMultipleColons = "- developer: implement logic: some details";

        List<SubTask> subTasks = subtasksSyntaxService.parseString(subTaskWithMultipleColons);
        assertThat(subTasks.size(), is(equalTo(1)));
        assertThat(subTasks.get(0).getSummary(), is(equalTo("developer: implement logic: some details")));
    }

    // negative tests

    @Test(expected = SyntaxFormatException.class)
    public void shouldRejectASingleLineOfText() {
        subtasksSyntaxService.parseString("ein Task?");
    }

    @Test(expected = SyntaxFormatException.class)
    public void shouldRejectMultipleLinesOfText() {
        subtasksSyntaxService.parseString("ein Task?\nund noch mehr Text?");
    }

    @Test(expected = SyntaxFormatException.class)
    public void shouldRejectMultipleLinesOfTextNotStartingWithATaskInFirstLine() {
        subtasksSyntaxService.parseString("kein Task\n- und jetzt ein Task");
    }

    @Test(expected = SyntaxFormatException.class)
    public void shouldRejectTaskWithAttributesThatAreNotKeyValueAttributes() {
        subtasksSyntaxService.parseString("- Ein Task mit Attribut ohne Wert\n" +
            "  assignee");
    }

}
