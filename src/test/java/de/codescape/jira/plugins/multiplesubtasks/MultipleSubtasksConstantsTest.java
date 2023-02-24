package de.codescape.jira.plugins.multiplesubtasks;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class MultipleSubtasksConstantsTest {

    @Test
    public void exposesPluginKey() {
        assertThat(MultipleSubtasksConstants.MULTIPLE_SUBTASKS_PLUGIN_KEY, is(not(nullValue())));
    }

}
