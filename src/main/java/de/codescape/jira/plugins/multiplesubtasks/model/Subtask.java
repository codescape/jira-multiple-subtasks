package de.codescape.jira.plugins.multiplesubtasks.model;

import com.google.common.collect.ArrayListMultimap;
import de.codescape.jira.plugins.multiplesubtasks.service.syntax.DateTimeStringService;
import de.codescape.jira.plugins.multiplesubtasks.service.syntax.EstimateStringService;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * This class represents the request to create a new subtask with the given attributes.
 */
public class Subtask {

    /**
     * Container for all named attributes that are interpreted in subtask syntax.
     */
    public static class Attributes {

        public static final String SUMMARY = "summary";
        public static final String DESCRIPTION = "description";
        public static final String ASSIGNEE = "assignee";
        public static final String PRIORITY = "priority";
        public static final String ISSUE_TYPE = "issueType";
        public static final String REPORTER = "reporter";
        public static final String COMPONENT = "component";
        public static final String LABEL = "label";
        public static final String ESTIMATE = "estimate";
        public static final String WATCHER = "watcher";
        public static final String FIX_VERSION = "fixVersion";
        public static final String AFFECTED_VERSION = "affectedVersion";
        public static final String DUE_DATE = "dueDate";

        static final List<String> ALL = Arrays.asList(
            SUMMARY, DESCRIPTION, ASSIGNEE, PRIORITY, ISSUE_TYPE, REPORTER, COMPONENT, LABEL, ESTIMATE, WATCHER,
            FIX_VERSION, AFFECTED_VERSION, DUE_DATE
        );

    }

    // custom fields can be referenced by id: customfield_12345:
    private static final Pattern CUSTOM_FIELD_ID_PATTERN = Pattern.compile("customfield_\\d{5}");

    // custom fields can be reference by name: customfield(fieldname):
    private static final Pattern CUSTOM_FIELD_NAME_PATTERN = Pattern.compile("customfield\\((?<name>(?:\\\\\\\\|\\\\\\)|[^)])++)\\)");

    private final String summary;
    private final String description;
    private final String assignee;
    private final String priority;
    private final String issueType;
    private final String reporter;
    private final List<String> labels;
    private final List<String> components;
    private final String estimate;
    private final List<String> watchers;
    private final List<String> fixVersions;
    private final List<String> affectedVersions;
    private final String dueDate;
    private final Map<String, List<String>> customFieldsById;
    private final Map<String, List<String>> customFieldsByName;

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
        watchers = attributes.get(Attributes.WATCHER);
        fixVersions = attributes.get(Attributes.FIX_VERSION);
        affectedVersions = attributes.get(Attributes.AFFECTED_VERSION);
        dueDate = ensureValidDueDate(attributes);
        customFieldsById = extractCustomFieldsById(attributes);
        customFieldsByName = extractCustomFieldsByName(attributes);
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

    /**
     * Return the optional watchers of the subtask.
     */
    public List<String> getWatchers() {
        return watchers;
    }

    /**
     * Return the optional fixVersions of the subtask.
     */
    public List<String> getFixVersions() {
        return fixVersions;
    }

    /**
     * Return the optional affectedVersions of the subtask.
     */
    public List<String> getAffectedVersions() {
        return affectedVersions;
    }

    /**
     * Return the optional dueDate of the subtask.
     */
    public String getDueDate() {
        return dueDate;
    }

    /**
     * Return the optional custom fields by id.
     */
    public Map<String, List<String>> getCustomFieldsById() {
        return customFieldsById;
    }

    /**
     * Return the optional custom fields by name.
     */
    public Map<String, List<String>> getCustomFieldsByName() {
        return customFieldsByName;
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
            if (!Attributes.ALL.contains(key) && !CUSTOM_FIELD_ID_PATTERN.matcher(key).matches()
                && !CUSTOM_FIELD_NAME_PATTERN.matcher(key).matches())
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

    private String ensureValidDueDate(ArrayListMultimap<String, String> attributes) {
        String dueDate = ensureSingleValue(attributes, Attributes.DUE_DATE);
        if (dueDate == null) {
            return null;
        }
        Matcher absoluteDateMatcher = DateTimeStringService.DATE_PATTERN.matcher(dueDate);
        Matcher relativeDateMatcher = DateTimeStringService.RELATIVE_DATE_PATTERN.matcher(dueDate);
        if (!absoluteDateMatcher.matches() && !relativeDateMatcher.matches()) {
            throw new SyntaxFormatException("Invalid pattern for dueDate: " + dueDate);
        }
        return dueDate;
    }

    private Map<String, List<String>> extractCustomFieldsById(ArrayListMultimap<String, String> attributes) {
        // get all custom field keys
        List<String> customFieldKeys = attributes.keySet().stream()
            .filter(s -> CUSTOM_FIELD_ID_PATTERN.matcher(s).matches())
            .collect(Collectors.toList());
        // collect all keys and values into a map
        Map<String, List<String>> customFields = new HashMap<>();
        customFieldKeys.forEach(s -> customFields.put(s, new ArrayList<>(attributes.get(s))));
        return customFields;
    }

    private Map<String, List<String>> extractCustomFieldsByName(ArrayListMultimap<String, String> attributes) {
        // get all custom field keys
        List<String> customFieldKeys = attributes.keySet().stream()
            .filter(s -> CUSTOM_FIELD_NAME_PATTERN.matcher(s).matches())
            .collect(Collectors.toList());
        // collect all keys and values into a map
        Map<String, List<String>> customFields = new HashMap<>();
        customFieldKeys.forEach(key -> customFields.put(extractCustomFieldName(key), new ArrayList<>(attributes.get(key))));
        return customFields;
    }

    private String extractCustomFieldName(String customFieldString) {
        Matcher matcher = CUSTOM_FIELD_NAME_PATTERN.matcher(customFieldString);
        if (matcher.matches()) {
            return matcher.group("name")
                .replaceAll("\\\\\\)", ")")
                .replaceAll("\\\\\\(", "(")
                .replaceAll("\\\\:", ":")
                .trim();
        } else {
            throw new SyntaxFormatException("Illegal custom field name: " + customFieldString);
        }
    }

}
