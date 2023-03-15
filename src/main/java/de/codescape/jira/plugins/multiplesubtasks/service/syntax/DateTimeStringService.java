package de.codescape.jira.plugins.multiplesubtasks.service.syntax;

import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.regex.Matcher;
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
     * This pattern describes the valid format to provide a relative date.
     */
    public static final Pattern RELATIVE_DATE_PATTERN = Pattern.compile("(?<base>@now|@inherit)(?:\\s*(?<operand>[+\\-])\\s*(?:(?<days>\\d+)d)?\\s*(?:(?<weeks>\\d+)w)?\\s*(?:(?<months>\\d+)m)?\\s*(?:(?<years>\\d+)y)?)?");

    /**
     * This pattern describes the valid format to provide a date with optional time.
     */
    public static final Pattern DATE_TIME_PATTERN = Pattern.compile("(?<year>\\d{4})-(?<month>0?[1-9]|1[012])-(?<day>0?[1-9]|[12][0-9]|3[01]) (?<hours>0?[1-9]|1[0-9]|2[0-4]):(?<minutes>[0-5][0-9]|60)");

    /* named matcher groups */

    private static final String MATCHER_BASE = "base";
    private static final String MATCHER_OPERAND = "operand";
    private static final String MATCHER_YEARS = "years";
    private static final String MATCHER_MONTHS = "months";
    private static final String MATCHER_WEEKS = "weeks";
    private static final String MATCHER_DAYS = "days";

    /* relative base values */

    private static final String RELATIVE_TO_NOW = "@now";
    private static final String RELATIVE_TO_PARENT = "@inherit";

    /**
     * Return the timestamp for the provided date.
     *
     * @param dateString input string
     * @param parentDate date of the parent issue
     * @return Timestamp representation
     */
    public Timestamp dateStringToTimestamp(String dateString, Timestamp parentDate) {
        Matcher relativeDateMatcher = RELATIVE_DATE_PATTERN.matcher(dateString);
        if (relativeDateMatcher.matches()) {
            return calculateRelativeDateTime(relativeDateMatcher, parentDate, false);
        } else if (DATE_PATTERN.matcher(dateString).matches()) {
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
     * @param parentDate        date of the parent issue
     * @return representation
     */
    public Timestamp dateAndTimeStringToTimestamp(String dateAndTimeString, Timestamp parentDate) {
        Matcher relativeDateMatcher = RELATIVE_DATE_PATTERN.matcher(dateAndTimeString);
        if (relativeDateMatcher.matches()) {
            return calculateRelativeDateTime(relativeDateMatcher, parentDate, true);
        } else {
            Timestamp timestamp = dateStringToTimestamp(dateAndTimeString, parentDate);
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
        }
        return null;
    }

    /**
     * Calculate the relative date and time or otherwise return <code>null</code>.
     */
    private static Timestamp calculateRelativeDateTime(Matcher matcher, Timestamp parentDate, boolean withTime) {
        // extract the data from the matcher
        String base = matcher.group(MATCHER_BASE);
        String operand = matcher.group(MATCHER_OPERAND);
        boolean addition = "+".equals(operand);
        int years = matcher.group(MATCHER_YEARS) != null ? Integer.parseInt(matcher.group(MATCHER_YEARS)) : 0;
        int months = matcher.group(MATCHER_MONTHS) != null ? Integer.parseInt(matcher.group(MATCHER_MONTHS)) : 0;
        int weeks = matcher.group(MATCHER_WEEKS) != null ? Integer.parseInt(matcher.group(MATCHER_WEEKS)) : 0;
        int days = matcher.group(MATCHER_DAYS) != null ? Integer.parseInt(matcher.group(MATCHER_DAYS)) : 0;

        // calculate the base
        Timestamp date;
        switch (base) {
            case RELATIVE_TO_NOW:
                date = new Timestamp(System.currentTimeMillis());
                break;
            case RELATIVE_TO_PARENT:
                // no calculation possible if parent has no date
                if (parentDate == null) {
                    return null;
                }
                date = parentDate;
                break;
            default:
                return null;
        }

        // subtract and add the relative date information
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        if (years > 0) {
            calendar.add(Calendar.YEAR, years * (addition ? 1 : -1));
        }
        if (months > 0) {
            calendar.add(Calendar.MONTH, months * (addition ? 1 : -1));
        }
        if (weeks > 0) {
            calendar.add(Calendar.WEEK_OF_YEAR, weeks * (addition ? 1 : -1));
        }
        if (days > 0) {
            calendar.add(Calendar.DAY_OF_YEAR, days * (addition ? 1 : -1));
        }

        // remove or keep the time portion?
        if (!withTime) {
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
        }

        return new Timestamp(calendar.getTime().getTime());
    }

}
