package de.codescape.jira.plugins.multiplesubtasks.upgrade;

import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.sal.api.upgrade.PluginUpgradeTask;
import de.codescape.jira.plugins.multiplesubtasks.model.TemplateSortOrder;
import de.codescape.jira.plugins.multiplesubtasks.service.MultipleSubtasksConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

/**
 * Initialize configuration with default settings for templates sort order.
 *
 * @since 23.09.1
 */
@Component
@ExportAsService(PluginUpgradeTask.class)
public class Upgrade02TemplatesSortOrder extends AbstractUpgradeTask {

    private final MultipleSubtasksConfigurationService multipleSubtasksConfigurationService;

    @Autowired
    public Upgrade02TemplatesSortOrder(MultipleSubtasksConfigurationService multipleSubtasksConfigurationService) {
        this.multipleSubtasksConfigurationService = multipleSubtasksConfigurationService;
    }

    @Override
    public int getBuildNumber() {
        return 2;
    }

    @Nonnull
    @Override
    public String getShortDescription() {
        return "Set default templates sort order";
    }

    @Override
    protected void performUpgrade() {
        multipleSubtasksConfigurationService.set(MultipleSubtasksConfigurationService.TEMPLATES_SORT_ORDER, TemplateSortOrder.NAME_ASC.name());
    }

}
