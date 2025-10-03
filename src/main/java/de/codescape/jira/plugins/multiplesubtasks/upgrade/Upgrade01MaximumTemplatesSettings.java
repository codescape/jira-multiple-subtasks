package de.codescape.jira.plugins.multiplesubtasks.upgrade;

import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.sal.api.upgrade.PluginUpgradeTask;
import de.codescape.jira.plugins.multiplesubtasks.service.MultipleSubtasksConfigurationService;
import jakarta.annotation.Nonnull;
import jakarta.inject.Inject;
import org.springframework.stereotype.Component;

/**
 * Initialize configuration with default settings for maximum templates per user and per project.
 *
 * @since 23.1.0
 */
@Component
@ExportAsService(PluginUpgradeTask.class)
public class Upgrade01MaximumTemplatesSettings extends AbstractUpgradeTask {

    private final MultipleSubtasksConfigurationService multipleSubtasksConfigurationService;

    @Inject
    public Upgrade01MaximumTemplatesSettings(MultipleSubtasksConfigurationService multipleSubtasksConfigurationService) {
        this.multipleSubtasksConfigurationService = multipleSubtasksConfigurationService;
    }

    @Override
    public int getBuildNumber() {
        return 1;
    }

    @Nonnull
    @Override
    public String getShortDescription() {
        return "Set default maximum templates for user and project";
    }

    @Override
    protected void performUpgrade() {
        multipleSubtasksConfigurationService.set(MultipleSubtasksConfigurationService.TEMPLATES_PER_USER, "10");
        multipleSubtasksConfigurationService.set(MultipleSubtasksConfigurationService.TEMPLATES_PER_PROJECT, "10");
    }

}
