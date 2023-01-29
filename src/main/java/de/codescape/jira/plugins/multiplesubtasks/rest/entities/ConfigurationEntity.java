package de.codescape.jira.plugins.multiplesubtasks.rest.entities;

import de.codescape.jira.plugins.multiplesubtasks.ao.SubtaskConfig;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * JSON entity to transport the configuration.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConfigurationEntity {

    @JsonProperty
    private final String key;

    @JsonProperty
    private final String value;

    public ConfigurationEntity(SubtaskConfig subtaskConfig) {
        this.key = subtaskConfig.getKey();
        this.value = subtaskConfig.getValue();
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

}
