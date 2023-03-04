package de.codescape.jira.plugins.multiplesubtasks.service.syntax;

import org.junit.Before;
import org.junit.Test;

import java.sql.Timestamp;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class DateTimeStringServiceTest {

    private DateTimeStringService dateTimeStringService;

    @Before
    public void before() {
        dateTimeStringService = new DateTimeStringService();
    }

    /* dateStringToTimestamp */

    @Test
    public void returnTimestampForValidDate() {
        String date = "2023-02-23";
        Timestamp timestamp = dateTimeStringService.dateStringToTimestamp(date);
        assertThat(timestamp.toString(), is(equalTo("2023-02-23 00:00:00.0")));
    }

    @Test
    public void returnTimestampForShortValidDate() {
        String date = "2023-1-4";
        Timestamp timestamp = dateTimeStringService.dateStringToTimestamp(date);
        assertThat(timestamp.toString(), is(equalTo("2023-01-04 00:00:00.0")));
    }

    @Test
    public void returnNullForInvalidDate() {
        String date = "2011-13-23";
        Timestamp timestamp = dateTimeStringService.dateStringToTimestamp(date);
        assertThat(timestamp, is(nullValue()));
    }

    /* dateAndTimeStringToTimestamp */

    @Test
    public void returnDateAndTimeTimestampForValidDate() {
        String date = "2023-02-23";
        Timestamp timestamp = dateTimeStringService.dateAndTimeStringToTimestamp(date);
        assertThat(timestamp.toString(), is(equalTo("2023-02-23 00:00:00.0")));
    }

    @Test
    public void returnDateAndTimeTimestampForValidDateAndTime() {
        String date = "2023-05-09 21:45";
        Timestamp timestamp = dateTimeStringService.dateAndTimeStringToTimestamp(date);
        assertThat(timestamp.toString(), is(equalTo("2023-05-09 21:45:00.0")));
    }

    @Test
    public void returnNullForInvalidDateAndTime() {
        String date = "2011-11-23 61:09";
        Timestamp timestamp = dateTimeStringService.dateAndTimeStringToTimestamp(date);
        assertThat(timestamp, is(nullValue()));
    }

}
