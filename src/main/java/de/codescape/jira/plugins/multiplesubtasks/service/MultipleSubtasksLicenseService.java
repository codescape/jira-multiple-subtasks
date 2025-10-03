package de.codescape.jira.plugins.multiplesubtasks.service;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.PluginInformation;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.upm.api.license.PluginLicenseManager;
import com.atlassian.upm.api.license.entity.PluginLicense;
import jakarta.inject.Inject;
import org.springframework.stereotype.Component;

/**
 * License service to check for the existence of a valid license to use the plugin.
 */
@Component
public class MultipleSubtasksLicenseService {

    private static final String ATLASSIAN_LICENSING_ENABLED = "atlassian-licensing-enabled";

    private final PluginLicenseManager pluginLicenseManager;
    private final PluginAccessor pluginAccessor;

    @Inject
    public MultipleSubtasksLicenseService(@ComponentImport PluginLicenseManager pluginLicenseManager,
                                          @ComponentImport PluginAccessor pluginAccessor) {
        this.pluginLicenseManager = pluginLicenseManager;
        this.pluginAccessor = pluginAccessor;
    }

    /**
     * Return whether this plugin uses a valid license or not.
     */
    public boolean hasValidLicense() {
        // if licensing is disabled we can ignore a further license check
        if (!isLicensingEnabled()) {
            return true;
        }
        // if license exists check whether there are errors
        if (pluginLicenseManager.getLicense().isDefined()) {
            PluginLicense license = pluginLicenseManager.getLicense().get();
            return !license.getError().isDefined();
        } else {
            return false;
        }
    }

    // check whether licensing is enabled for the plugin.
    private boolean isLicensingEnabled() {
        Plugin plugin = pluginAccessor.getPlugin(pluginLicenseManager.getPluginKey());
        PluginInformation pluginInformation = plugin.getPluginInformation();
        return Boolean.parseBoolean(pluginInformation.getParameters().get(ATLASSIAN_LICENSING_ENABLED));
    }

}
