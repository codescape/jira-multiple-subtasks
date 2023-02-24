package de.codescape.jira.plugins.multiplesubtasks.service.syntax;

import com.atlassian.jira.bc.issue.worklog.TimeTrackingConfiguration;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This services parses a given estimate string and returns the temporal estimate in seconds for a valid pattern.
 * Otherwise, it returns a value of zero.
 * <p/>
 * This service uses the information from {@link TimeTrackingConfiguration} that is configured in Jira which allows to
 * define the number of hours for a standard working day and the number of days for a standard working week.
 */
@Component
public class EstimateStringService {

    /**
     * This pattern describes the valid format to provide an estimate.
     */
    public static final Pattern PATTERN = Pattern.compile("((?<weeks>\\d+)w)? ?((?<days>\\d+)d)? ?((?<hours>\\d+)h)? ?((?<minutes>\\d+)m)?");

    private static final String MATCHER_WEEKS = "weeks";
    private static final String MATCHER_DAYS = "days";
    private static final String MATCHER_HOURS = "hours";
    private static final String MATCHER_MINUTES = "minutes";

    private final TimeTrackingConfiguration timeTrackingConfiguration;

    @Autowired
    public EstimateStringService(@ComponentImport TimeTrackingConfiguration timeTrackingConfiguration) {
        this.timeTrackingConfiguration = timeTrackingConfiguration;
    }

    /**
     * Returns the amount of seconds for a given estimate string that complies with the pattern.
     */
    public Long estimateStringToSeconds(String input) {
        Matcher matcher = PATTERN.matcher(input);
        if (!matcher.matches()) {
            return 0L;
        }
        Long weeks = matcher.group(MATCHER_WEEKS) != null ? Long.parseLong(matcher.group(MATCHER_WEEKS)) : 0;
        Long days = matcher.group(MATCHER_DAYS) != null ? Long.parseLong(matcher.group(MATCHER_DAYS)) : 0;
        Long hours = matcher.group(MATCHER_HOURS) != null ? Long.parseLong(matcher.group(MATCHER_HOURS)) : 0;
        Long minutes = matcher.group(MATCHER_MINUTES) != null ? Long.parseLong(matcher.group(MATCHER_MINUTES)) : 0;
        return minutesInSeconds(minutes) + hoursInSeconds(hours) + daysInSeconds(days) + weeksInSeconds(weeks);
    }

    /* helper methods */

    /**
     * One week has the configured number of working days per week.
     */
    private long weeksInSeconds(Long weeks) {
        return daysInSeconds(weeks * timeTrackingConfiguration.getDaysPerWeek().longValue());
    }

    /**
     * One day has the configured number of working hours per day.
     */
    private long daysInSeconds(Long days) {
        return hoursInSeconds(days * timeTrackingConfiguration.getHoursPerDay().longValue());
    }

    /**
     * One hour has 60 minutes.
     */
    private static long hoursInSeconds(Long hours) {
        return minutesInSeconds(hours * 60);
    }

    /**
     * One minute has 60 seconds.
     */
    private static long minutesInSeconds(Long minutes) {
        return minutes * 60;
    }

}
