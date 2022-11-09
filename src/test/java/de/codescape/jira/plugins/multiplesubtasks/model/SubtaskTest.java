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

    @Test(expected = SyntaxFormatException.class)
    public void summaryMustNotOccurMultipleTimes() {
        ArrayListMultimap<String, String> map = ArrayListMultimap.create();
        map.put(Subtask.Attributes.SUMMARY, "First summary!");
        map.put(Subtask.Attributes.SUMMARY, "Second summary");
        new Subtask(map);
    }

    @Test(expected = SyntaxFormatException.class)
    public void shouldRejectUnknownAttributes() {
        ArrayListMultimap<String, String> map = ArrayListMultimap.create();
        map.put("unknown-attribute", "some value");
        new Subtask(map);
    }

    // TODO: improve test case and check all attributes individually
    @Test
    public void shouldAcceptAllKnownAttributes() {
        ArrayListMultimap<String, String> map = ArrayListMultimap.create();
        Subtask.Attributes.ALL.forEach(key -> map.put(key, "value"));
        Subtask subTask = new Subtask(map);
        assertThat(subTask.getSummary(), is(equalTo("value")));
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

    private String characters(int length) {
        return Stream.generate(() -> String.valueOf('*')).limit(length).collect(Collectors.joining());
    }

}
