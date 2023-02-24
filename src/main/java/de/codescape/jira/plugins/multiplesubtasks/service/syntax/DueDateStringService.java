package de.codescape.jira.plugins.multiplesubtasks.service.syntax;

import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.regex.Pattern;

/**
 * This services parses a given due date string and returns the {@link Timestamp} for a valid pattern. Otherwise, it
 * returns a value of <code>null</code>.
 */
@Component
public class DueDateStringService {

    /**
     * This pattern describes the valid format to provide a due date.
     */
    public static final Pattern PATTERN = Pattern.compile("(?<year>\\d{4})-(?<month>0?[1-9]|1[012])-(?<day>0?[1-9]|[12][0-9]|3[01])");

    /**
     * Return the timestamp for the provided due date.
     *
     * @param dueDate input string
     * @return Timestamp representation
     */
    public Timestamp dueDateStringToTimestamp(String dueDate) {
        if (PATTERN.matcher(dueDate).matches()) {
            try {
                return Timestamp.valueOf(dueDate + " 00:00:00");
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        return null;
    }

}
