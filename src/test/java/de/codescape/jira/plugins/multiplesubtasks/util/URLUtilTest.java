package de.codescape.jira.plugins.multiplesubtasks.util;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class URLUtilTest {

    @Test
    public void shouldAcceptFullQualifiedURLs() {
        assertThat(URLUtil.isValidURL("https://www.codescape.de"), is(equalTo(true)));
    }

    @Test
    public void shouldRejectNonValidURLs() {
        assertThat(URLUtil.isValidURL("stefan@codescape.de"), is(equalTo(false)));
    }

}
