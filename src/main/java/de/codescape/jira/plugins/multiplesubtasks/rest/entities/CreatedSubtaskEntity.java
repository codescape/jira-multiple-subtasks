package de.codescape.jira.plugins.multiplesubtasks.rest.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.codescape.jira.plugins.multiplesubtasks.model.CreatedSubtask;

import java.util.List;

/**
 * JSON entity to transport the created subtask.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({"issueKey", "issueSummary", "warnings"})
public class CreatedSubtaskEntity {

    private final CreatedSubtask createdSubtask;

    public CreatedSubtaskEntity(CreatedSubtask createdSubtask) {
        this.createdSubtask = createdSubtask;
    }

    @JsonProperty
    public String getIssueKey() {
        return createdSubtask.getIssue().getKey();
    }

    @JsonProperty
    public String getIssueSummary() {
        return createdSubtask.getIssue().getSummary();
    }

    @JsonProperty
    public List<String> getWarnings() {
        return createdSubtask.getWarnings();
    }

}
