package de.codescape.jira.plugins.multiplesubtasks.action;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class SubtaskTemplateImportActionTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @InjectMocks
    private SubtaskTemplateImportAction subtaskTemplateImportAction;

    /* simple syntax */

    @Test
    public void shouldTransformOneSimpleTasks() {
        assertTransformation(
            "- Simple Task",
            "- Simple Task\n");
    }

    @Test
    public void shouldTransformTwoSimpleTasks() {
        assertTransformation(
            "- Test\n-Document",
            "- Test\n- Document\n");
    }

    @Test
    public void shouldIgnoreInvalidPartAfterSlash() {
        assertTransformation(
            "- Test / ignore invalid",
            "- Test\n");
    }

    @Test
    public void shouldIgnoreInvalidLines() {
        assertTransformation(
            "- a Task\nohhhh",
            "- a Task\n"
        );
    }

    /* advanced syntax */

    @Test
    public void shouldTransformPriority() {
        assertTransformation(
            "- Task with / priority:\"Highest\"",
            "- Task with\n  priority: Highest\n");
    }

    @Test
    public void shouldTransformDescription() {
        assertTransformation(
            "- Task with / description:\"A good description!\"",
            "- Task with\n  description: A good description!\n"
        );
    }

    @Test
    public void shouldTransformEstimate() {
        assertTransformation(
            "- Task with / estimate:\"6d\"",
            "- Task with\n  estimate: 6d\n"
        );
    }

    @Test
    public void shouldTransformAssignee() {
        assertTransformation(
            "- Task with / assignee:\"roger\"",
            "- Task with\n  assignee: roger\n"
        );
    }

    @Test
    public void shouldTransformDueDate() {
        assertTransformation(
            "- Task with / dueDate:\"2027-02-03\"",
            "- Task with\n  dueDate: 2027-02-03\n"
        );
    }

    @Test
    public void shouldTransformReporter() {
        assertTransformation(
            "- Task with / reporter:\"karla\"",
            "- Task with\n  reporter: karla\n"
        );
    }

    @Test
    public void shouldTransformIssueType() {
        assertTransformation(
            "- Task with / issueType:\"Testing\"",
            "- Task with\n  issueType: Testing\n"
        );
    }

    @Test
    public void shouldTransformFixversion() {
        assertTransformation(
            "- Task with / fixversion:\"2.0\"",
            "- Task with\n  fixVersion: 2.0\n"
        );
    }

    @Test
    public void shouldTransformAffectedversion() {
        assertTransformation(
            "- Task with / affectedversion:\"1.0\"",
            "- Task with\n  affectedVersion: 1.0\n"
        );
    }

    @Test
    public void shouldTransformComponent() {
        assertTransformation(
            "- Task with / component:\"backend,frontend\"",
            "- Task with\n  component: backend\n  component: frontend\n"
        );
    }

    @Test
    public void shouldTransformLabels() {
        assertTransformation(
            "- Task with / labels:\"one,two,three\"",
            "- Task with\n  label: one\n  label: two\n  label: three\n"
        );
    }

    @Test
    public void shouldTransformWatchers() {
        assertTransformation(
            "- Task / watcher:\"codescape\"",
            "- Task\n  watcher: codescape\n");
        assertTransformation(
            "- Task / watcher:\"codescape, bob\"",
            "- Task\n  watcher: codescape\n  watcher: bob\n");
    }

    /* not supported syntax */

    @Test
    public void shouldIgnoreLinks() {
        assertTransformation(
            "- Task / links:\"blocks -> DEMO-1\"",
            "- Task\n");
    }

    @Test
    public void shouldIgnoreCustomFields() {
        assertTransformation(
            "- Task / cfield:\"field:value\"",
            "- Task\n");
    }

    /* complex example */

    @Test
    public void shouldTransformTwoAttributes() {
        assertTransformation(
            "- Task / assignee:\"mrx\" priority:\"high\"",
            "- Task\n  assignee: mrx\n  priority: high\n"
        );
    }

    @Test
    public void shouldTransformComplexExample() {
        String complexExample = "- Implementation\n" +
            "- Write unit tests / estimate:\"3h\"\n" +
            "- Write Acceptance tests / estimate:\"6h\" labels:\"testing,atdd\" fixversion:\"1.5-alpha\"\n" +
            "- Code Review / assignee:\"michael\" component:\"testing,development\" affectedversion:\"preview-1\"\n" +
            "- End user documentation / description:\"Create it!\" estimate:\"4h\" assignee:\"michael\" priority:\"Critical\"\n" +
            "- Call back customer / issueType:\"CRM-Subtask\"\n" +
            "- Inform everyone / reporter:\"stefan\" watcher:\"rico, bob,admin\"\n" +
            "- Ignore advanced stuff / cfield:\"cf1:@inherit\" cfield:\"cf2:@inherit\" link:\"blocks -> ISSUE-1\"";
        String result = subtaskTemplateImportAction.transformQuickSubtasksTemplate(complexExample);
        assertThat(result, allOf(
            containsString("Implementation"),
            containsString("Write unit tests"),
            containsString("Write Acceptance tests"),
            containsString("Code Review"),
            containsString("End user documentation"),
            containsString("Call back customer"),
            containsString("Inform everyone"),
            containsString("Ignore advanced stuff")
        ));
    }

    /* helper methods */

    private void assertTransformation(String input, String output) {
        String result = subtaskTemplateImportAction.transformQuickSubtasksTemplate(input);
        assertThat(result, is(equalTo(output)));
    }

}
