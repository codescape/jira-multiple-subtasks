package de.codescape.jira.plugins.multiplesubtasks.action;

import de.codescape.jira.plugins.multiplesubtasks.model.ShowSubtaskTemplate;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.List;

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

    @Test
    public void shouldTransformCustomFields() {
        assertTransformation(
            "- Task / cfield:\"field:value\"",
            "- Task\n  customfield(field): value\n");
    }

    @Test
    public void shouldTransformMultipleCustomFields() {
        assertTransformation(
            "- Task / cfield:\"cf1:@inherit\" cfield:\"cf2:@inherit\"",
            "- Task\n  customfield(cf1): @inherit\n  customfield(cf2): @inherit\n"
        );
    }

    @Test
    public void shouldTransformAndEscapeCustomFields() {
        assertTransformation(
            "- Task / cfield:\"pet(s):@inherit\"",
            "- Task\n  customfield(pet\\(s\\)): @inherit\n"
        );
    }

    /* not supported syntax */

    @Test
    public void shouldIgnoreLinks() {
        assertTransformation(
            "- Task / links:\"blocks -> DEMO-1\"",
            "- Task\n");
    }

    /* accept syntax with dividing slashes */

    @Test
    public void shouldAcceptDividingSlashes() {
        assertTransformation(
            "- Task / estimate:\"6d\" / watcher:\"codescape\" / labels:\"one\"",
            "- Task\n  estimate: 6d\n  watcher: codescape\n  label: one\n"
        );
    }

    /* accept syntax with multiple spaces */

    @Test
    public void shouldAcceptAttributesWithMultipleSpaces() {
        assertTransformation(
            "  - Task  /     estimate:\"2h\"     labels:\"one\"   /   watcher:\"myself\" ",
            "- Task\n  estimate: 2h\n  label: one\n  watcher: myself\n"
        );
    }

    /* do not rely on case-sensitive attributes */

    @Test
    public void shouldNotEnforceCaseSensitiveAttributes() {
        assertTransformation("- Task / eStIMate:\"2h\" duedate:\"2022-02-02\" LABELS:\"aaa\"",
            "- Task\n  estimate: 2h\n  dueDate: 2022-02-02\n  label: aaa\n");
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

    /* fix: do not fail on rounded brackets in summary */

    @Test
    public void shouldAcceptRoundedBracketsInSummary() {
        assertTransformation("- a test (with rounded brackets)",
            "- a test (with rounded brackets)\n");
    }

    /* fix: do not fail on colon or question mark in summary */

    @Test
    public void shouldAcceptColonAndQuestionMarkInSummary() {
        assertTransformation("- a test: will it work?",
            "- a test: will it work?\n");
    }

    /* fix: do not fail on uncommon characters */

    @Test
    public void shouldAcceptUncommonLetters() {
        String input = "<list>\n" +
            "      <subtaskTemplate>\n" +
            "        <text>- Serve a drink in the Café / assignee:&quot;bartender&quot;</text>\n" +
            "        <title>drinking</title>\n" +
            "        <id>84634E96-7E97-4490-903E-6C159E3CE590</id>\n" +
            "      </subtaskTemplate>\n" +
            "    </list>";
        List<ShowSubtaskTemplate> templates = subtaskTemplateImportAction.extractQuickSubtasksTemplatesFromXml(input, true);
        assertThat(templates.size(), is(equalTo(1)));
        assertThat(templates.get(0).getTemplate(), containsString("Serve a drink in the Café"));
    }

    /* helper methods */

    private void assertTransformation(String input, String output) {
        String result = subtaskTemplateImportAction.transformQuickSubtasksTemplate(input);
        assertThat(result, is(equalTo(output)));
    }

}
