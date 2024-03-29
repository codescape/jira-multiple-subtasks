package de.codescape.jira.plugins.multiplesubtasks.model;

import com.google.common.collect.ArrayListMultimap;
import org.junit.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.codescape.jira.plugins.multiplesubtasks.model.Markers.INHERIT_MARKER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class SubtaskTest {

    /* general */

    @Test(expected = SyntaxFormatException.class)
    public void shouldRejectUnknownAttributes() {
        ArrayListMultimap<String, String> map = ArrayListMultimap.create();
        map.put("unknown-attribute", "some value");
        new Subtask(map);
    }

    @Test
    public void shouldAcceptAllKnownAttributes() {
        ArrayListMultimap<String, String> map = ArrayListMultimap.create();
        Subtask.Attributes.ALL.forEach(key -> map.put(key, "value"));
        map.removeAll(Subtask.Attributes.ESTIMATE);
        map.put(Subtask.Attributes.ESTIMATE, "4d");
        map.removeAll(Subtask.Attributes.DUE_DATE);
        map.put(Subtask.Attributes.DUE_DATE, "2023-02-11");
        Subtask subTask = new Subtask(map);
        assertThat(subTask.getSummary(), is(equalTo("value")));
    }

    /* summary */

    @Test(expected = SyntaxFormatException.class)
    public void shouldRejectMultipleSummariesForOneTask() {
        ArrayListMultimap<String, String> map = ArrayListMultimap.create();
        map.put(Subtask.Attributes.SUMMARY, "First summary!");
        map.put(Subtask.Attributes.SUMMARY, "Second summary");
        new Subtask(map);
    }

    @Test(expected = SyntaxFormatException.class)
    public void shouldRejectEmptySummary() {
        ArrayListMultimap<String, String> map = ArrayListMultimap.create();
        map.put(Subtask.Attributes.ASSIGNEE, "Stefan");
        new Subtask(map);
    }

    @Test(expected = SyntaxFormatException.class)
    public void shouldRejectSummaryWithMoreThan255Characters() {
        ArrayListMultimap<String, String> map = ArrayListMultimap.create();
        map.put(Subtask.Attributes.SUMMARY, characters(256));
        new Subtask(map);
    }

    @Test
    public void shouldAcceptSummaryWith255Characters() {
        ArrayListMultimap<String, String> map = ArrayListMultimap.create();
        map.put(Subtask.Attributes.SUMMARY, characters(255));
        Subtask subtask = new Subtask(map);
        assertThat(subtask.getSummary().length(), is(255));
    }

    /* description */

    @Test
    public void shouldAcceptTaskWithDescription() {
        ArrayListMultimap<String, String> map = ArrayListMultimap.create();
        map.put(Subtask.Attributes.SUMMARY, "This task has a description");
        String expectedDescription = "This is the description of the task.";
        map.put(Subtask.Attributes.DESCRIPTION, expectedDescription);
        Subtask subtask = new Subtask(map);
        assertThat(subtask.getDescription(), is(equalTo(expectedDescription)));
    }

    /* priority */

    @Test
    public void shouldAcceptTaskWithPriority() {
        ArrayListMultimap<String, String> map = ArrayListMultimap.create();
        map.put(Subtask.Attributes.SUMMARY, "This task has a priority");
        String expectedPriority = "Highest";
        map.put(Subtask.Attributes.PRIORITY, expectedPriority);
        Subtask subtask = new Subtask(map);
        assertThat(subtask.getPriority(), is(equalTo(expectedPriority)));
    }

    @Test
    public void shouldAcceptTaskWithPriorityInherited() {
        ArrayListMultimap<String, String> map = ArrayListMultimap.create();
        map.put(Subtask.Attributes.SUMMARY, "This task has a priority");
        map.put(Subtask.Attributes.PRIORITY, INHERIT_MARKER);
        Subtask subtask = new Subtask(map);
        assertThat(subtask.getPriority(), is(equalTo(INHERIT_MARKER)));
    }

    /* label */

    @Test(expected = SyntaxFormatException.class)
    public void shouldRejectLabelsWithMoreThan255Characters() {
        ArrayListMultimap<String, String> map = ArrayListMultimap.create();
        map.put(Subtask.Attributes.SUMMARY, "This task has two labels and one is too long.");
        map.put(Subtask.Attributes.LABEL, characters(118));
        map.put(Subtask.Attributes.LABEL, characters(256));
        new Subtask(map);
    }

    @Test(expected = SyntaxFormatException.class)
    public void shouldRejectLabelsWithWhitespaceCharacters() {
        ArrayListMultimap<String, String> map = ArrayListMultimap.create();
        map.put(Subtask.Attributes.SUMMARY, "This task has two labels and one has whitespace in it.");
        map.put(Subtask.Attributes.LABEL, "this-is-ok");
        map.put(Subtask.Attributes.LABEL, "this is not ok");
        new Subtask(map);
    }

    @Test
    public void shouldAcceptLabelsWithLessThan255Characters() {
        ArrayListMultimap<String, String> map = ArrayListMultimap.create();
        map.put(Subtask.Attributes.SUMMARY, "This task has two long but valid labels.");
        map.put(Subtask.Attributes.LABEL, characters(118));
        map.put(Subtask.Attributes.LABEL, characters(255));
        Subtask subtask = new Subtask(map);
        assertThat(subtask.getLabels(), hasSize(2));
    }

    @Test
    public void shouldAcceptMultipleLabels() {
        ArrayListMultimap<String, String> map = ArrayListMultimap.create();
        map.put(Subtask.Attributes.SUMMARY, "This task has two valid labels.");
        map.put(Subtask.Attributes.LABEL, "first-label");
        map.put(Subtask.Attributes.LABEL, "second-label");
        Subtask subtask = new Subtask(map);
        assertThat(subtask.getLabels(), hasSize(2));
        assertThat(subtask.getLabels(), hasItems("first-label", "second-label"));
    }

    /* estimate */

    @Test
    public void shouldAcceptValidEstimate() {
        ArrayListMultimap<String, String> map = ArrayListMultimap.create();
        map.put(Subtask.Attributes.SUMMARY, "This task has a valid estimate!");
        map.put(Subtask.Attributes.ESTIMATE, "1h 42m");
        Subtask subtask = new Subtask(map);
        assertThat(subtask.getEstimate(), is(equalTo("1h 42m")));
    }

    @Test(expected = SyntaxFormatException.class)
    public void shouldRejectInvalidEstimate() {
        ArrayListMultimap<String, String> map = ArrayListMultimap.create();
        map.put(Subtask.Attributes.SUMMARY, "This task has an invalid estimate!");
        map.put(Subtask.Attributes.ESTIMATE, "9p");
        new Subtask(map);
    }

    @Test
    public void shouldAcceptInheritingTheEstimateFromParentIssue() {
        ArrayListMultimap<String, String> map = ArrayListMultimap.create();
        map.put(Subtask.Attributes.SUMMARY, "This task inherits the estimate!");
        map.put(Subtask.Attributes.ESTIMATE, INHERIT_MARKER);
        Subtask subtask = new Subtask(map);
        assertThat(subtask.getEstimate(), is(equalTo(INHERIT_MARKER)));
    }

    /* component */

    @Test
    public void shouldAcceptSingleComponent() {
        ArrayListMultimap<String, String> map = ArrayListMultimap.create();
        map.put(Subtask.Attributes.SUMMARY, "This task has a single component");
        map.put(Subtask.Attributes.COMPONENT, "backend");
        Subtask subtask = new Subtask(map);
        assertThat(subtask.getComponents(), hasSize(1));
        assertThat(subtask.getComponents(), hasItem("backend"));
    }

    @Test
    public void shouldAcceptMultipleComponents() {
        ArrayListMultimap<String, String> map = ArrayListMultimap.create();
        map.put(Subtask.Attributes.SUMMARY, "This task has a single component");
        map.put(Subtask.Attributes.COMPONENT, "backend");
        map.put(Subtask.Attributes.COMPONENT, "frontend");
        map.put(Subtask.Attributes.COMPONENT, "design");
        Subtask subtask = new Subtask(map);
        assertThat(subtask.getComponents(), hasSize(3));
        assertThat(subtask.getComponents(), hasItems("backend", "frontend", "design"));
    }

    /* watcher */

    @Test
    public void shouldAcceptSingleWatcher() {
        ArrayListMultimap<String, String> map = ArrayListMultimap.create();
        map.put(Subtask.Attributes.SUMMARY, "This task has a single watcher");
        map.put(Subtask.Attributes.WATCHER, "curious");
        Subtask subtask = new Subtask(map);
        assertThat(subtask.getWatchers(), hasSize(1));
        assertThat(subtask.getWatchers(), hasItem("curious"));
    }

    @Test
    public void shouldAcceptMultipleWatchers() {
        ArrayListMultimap<String, String> map = ArrayListMultimap.create();
        map.put(Subtask.Attributes.SUMMARY, "This task has a two watchers");
        map.put(Subtask.Attributes.WATCHER, "curious");
        map.put(Subtask.Attributes.WATCHER, "nervous");
        Subtask subtask = new Subtask(map);
        assertThat(subtask.getWatchers(), hasSize(2));
        assertThat(subtask.getWatchers(), hasItems("curious", "nervous"));
    }

    /* affectedVersion */

    @Test
    public void shouldAcceptSingleAffectedVersion() {
        ArrayListMultimap<String, String> map = ArrayListMultimap.create();
        map.put(Subtask.Attributes.SUMMARY, "This task has a single affectedVersion");
        map.put(Subtask.Attributes.AFFECTED_VERSION, "1.0");
        Subtask subtask = new Subtask(map);
        assertThat(subtask.getAffectedVersions(), hasSize(1));
        assertThat(subtask.getAffectedVersions(), hasItem("1.0"));
    }

    @Test
    public void shouldAcceptMultipleAffectedVersions() {
        ArrayListMultimap<String, String> map = ArrayListMultimap.create();
        map.put(Subtask.Attributes.SUMMARY, "This task has a multiple affectedVersions");
        map.put(Subtask.Attributes.AFFECTED_VERSION, "1.0");
        map.put(Subtask.Attributes.AFFECTED_VERSION, "2.0");
        Subtask subtask = new Subtask(map);
        assertThat(subtask.getAffectedVersions(), hasSize(2));
        assertThat(subtask.getAffectedVersions(), hasItems("1.0", "2.0"));
    }

    /* fixVersion */

    @Test
    public void shouldAcceptSingleFixVersion() {
        ArrayListMultimap<String, String> map = ArrayListMultimap.create();
        map.put(Subtask.Attributes.SUMMARY, "This task has a single fixVersion");
        map.put(Subtask.Attributes.FIX_VERSION, "1.1");
        Subtask subtask = new Subtask(map);
        assertThat(subtask.getFixVersions(), hasSize(1));
        assertThat(subtask.getFixVersions(), hasItem("1.1"));
    }

    @Test
    public void shouldAcceptMultipleFixVersions() {
        ArrayListMultimap<String, String> map = ArrayListMultimap.create();
        map.put(Subtask.Attributes.SUMMARY, "This task has a multiple fixVersions");
        map.put(Subtask.Attributes.FIX_VERSION, "1.1");
        map.put(Subtask.Attributes.FIX_VERSION, "2.1");
        Subtask subtask = new Subtask(map);
        assertThat(subtask.getFixVersions(), hasSize(2));
        assertThat(subtask.getFixVersions(), hasItems("1.1", "2.1"));
    }

    /* dueDate */

    @Test
    public void shouldAcceptSingleValidDueDate() {
        ArrayListMultimap<String, String> map = ArrayListMultimap.create();
        map.put(Subtask.Attributes.SUMMARY, "This task has a due date.");
        map.put(Subtask.Attributes.DUE_DATE, "2022-08-01");
        Subtask subtask = new Subtask(map);
        assertThat(subtask.getDueDate(), is(equalTo("2022-08-01")));
    }

    @Test
    public void shouldAcceptDueDateToBeInherited() {
        ArrayListMultimap<String, String> map = ArrayListMultimap.create();
        map.put(Subtask.Attributes.SUMMARY, "This task has a due date from parent.");
        map.put(Subtask.Attributes.DUE_DATE, "@inherit");
        Subtask subtask = new Subtask(map);
        assertThat(subtask.getDueDate(), is(equalTo("@inherit")));
    }

    @Test(expected = SyntaxFormatException.class)
    public void shouldRejectInvalidDueDate() {
        ArrayListMultimap<String, String> map = ArrayListMultimap.create();
        map.put(Subtask.Attributes.SUMMARY, "This task has an invalid due date.");
        map.put(Subtask.Attributes.DUE_DATE, "22/08/01");
        new Subtask(map);
    }

    /* custom fields by id */

    @Test(expected = SyntaxFormatException.class)
    public void shouldRejectIllegalCustomFieldsbyId() {
        ArrayListMultimap<String, String> map = ArrayListMultimap.create();
        map.put(Subtask.Attributes.SUMMARY, "This task has an illegal custom field");
        map.put("customfield_abcde", "value");
        new Subtask(map);
    }

    @Test
    public void shouldAcceptMultipleCustomFields() {
        ArrayListMultimap<String, String> map = ArrayListMultimap.create();
        map.put(Subtask.Attributes.SUMMARY, "This task has multiple custom fields");
        map.put("customfield_12345", "12345");
        map.put("customfield_10009", "value");
        Subtask subtask = new Subtask(map);
        assertThat(subtask.getCustomFieldsById().keySet(), hasSize(2));
        assertThat(subtask.getCustomFieldsById().get("customfield_12345"), hasSize(1));
        assertThat(subtask.getCustomFieldsById().get("customfield_12345"), hasItem("12345"));
        assertThat(subtask.getCustomFieldsById().get("customfield_10009"), hasSize(1));
        assertThat(subtask.getCustomFieldsById().get("customfield_10009"), hasItem("value"));
    }

    @Test
    public void shouldAcceptMultipleValuesForCustomFields() {
        ArrayListMultimap<String, String> map = ArrayListMultimap.create();
        map.put(Subtask.Attributes.SUMMARY, "This task has an illegal custom field");
        map.put("customfield_10000", "first value");
        map.put("customfield_10000", "second value");
        Subtask subtask = new Subtask(map);
        assertThat(subtask.getCustomFieldsById().keySet(), hasSize(1));
        assertThat(subtask.getCustomFieldsById().get("customfield_10000"), hasSize(2));
        assertThat(subtask.getCustomFieldsById().get("customfield_10000"), hasItems("first value", "second value"));
    }

    /* custom fields by name */

    @Test(expected = SyntaxFormatException.class)
    public void shouldRejectIllegalCustomFieldsByName() {
        ArrayListMultimap<String, String> map = ArrayListMultimap.create();
        map.put(Subtask.Attributes.SUMMARY, "This task has an illegal custom field");
        map.put("customfield()", "value");
        new Subtask(map);
    }

    @Test
    public void shouldAcceptMultipleCustomFieldsByName() {
        ArrayListMultimap<String, String> map = ArrayListMultimap.create();
        map.put(Subtask.Attributes.SUMMARY, "This task has multiple custom fields");
        map.put("customfield(field name)", "field value");
        map.put("customfield(Revenue)", "900");
        Subtask subtask = new Subtask(map);
        assertThat(subtask.getCustomFieldsByName().keySet(), hasSize(2));
        assertThat(subtask.getCustomFieldsByName().get("field name"), hasSize(1));
        assertThat(subtask.getCustomFieldsByName().get("field name"), hasItem("field value"));
        assertThat(subtask.getCustomFieldsByName().get("Revenue"), hasSize(1));
        assertThat(subtask.getCustomFieldsByName().get("Revenue"), hasItem("900"));
    }

    @Test
    public void shouldAcceptCustomFieldsByNameContainingEscapedRoundBracketsInKey() {
        ArrayListMultimap<String, String> map = ArrayListMultimap.create();
        map.put(Subtask.Attributes.SUMMARY, "This task has tricky custom field names");
        map.put("customfield(parent(s\\))", "father");
        map.put("customfield(parent(s\\))", "mother");
        map.put("customfield(pet\\(s\\))", "dog");
        map.put("customfield(pet\\(s\\))", "cat");
        Subtask subtask = new Subtask(map);
        assertThat(subtask.getCustomFieldsByName().keySet(), hasSize(2));
        assertThat(subtask.getCustomFieldsByName().get("parent(s)"), hasSize(2));
        assertThat(subtask.getCustomFieldsByName().get("parent(s)"), hasItems("father", "mother"));
        assertThat(subtask.getCustomFieldsByName().get("pet(s)"), hasSize(2));
        assertThat(subtask.getCustomFieldsByName().get("pet(s)"), hasItems("dog", "cat"));
    }

    @Test
    public void shouldAcceptCustomFieldsIgnoringLeadingAndTrailingWhitespace() {
        ArrayListMultimap<String, String> map = ArrayListMultimap.create();
        map.put(Subtask.Attributes.SUMMARY, "This task has a whitespace custom field");
        map.put("customfield( Whitespace   )", "Hello World");
        Subtask subtask = new Subtask(map);
        assertThat(subtask.getCustomFieldsByName().keySet(), hasSize(1));
        assertThat(subtask.getCustomFieldsByName().get("Whitespace"), hasSize(1));
        assertThat(subtask.getCustomFieldsByName().get("Whitespace"), hasItem("Hello World"));
    }

    /* helper methods */

    private String characters(int length) {
        return Stream.generate(() -> String.valueOf('*')).limit(length).collect(Collectors.joining());
    }

}
