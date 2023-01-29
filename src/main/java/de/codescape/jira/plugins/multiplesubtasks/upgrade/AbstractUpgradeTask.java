package de.codescape.jira.plugins.multiplesubtasks.upgrade;

import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.upgrade.PluginUpgradeTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Collection;

import static de.codescape.jira.plugins.multiplesubtasks.MultipleSubtasksConstants.MULTIPLE_SUBTASKS_PLUGIN_KEY;

/**
 * Base class to be used when implementing {@link PluginUpgradeTask}.
 */
abstract class AbstractUpgradeTask implements PluginUpgradeTask {

    private static final Logger log = LoggerFactory.getLogger(AbstractUpgradeTask.class);

    @Override
    public final Collection<Message> doUpgrade() {
        log.info("Upgrade to build #{} with task {} started", getBuildNumber(), getClass().getSimpleName());
        performUpgrade();
        log.info("Upgrade to build #{} with task {} finished", getBuildNumber(), getClass().getSimpleName());
        return null;
    }

    /**
     * Implementation of the upgrade task.
     */
    protected abstract void performUpgrade();

    @Override
    @Nonnull
    public final String getPluginKey() {
        return MULTIPLE_SUBTASKS_PLUGIN_KEY;
    }

}
