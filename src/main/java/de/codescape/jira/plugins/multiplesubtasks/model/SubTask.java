package de.codescape.jira.plugins.multiplesubtasks.model;

import java.util.Map;

// TODO document class
public class SubTask {

    private final Map<String, String> attributes;

    public SubTask(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public String getSummary() {
        return attributes.get("summary");
    }

    public String getAssignee() {
        return attributes.get("assignee");
    }

    public String getPriority() {
        return attributes.get("priority");
    }

}
