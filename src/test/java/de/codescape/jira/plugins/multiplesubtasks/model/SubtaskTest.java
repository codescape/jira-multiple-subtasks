package de.codescape.jira.plugins.multiplesubtasks.model;

import com.google.common.collect.ArrayListMultimap;
import org.junit.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        Subtask subtask = new Subtask(map);
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

    /* custom fields */

    @Test(expected = SyntaxFormatException.class)
    public void shouldRejectIllegalCustomFields() {
        ArrayListMultimap<String, String> map = ArrayListMultimap.create();
        map.put(Subtask.Attributes.SUMMARY, "This task has an illegal custom field");
        map.put("customfield_abcde", "value");
        Subtask subtask = new Subtask(map);
    }

    @Test
    public void shouldAcceptMultipleCustomFields() {
        ArrayListMultimap<String, String> map = ArrayListMultimap.create();
        map.put(Subtask.Attributes.SUMMARY, "This task has an illegal custom field");
        map.put("customfield_12345", "12345");
        map.put("customfield_10009", "value");
        Subtask subtask = new Subtask(map);
        assertThat(subtask.getCustomFields().keySet(), hasSize(2));
        assertThat(subtask.getCustomFields().get("customfield_12345"), hasSize(1));
        assertThat(subtask.getCustomFields().get("customfield_12345"), hasItem("12345"));
        assertThat(subtask.getCustomFields().get("customfield_10009"), hasSize(1));
        assertThat(subtask.getCustomFields().get("customfield_10009"), hasItem("value"));
    }

    @Test
    public void shouldAcceptMultipleValuesForCustomFields() {
        ArrayListMultimap<String, String> map = ArrayListMultimap.create();
        map.put(Subtask.Attributes.SUMMARY, "This task has an illegal custom field");
        map.put("customfield_10000", "first value");
        map.put("customfield_10000", "second value");
        Subtask subtask = new Subtask(map);
        assertThat(subtask.getCustomFields().keySet(), hasSize(1));
        assertThat(subtask.getCustomFields().get("customfield_10000"), hasSize(2));
        assertThat(subtask.getCustomFields().get("customfield_10000"), hasItems("first value", "second value"));
    }

    /* helper methods */

    private String characters(int length) {
        return Stream.generate(() -> String.valueOf('*')).limit(length).collect(Collectors.joining());
    }

}
