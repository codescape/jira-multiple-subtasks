package de.codescape.jira.plugins.multiplesubtasks.util;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Utility class for URL related stuff.
 */
public class URLUtil {

    /**
     * Verify the provided URL string is a valid URL to be persisted to a custom field of type URL.
     */
    public static boolean isValidURL(String url) {
        try {
            //noinspection ResultOfMethodCallIgnored
            new URI(url).toURL();
            return true;
        } catch (MalformedURLException | URISyntaxException | IllegalArgumentException e) {
            return false;
        }
    }

}
