package de.codescape.jira.plugins.multiplesubtasks.service;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.upm.api.license.PluginLicenseManager;
import com.atlassian.upm.api.license.entity.PluginLicense;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * License service to check for the existence of a valid license to use the plugin.
 */
@Component
public class MultipleSubtasksLicenseService {

    private final PluginLicenseManager pluginLicenseManager;

    @Autowired
    public MultipleSubtasksLicenseService(@ComponentImport PluginLicenseManager pluginLicenseManager) {
        this.pluginLicenseManager = pluginLicenseManager;
    }

    public boolean hasValidLicense() {
        if (pluginLicenseManager.getLicense().isDefined()) {
            PluginLicense license = pluginLicenseManager.getLicense().get();
            return !license.getError().isDefined();
        } else {
            return false;
        }
    }

}
