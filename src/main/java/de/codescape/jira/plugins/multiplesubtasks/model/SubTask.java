package de.codescape.jira.plugins.multiplesubtasks.model;

import java.util.Map;

/**
 * This class represents the request to create a new subtask with the given attributes.
 */
public class SubTask {

    private final Map<String, String> attributes;

    /**
     * Create a subtask with the provided attributes.
     *
     * @param attributes map of attributes
     */
    public SubTask(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    /**
     * Return the summary of the subtask.
     */
    public String getSummary() {
        return attributes.get("summary");
    }

    /**
     * Return the optional assignee of the subtask.
     */
    public String getAssignee() {
        return attributes.get("assignee");
    }

    /**
     * Return the optional priority of the subtask.
     */
    public String getPriority() {
        return attributes.get("priority");
    }

}
