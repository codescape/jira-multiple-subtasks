package de.codescape.jira.plugins.multiplesubtasks.model;

import com.google.common.collect.ArrayListMultimap;
import de.codescape.jira.plugins.multiplesubtasks.service.EstimateStringService;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;

/**
 * This class represents the request to create a new subtask with the given attributes.
 */
public class Subtask {

    /**
     * Container for all named attributes that are interpreted in subtask syntax.
     */
    static class Attributes {

        static final String SUMMARY = "summary";
        static final String DESCRIPTION = "description";
        static final String ASSIGNEE = "assignee";
        static final String PRIORITY = "priority";
        static final String ISSUE_TYPE = "issueType";
        static final String REPORTER = "reporter";
        static final String COMPONENT = "component";
        static final String LABEL = "label";
        static final String ESTIMATE = "estimate";

        static final List<String> ALL = Arrays.asList(SUMMARY, DESCRIPTION, ASSIGNEE, PRIORITY, ISSUE_TYPE, REPORTER, COMPONENT, LABEL, ESTIMATE);

    }

    private final String summary;
    private final String description;
    private final String assignee;
    private final String priority;
    private final String issueType;
    private final String reporter;
    private final List<String> labels;
    private final List<String> components;
    private final String estimate;

    /**
     * Create a subtask with the provided attributes.
     *
     * @param attributes map of attributes
     */
    public Subtask(ArrayListMultimap<String, String> attributes) {
        verifyOnlyKnownAttributes(attributes);
        summary = ensureValidSummary(attributes);
        description = ensureSingleValue(attributes, Attributes.DESCRIPTION);
        assignee = ensureSingleValue(attributes, Attributes.ASSIGNEE);
        priority = ensureSingleValue(attributes, Attributes.PRIORITY);
        issueType = ensureSingleValue(attributes, Attributes.ISSUE_TYPE);
        reporter = ensureSingleValue(attributes, Attributes.REPORTER);
        labels = ensureValidLabels(attributes);
        components = attributes.get(Attributes.COMPONENT);
        estimate = ensureValidEstimate(attributes);
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

    /**
     * Return the optional components of the subtask.
     */
    public List<String> getComponents() {
        return components;
    }

    /**
     * Return the optional estimate.
     */
    public String getEstimate() {
        return estimate;
    }

    /* internal helper methods */

    private String ensureSingleValue(ArrayListMultimap<String, String> attributes, String key) {
        List<String> values = attributes.get(key);
        if (values.size() > 1) {
            throw new SyntaxFormatException("Attribute " + key + " may only be used once per task.");
        }
        return values.isEmpty() ? null : values.get(0);
    }

    private List<String> ensureValidLabels(ArrayListMultimap<String, String> attributes) {
        List<String> values = attributes.get(Attributes.LABEL);
        if (values.stream().anyMatch(s -> s.contains(" "))) {
            throw new SyntaxFormatException("Labels must not contain spaces.");
        }
        if (values.stream().anyMatch(s -> s.length() > 255)) {
            throw new SyntaxFormatException("Labels must not be longer than 255 characters.");
        }
        return values;
    }

    private String ensureValidEstimate(ArrayListMultimap<String, String> attributes) {
        String estimate = ensureSingleValue(attributes, Attributes.ESTIMATE);
        if (estimate == null) {
            return null;
        }
        Matcher matcher = EstimateStringService.PATTERN.matcher(estimate);
        if (!matcher.matches()) {
            throw new SyntaxFormatException("Invalid pattern for estimate: " + estimate);
        }
        return estimate;
    }

    private void verifyOnlyKnownAttributes(ArrayListMultimap<String, String> attributes) {
        attributes.forEach((key, value) -> {
            if (!Attributes.ALL.contains(key))
                throw new SyntaxFormatException("Unknown attribute " + key + " found.");
        });
    }

    private String ensureValidSummary(ArrayListMultimap<String, String> attributes) {
        String summary = ensureSingleValue(attributes, Attributes.SUMMARY);
        // Summary is required
        if (summary == null) {
            throw new SyntaxFormatException("Summary for creation of a new task is mandatory.");
        }
        // Summary may not be longer than 255 characters
        if (summary.length() > 255) {
            throw new SyntaxFormatException("Summary (" + summary + ") exceeds allowed maximum length of 255 characters.");
        }
        return summary;
    }

}
