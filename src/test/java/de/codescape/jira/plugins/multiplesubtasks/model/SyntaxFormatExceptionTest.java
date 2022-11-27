package de.codescape.jira.plugins.multiplesubtasks.model;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class SyntaxFormatExceptionTest {

    @Test
    public void exceptionWithMessageOnlyReturnsOriginalMessage() {
        SyntaxFormatException exception = new SyntaxFormatException("original message");
        assertThat(exception.getMessage(), is(equalTo("original message")));
        assertThat(exception.getCause(), is(nullValue()));
    }

    @Test
    public void exceptionWithMessageAndThrowableReturnsBoth() {
        RuntimeException original_cause = new RuntimeException();
        SyntaxFormatException exception = new SyntaxFormatException("original message", original_cause);
        assertThat(exception.getMessage(), is(equalTo("original message")));
        assertThat(exception.getCause(), is(equalTo(original_cause)));
    }

}
