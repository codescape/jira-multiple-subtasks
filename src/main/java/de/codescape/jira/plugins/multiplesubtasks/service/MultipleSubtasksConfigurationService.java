package de.codescape.jira.plugins.multiplesubtasks.service;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.tx.Transactional;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import de.codescape.jira.plugins.multiplesubtasks.ao.SubtaskConfig;
import net.java.ao.DBParam;
import net.java.ao.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Service class for configuration options.
 */
@Component
@Transactional
public class MultipleSubtasksConfigurationService {

    public static final String TEMPLATES_PER_USER = "TEMPLATES_PER_USER";
    public static final String TEMPLATES_PER_PROJECT = "TEMPLATES_PER_PROJECT";

    private final ActiveObjects activeObjects;

    @Autowired
    public MultipleSubtasksConfigurationService(@ComponentImport ActiveObjects activeObjects) {
        this.activeObjects = activeObjects;
    }

    /**
     * Get a list of all configuration properties.
     */
    public List<SubtaskConfig> get() {
        return Arrays.asList(activeObjects.find(SubtaskConfig.class));
    }

    /**
     * Get a configuration property or <code>null</code> if it does not exist.
     */
    public SubtaskConfig get(String key) {
        SubtaskConfig[] subtaskConfigs = activeObjects.find(SubtaskConfig.class, Query.select()
            .where("KEY = ?", key).limit(1));
        return subtaskConfigs.length == 0 ? null : subtaskConfigs[0];
    }

    /**
     * Set or update a given configuration property.
     */
    public SubtaskConfig set(String key, String value) {
        SubtaskConfig subtaskConfig = get(key);
        if (subtaskConfig == null) {
            subtaskConfig = activeObjects.create(SubtaskConfig.class, new DBParam("KEY", key));
        }
        subtaskConfig.setValue(value);
        subtaskConfig.save();
        return subtaskConfig;
    }

}
