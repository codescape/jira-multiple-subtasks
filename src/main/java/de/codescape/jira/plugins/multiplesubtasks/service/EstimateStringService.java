package de.codescape.jira.plugins.multiplesubtasks.service;

import com.atlassian.jira.bc.issue.worklog.TimeTrackingConfiguration;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class EstimateStringService {

    public static final Pattern PATTERN = Pattern.compile("((?<weeks>[0-9]+)w)? ?((?<days>[0-9]+)d)? ?((?<hours>[0-9]+)h)? ?((?<minutes>[0-9]+)m)?");

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
        Long weeks = matcher.group("weeks") != null ? Long.parseLong(matcher.group("weeks")) : 0;
        Long days = matcher.group("days") != null ? Long.parseLong(matcher.group("days")) : 0;
        Long hours = matcher.group("hours") != null ? Long.parseLong(matcher.group("hours")) : 0;
        Long minutes = matcher.group("minutes") != null ? Long.parseLong(matcher.group("minutes")) : 0;
        return minutesInSeconds(minutes) + hoursInSeconds(hours) + daysInSeconds(days) + weeksInSeconds(weeks);
    }

    /* helper methods */

    private long weeksInSeconds(Long weeks) {
        return daysInSeconds(weeks * timeTrackingConfiguration.getDaysPerWeek().longValue());
    }

    private long daysInSeconds(Long days) {
        return hoursInSeconds(days * timeTrackingConfiguration.getHoursPerDay().longValue());
    }

    private static long hoursInSeconds(Long hours) {
        return minutesInSeconds(hours * 60);
    }

    private static long minutesInSeconds(Long minutes) {
        return minutes * 60;
    }

}
