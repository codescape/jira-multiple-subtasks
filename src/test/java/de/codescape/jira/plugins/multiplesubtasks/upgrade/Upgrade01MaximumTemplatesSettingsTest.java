package de.codescape.jira.plugins.multiplesubtasks.upgrade;

import de.codescape.jira.plugins.multiplesubtasks.service.MultipleSubtasksConfigurationService;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static de.codescape.jira.plugins.multiplesubtasks.service.MultipleSubtasksConfigurationService.TEMPLATES_PER_PROJECT;
import static de.codescape.jira.plugins.multiplesubtasks.service.MultipleSubtasksConfigurationService.TEMPLATES_PER_USER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class Upgrade01MaximumTemplatesSettingsTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    private MultipleSubtasksConfigurationService multipleSubtasksConfigurationService;

    @InjectMocks
    private Upgrade01MaximumTemplatesSettings upgrade;

    @Test
    public void shouldReturnCorrectBuildNumber() {
        assertThat(upgrade.getBuildNumber(), is(equalTo(1)));
    }

    @Test
    public void shouldReturnDescriptionLessThan50Characters() {
        assertThat(upgrade.getShortDescription(), is(notNullValue()));
        assertThat(upgrade.getShortDescription().length(), is(lessThanOrEqualTo(50)));
    }

    @Test
    public void shouldApplyTheDefaultsToBothConfigurationFields() {
        upgrade.performUpgrade();

        verify(multipleSubtasksConfigurationService, times(1)).set(eq(TEMPLATES_PER_USER), eq("10"));
        verify(multipleSubtasksConfigurationService, times(1)).set(eq(TEMPLATES_PER_PROJECT), eq("10"));
        verifyNoMoreInteractions(multipleSubtasksConfigurationService);
    }

}
