package de.codescape.jira.plugins.multiplesubtasks.model;

import com.atlassian.jira.issue.Issue;

import java.util.List;
import java.util.stream.Collectors;

public class CreatedSubtask {

    private final Issue issue;
    private final List<String> warnings;

    public CreatedSubtask(Issue issue, List<String> warnings) {
        this.issue = issue;
        this.warnings = warnings;
    }

    public Issue getIssue() {
        return issue;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public String getWarningsMessage() {
        return warnings.isEmpty() ? null : warnings.stream()
            .collect(Collectors.joining("</li><li>", "<ul><li>", "</li></ul>"));
    }

}
