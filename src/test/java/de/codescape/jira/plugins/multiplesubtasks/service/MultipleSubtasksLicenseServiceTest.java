package de.codescape.jira.plugins.multiplesubtasks.service;


import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.PluginInformation;
import com.atlassian.upm.api.license.PluginLicenseManager;
import com.atlassian.upm.api.license.entity.LicenseError;
import com.atlassian.upm.api.license.entity.PluginLicense;
import com.atlassian.upm.api.util.Option;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.HashMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MultipleSubtasksLicenseServiceTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    private PluginAccessor pluginAccessor;

    @Mock
    private PluginLicenseManager pluginLicenseManager;

    @InjectMocks
    private MultipleSubtasksLicenseService service;

    @Mock
    private Plugin plugin;

    @Mock
    private PluginInformation pluginInformation;

    @Mock
    private PluginLicense pluginLicense;

    @Test
    public void shouldReturnValidLicenseIfLicensingIsDisabled() {
        pluginLicensingEnabled(false);

        assertThat(service.hasValidLicense(), is(true));
    }

    @Test
    public void shouldReturnInvalidLicenseForLicensingEnabledButNoLicenseProvided() {
        pluginLicensingEnabled(true);
        noPluginLicenseExists();

        assertThat(service.hasValidLicense(), is(false));
    }

    @Test
    public void shouldReturnValidLicenseForLicensingEnabledWithLicenseWithoutErrors() {
        pluginLicensingEnabled(true);
        pluginLicenseWithoutErrors();

        assertThat(service.hasValidLicense(), is(true));
    }

    @Test
    public void shouldReturnInvalidLicenseForLicensingEnabledWithLicenseWithErrors() {
        pluginLicensingEnabled(true);
        pluginLicenseWithErrors();

        assertThat(service.hasValidLicense(), is(false));
    }

    /* helper methods */

    private void pluginLicenseWithErrors() {
        Option<PluginLicense> pluginLicenses = Option.option(pluginLicense);
        when(pluginLicenseManager.getLicense()).thenReturn(pluginLicenses);
        when(pluginLicense.getError()).thenReturn(Option.option(LicenseError.EXPIRED));
    }

    private void pluginLicenseWithoutErrors() {
        Option<PluginLicense> pluginLicenses = Option.option(pluginLicense);
        when(pluginLicenseManager.getLicense()).thenReturn(pluginLicenses);
        when(pluginLicense.getError()).thenReturn(Option.none());
    }

    private void noPluginLicenseExists() {
        when(pluginLicenseManager.getLicense()).thenReturn(Option.none());
    }

    private void pluginLicensingEnabled(boolean licensingEnabled) {
        when(pluginLicenseManager.getPluginKey()).thenReturn("multiple-subtasks");
        when(pluginAccessor.getPlugin(eq("multiple-subtasks"))).thenReturn(plugin);
        when(plugin.getPluginInformation()).thenReturn(pluginInformation);
        HashMap<String, String> parameters = mock(HashMap.class);
        when(pluginInformation.getParameters()).thenReturn(parameters);
        when(parameters.get(eq("atlassian-licensing-enabled"))).thenReturn(String.valueOf(licensingEnabled));
    }

}
