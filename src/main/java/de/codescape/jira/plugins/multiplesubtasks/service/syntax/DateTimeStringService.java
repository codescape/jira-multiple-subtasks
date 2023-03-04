package de.codescape.jira.plugins.multiplesubtasks.service.syntax;

import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.regex.Pattern;

/**
 * This services parses a given date and time string and returns the {@link Timestamp} for a valid pattern. Otherwise,
 * it returns a value of <code>null</code>.
 */
@Component
public class DateTimeStringService {

    /**
     * This pattern describes the valid format to provide a date.
     */
    public static final Pattern DATE_PATTERN = Pattern.compile("(?<year>\\d{4})-(?<month>0?[1-9]|1[012])-(?<day>0?[1-9]|[12][0-9]|3[01])");

    /**
     * This pattern describes the valid format to provide a date with optional time.
     */
    public static final Pattern DATE_TIME_PATTERN = Pattern.compile("(?<year>\\d{4})-(?<month>0?[1-9]|1[012])-(?<day>0?[1-9]|[12][0-9]|3[01]) (?<hours>0?[1-9]|1[0-9]|2[0-4]):(?<minutes>[0-5][0-9]|60)");

    /**
     * Return the timestamp for the provided date.
     *
     * @param dateString input string
     * @return Timestamp representation
     */
    public Timestamp dateStringToTimestamp(String dateString) {
        if (DATE_PATTERN.matcher(dateString).matches()) {
            try {
                return Timestamp.valueOf(dateString + " 00:00:00");
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * Return the timestamp for a provided date and time. The time portion is optional.
     *
     * @param dateAndTimeString input string
     * @return representation
     */
    public Timestamp dateAndTimeStringToTimestamp(String dateAndTimeString) {
        Timestamp timestamp = dateStringToTimestamp(dateAndTimeString);
        if (timestamp != null) {
            return timestamp;
        } else {
            if (DATE_TIME_PATTERN.matcher(dateAndTimeString).matches()) {
                try {
                    return Timestamp.valueOf(dateAndTimeString + ":00");
                } catch (IllegalArgumentException e) {
                    return null;
                }
            }
        }
        return null;
    }

}
