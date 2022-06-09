package de.codescape.jira.plugins.multiplesubtasks.model;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.List;

/**
 * This class represents the request to create a new subtask with the given attributes.
 */
public class SubTask {

    private final String summary;
    private final String description;
    private final String assignee;
    private final String priority;
    private final String issueType;
    private final String reporter;
    private final List<String> labels;

    /**
     * Create a subtask with the provided attributes.
     *
     * @param attributes map of attributes
     */
    public SubTask(ArrayListMultimap<String, String> attributes) {
        summary = ensureSingleValue(attributes, "summary");
        description = ensureSingleValue(attributes, "description");
        assignee = ensureSingleValue(attributes, "assignee");
        priority = ensureSingleValue(attributes, "priority");
        issueType = ensureSingleValue(attributes, "issueType");
        reporter = ensureSingleValue(attributes, "reporter");
        labels = ensureValidLabels(attributes);
    }

    /**
     * Return the summary of the subtask.
     */
    public String getSummary() {
        return summary;
    }

    /**
     * Return the description of the subtask.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Return the optional assignee of the subtask.
     */
    public String getAssignee() {
        return assignee;
    }

    /**
     * Return the optional priority of the subtask.
     */
    public String getPriority() {
        return priority;
    }

    /**
     * Return the optional issue type of the subtask.
     */
    public String getIssueType() {
        return issueType;
    }

    /**
     * Return the optional reporter of the subtask.
     */
    public String getReporter() {
        return reporter;
    }

    /**
     * Return the optional labels of the subtask.
     */
    public List<String> getLabels() {
        return labels;
    }

    private String ensureSingleValue(ArrayListMultimap<String, String> attributes, String key) {
        List<String> valuesForKey = attributes.get(key);
        if (valuesForKey.size() > 1) {
            throw new SyntaxFormatException("Attribute " + key + " may only be used once per task.");
        }
        return valuesForKey.size() == 0 ? null : valuesForKey.get(0);
    }

    private List<String> ensureValidLabels(ArrayListMultimap<String, String> attributes) {
        List<String> values = attributes.get("label");
        if (values.stream().anyMatch(s -> s.contains(" "))) {
            throw new SyntaxFormatException("Labels must not contain spaces.");
        }
        return values;
    }

}
