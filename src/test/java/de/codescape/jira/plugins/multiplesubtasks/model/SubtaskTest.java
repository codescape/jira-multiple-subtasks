package de.codescape.jira.plugins.multiplesubtasks.model;

import com.google.common.collect.ArrayListMultimap;
import org.junit.Test;
import org.junit.platform.commons.util.StringUtils;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

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
        new Subtask(map);
    }

    /* estimate */

    @Test
    public void shouldAcceptValidEstimate() {
        ArrayListMultimap<String, String> map = ArrayListMultimap.create();
        map.put(Subtask.Attributes.SUMMARY, "This task has an estimate!");
        map.put(Subtask.Attributes.ESTIMATE, "1h 42m");
        Subtask subtask = new Subtask(map);
        assertThat(subtask.getEstimate(), is(equalTo("1h 42m")));
    }

    /* helper methods */

    private String characters(int length) {
        return Stream.generate(() -> String.valueOf('*')).limit(length).collect(Collectors.joining());
    }

}
