package de.codescape.jira.plugins.multiplesubtasks.model;

import com.google.common.collect.ArrayListMultimap;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class SubTaskTest {

    @Test(expected = SyntaxFormatException.class)
    public void summaryMustNotOccurMultipleTimes() {
        ArrayListMultimap<String, String> map = ArrayListMultimap.create();
        map.put(SubTask.Attributes.SUMMARY, "First summary!");
        map.put(SubTask.Attributes.SUMMARY, "Second summary");
        new SubTask(map);
    }

    @Test(expected = SyntaxFormatException.class)
    public void shouldRejectUnknownAttributes() {
        ArrayListMultimap<String, String> map = ArrayListMultimap.create();
        map.put("unknown-attribute", "some value");
        new SubTask(map);
    }

    // TODO: improve test case and check all attributes individually
    @Test
    public void shouldAcceptAllKnownAttributes() {
        ArrayListMultimap<String, String> map = ArrayListMultimap.create();
        SubTask.Attributes.ALL.forEach(key -> map.put(key, "value"));
        SubTask subTask = new SubTask(map);
        assertThat(subTask.getSummary(), is(equalTo("value")));
    }

}
