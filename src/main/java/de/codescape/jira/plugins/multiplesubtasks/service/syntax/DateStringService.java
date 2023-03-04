package de.codescape.jira.plugins.multiplesubtasks.service.syntax;

import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.regex.Pattern;

/**
 * This services parses a given date string and returns the {@link Timestamp} for a valid pattern. Otherwise, it returns
 * a value of <code>null</code>.
 */
@Component
public class DateStringService {

    /**
     * This pattern describes the valid format to provide a date.
     */
    public static final Pattern PATTERN = Pattern.compile("(?<year>\\d{4})-(?<month>0?[1-9]|1[012])-(?<day>0?[1-9]|[12][0-9]|3[01])");

    /**
     * Return the timestamp for the provided date.
     *
     * @param dateString input string
     * @return Timestamp representation
     */
    public Timestamp dateStringToTimestamp(String dateString) {
        if (PATTERN.matcher(dateString).matches()) {
            try {
                return Timestamp.valueOf(dateString + " 00:00:00");
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        return null;
    }

}
