package de.codescape.jira.plugins.multiplesubtasks.service;

import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.regex.Pattern;

@Component
public class DueDateStringService {

    /**
     * This pattern describes the valid format to provide a due date.
     */
    public static final Pattern PATTERN = Pattern.compile("(?<year>\\d{4})-(?<month>0?[1-9]|1[012])-(?<day>0?[1-9]|[12][0-9]|3[01])");

    private static final String MATCHER_YEAR = "year";
    private static final String MATCHER_MONTH = "month";
    private static final String MATCHER_DAY = "day";

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
