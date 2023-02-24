package de.codescape.jira.plugins.multiplesubtasks.service.syntax;

import com.atlassian.jira.bc.issue.worklog.TimeTrackingConfiguration;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

public class EstimateStringServiceTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    private TimeTrackingConfiguration timeTrackingConfiguration;

    @InjectMocks
    private EstimateStringService estimateStringService;

    @Before
    public void before() {
        when(timeTrackingConfiguration.getDaysPerWeek()).thenReturn(new BigDecimal("5"));
        when(timeTrackingConfiguration.getHoursPerDay()).thenReturn(new BigDecimal("8"));
    }

    /* invalid patterns */

    @Test
    public void shouldReturn0ForInvalidPattern() {
        assertThat(estimateStringService.estimateStringToSeconds("invalid"), is(equalTo(0L)));
    }

    /* simple patterns */

    @Test
    public void shouldAcceptPatternForWeeks() {
        assertThat(estimateStringService.estimateStringToSeconds("3w"), is(equalTo(432000L)));
    }

    @Test
    public void shouldAcceptPatternForDays() {
        assertThat(estimateStringService.estimateStringToSeconds("2d"), is(equalTo(57600L)));
    }

    @Test
    public void shouldAcceptPatternForHours() {
        assertThat(estimateStringService.estimateStringToSeconds("3h"), is(equalTo(10800L)));
    }

    @Test
    public void shouldAcceptPatternForMinutes() {
        assertThat(estimateStringService.estimateStringToSeconds("5m"), is(equalTo(300L)));
    }

    /* complex patterns */

    @Test
    public void shouldAcceptPatternForMinutesAndHours() {
        assertThat(estimateStringService.estimateStringToSeconds("1h 5m"), is(equalTo(3900L)));
    }

    @Test
    public void shouldAcceptComplexPatternWithSpaces() {
        assertThat(estimateStringService.estimateStringToSeconds("1w 1d 1h 1m"), is(equalTo(176460L)));
    }

    @Test
    public void shouldAcceptComplexPatternWithoutSpaces() {
        assertThat(estimateStringService.estimateStringToSeconds("1w1d1h1m"), is(equalTo(176460L)));
    }

}
