package de.codescape.jira.plugins.multiplesubtasks.service.syntax;

import org.junit.Before;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class DateTimeStringServiceTest {

    private DateTimeStringService dateTimeStringService;

    @Before
    public void before() {
        dateTimeStringService = new DateTimeStringService();
    }

    /* dateStringToTimestamp with absolute dates */

    @Test
    public void returnDateForValidDate() {
        String date = "2023-02-23";
        Timestamp timestamp = dateTimeStringService.dateStringToTimestamp(date, null);
        assertThat(timestamp.toString(), is(equalTo("2023-02-23 00:00:00.0")));
    }

    @Test
    public void returnDateForShortValidDate() {
        String date = "2023-1-4";
        Timestamp timestamp = dateTimeStringService.dateStringToTimestamp(date, null);
        assertThat(timestamp.toString(), is(equalTo("2023-01-04 00:00:00.0")));
    }

    @Test
    public void returnNoDateForInvalidDate() {
        String date = "2011-13-23";
        Timestamp timestamp = dateTimeStringService.dateStringToTimestamp(date, null);
        assertThat(timestamp, is(nullValue()));
    }

    /* dateStringToTimestamp with relative dates */

    @Test
    public void returnDateForRelativeDatePlusFiveDays() {
        Timestamp timestamp = dateTimeStringService.dateStringToTimestamp("@now + 5d", null);
        assertThat(timestamp.toString(), is(equalTo(todayAsString(0, 0, 0, 5))));
    }

    @Test
    public void returnDateForRelativeDateMinusOneWeek() {
        String date = "@now-1w";
        Timestamp timestamp = dateTimeStringService.dateStringToTimestamp(date, null);
        assertThat(timestamp.toString(), is(equalTo(todayAsString(0, 0, -1, 0))));
    }

    @Test
    public void returnDateForRelativeDateWithComplexSyntax() {
        String date = "@now + 4d3w2m1y";
        Timestamp timestamp = dateTimeStringService.dateStringToTimestamp(date, null);
        assertThat(timestamp.toString(), is(equalTo(todayAsString(1, 2, 3, 4))));
    }

    @Test
    public void returnDateForRelativeDateWithInheritAndParentValue() {
        String date = "@inherit + 3d";
        Timestamp timestamp = dateTimeStringService.dateStringToTimestamp(date, new Timestamp(new Date().getTime()));
        assertThat(timestamp.toString(), is(equalTo(todayAsString(0, 0, 0, 3))));
    }

    @Test
    public void returnNoDateForRelativeDateWithInheritAndMissingParentValue() {
        String date = "@inherit + 3d";
        Timestamp timestamp = dateTimeStringService.dateStringToTimestamp(date, null);
        assertThat(timestamp, is(nullValue()));
    }

    @Test
    public void returnDateForRelativeDateNow() {
        Timestamp timestamp = dateTimeStringService.dateStringToTimestamp("@now", null);
        assertThat(timestamp.toString(), is(equalTo(todayAsString(0, 0, 0, 0))));
    }

    @Test
    public void returnDateForRelativeDateInherit() {
        Timestamp timestamp = dateTimeStringService.dateStringToTimestamp("@inherit", new Timestamp(new Date().getTime()));
        assertThat(timestamp.toString(), is(equalTo(todayAsString(0, 0, 0, 0))));
    }

    /* dateAndTimeStringToTimestamp with absolute dates */

    @Test
    public void returnDateAndTimeForValidDate() {
        String date = "2023-02-23";
        Timestamp timestamp = dateTimeStringService.dateAndTimeStringToTimestamp(date, null);
        assertThat(timestamp.toString(), is(equalTo("2023-02-23 00:00:00.0")));
    }

    @Test
    public void returnDateAndTimeForValidDateAndTime() {
        String date = "2023-05-09 21:45";
        Timestamp timestamp = dateTimeStringService.dateAndTimeStringToTimestamp(date, null);
        assertThat(timestamp.toString(), is(equalTo("2023-05-09 21:45:00.0")));
    }

    @Test
    public void returnNoDateAndTimeForInvalidDateAndTime() {
        String date = "2011-11-23 61:09";
        Timestamp timestamp = dateTimeStringService.dateAndTimeStringToTimestamp(date, null);
        assertThat(timestamp, is(nullValue()));
    }

    /* dateAndTimeStringToTimestamp with relative dates */

    @Test
    public void returnDateAndTimeForRelativeDatePlusTwoDays() {
        Timestamp timestamp = dateTimeStringService.dateAndTimeStringToTimestamp("@now + 5d", null);
        Timestamp expected = today(0, 0, 0, 5, true);
        // check that the difference is smaller than one second
        assertThat(Math.abs(timestamp.getTime() - expected.getTime()), is(lessThan(1000L)));
    }

    @Test
    public void returnDateAndTimeForRelativeDateWithInheritAndParentValue() {
        String date = "@inherit + 3d";
        Timestamp timestamp = dateTimeStringService.dateAndTimeStringToTimestamp(date, new Timestamp(new Date().getTime()));
        Timestamp expected = today(0, 0, 0, 3, true);
        // check that the difference is smaller than one second
        assertThat(Math.abs(timestamp.getTime() - expected.getTime()), is(lessThan(1000L)));
    }

    @Test
    public void returnNoDateAndTimeForRelativeDateWithInheritAndMissingParentValue() {
        String date = "@inherit + 3d";
        Timestamp timestamp = dateTimeStringService.dateAndTimeStringToTimestamp(date, null);
        assertThat(timestamp, is(nullValue()));
    }

    /* helpers */

    private String todayAsString(int addYears, int addMonths, int addWeeks, int addDays) {
        return today(addYears, addMonths, addWeeks, addDays, false).toString();
    }

    private Timestamp today(int addYears, int addMonths, int addWeeks, int addDays, boolean withTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.YEAR, addYears);
        calendar.add(Calendar.MONTH, addMonths);
        calendar.add(Calendar.WEEK_OF_YEAR, addWeeks);
        calendar.add(Calendar.DAY_OF_YEAR, addDays);
        if (!withTime) {
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
        }
        return new Timestamp(calendar.getTime().getTime());
    }

}
