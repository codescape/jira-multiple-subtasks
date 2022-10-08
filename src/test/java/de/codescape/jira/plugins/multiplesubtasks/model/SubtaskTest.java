package de.codescape.jira.plugins.multiplesubtasks.model;

import com.google.common.collect.ArrayListMultimap;
import org.junit.Test;

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

}
