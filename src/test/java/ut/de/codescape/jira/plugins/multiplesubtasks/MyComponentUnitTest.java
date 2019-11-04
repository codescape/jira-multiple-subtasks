package ut.de.codescape.jira.plugins.multiplesubtasks;

import org.junit.Test;
import de.codescape.jira.plugins.multiplesubtasks.api.MyPluginComponent;
import de.codescape.jira.plugins.multiplesubtasks.impl.MyPluginComponentImpl;

import static org.junit.Assert.assertEquals;

public class MyComponentUnitTest
{
    @Test
    public void testMyName()
    {
        MyPluginComponent component = new MyPluginComponentImpl(null);
        assertEquals("names do not match!", "myComponent",component.getName());
    }
}