package de.codescape.jira.plugins.multiplesubtasks;

import de.codescape.jira.plugins.multiplesubtasks.ao.SubtaskConfig;
import de.codescape.jira.plugins.multiplesubtasks.ao.SubtaskTemplate;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.DatabaseUpdater;

/**
 * Database updater used for testing Active Object implementation.
 */
public class MultipleSubtasksDatabaseUpdater implements DatabaseUpdater {

    @Override
    @SuppressWarnings("unchecked")
    public void update(EntityManager entityManager) throws Exception {
        entityManager.migrate(SubtaskTemplate.class, SubtaskConfig.class);
    }

}
