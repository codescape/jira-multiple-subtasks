package de.codescape.jira.plugins.multiplesubtasks.model;

import com.atlassian.jira.issue.Issue;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The {@link CreatedSubtask} represents a subtask that was created. This object can potentially contain warnings that
 * occurred during the creation of the subtask in Jira.
 */
public class CreatedSubtask {

    private final Issue issue;
    private final List<String> warnings;

    /**
     * Initialize the created subtask.
     *
     * @param issue    issue that was created
     * @param warnings warnings that occurred during creation
     */
    public CreatedSubtask(Issue issue, List<String> warnings) {
        this.issue = issue;
        this.warnings = warnings;
    }

    /**
     * Return the issue that was created.
     */
    public Issue getIssue() {
        return issue;
    }

    /**
     * Return all warnings that occurred during creation.
     */
    public List<String> getWarnings() {
        return warnings;
    }

    /**
     * Return all warnings that occurred during creation in HTML format.
     */
    public String getWarningsMessage() {
        return warnings.isEmpty() ? null : warnings.stream()
            .collect(Collectors.joining("</li><li>", "<ul><li>", "</li></ul>"));
    }

}
