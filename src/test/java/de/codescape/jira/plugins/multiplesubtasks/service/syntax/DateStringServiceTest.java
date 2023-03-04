package de.codescape.jira.plugins.multiplesubtasks.service.syntax;

import org.junit.Before;
import org.junit.Test;

import java.sql.Timestamp;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class DateStringServiceTest {

    private DateStringService dateStringService;

    @Before
    public void before() {
        dateStringService = new DateStringService();
    }

    @Test
    public void returnTimestampForValidDueDate() {
        String dueDate = "2023-02-23";
        Timestamp timestamp = dateStringService.dateStringToTimestamp(dueDate);
        assertThat(timestamp.toString(), is(equalTo("2023-02-23 00:00:00.0")));
    }

    @Test
    public void returnTimestampForShortValidDueDate() {
        String dueDate = "2023-1-4";
        Timestamp timestamp = dateStringService.dateStringToTimestamp(dueDate);
        assertThat(timestamp.toString(), is(equalTo("2023-01-04 00:00:00.0")));
    }

    @Test
    public void returnNullForInvalidDueDate() {
        String dueDate = "2011-13-23";
        Timestamp timestamp = dateStringService.dateStringToTimestamp(dueDate);
        assertThat(timestamp, is(nullValue()));
    }

}
