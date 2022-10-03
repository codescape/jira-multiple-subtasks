package de.codescape.jira.plugins.multiplesubtasks.service;

import com.google.common.base.Splitter;
import com.google.common.collect.ArrayListMultimap;
import de.codescape.jira.plugins.multiplesubtasks.model.SubTask;
import de.codescape.jira.plugins.multiplesubtasks.model.SyntaxFormatException;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation to interpret and transform the textual representation of a list of subtasks.
 */
@Component
public class SubTasksSyntaxService {

    private static final String NEWLINE = "\n";
    private static final String MINUS_SIGN = "-";

    /**
     * Parse an input string and try to transform it into potentially multiple subtasks.
     *
     * @param input given textual representation of subtasks
     * @return list of subtasks
     * @throws SyntaxFormatException if input format contains errors
     */
    public List<SubTask> parseString(String input) {
        String cleanedUpInput = trimWhitespaceLineByLine(input);
        if (!cleanedUpInput.startsWith(MINUS_SIGN)) {
            throw new SyntaxFormatException("Subtasks always must be introduced with a minus sign.");
        }
        List<SubTask> subTasks = new ArrayList<>();
        splitInputIntoTasks(cleanedUpInput).forEach(entry -> {
            try {
                subTasks.add(new SubTask(createListOfSubTaskAttributes(entry)));
            } catch (IllegalArgumentException e) {
                throw new SyntaxFormatException("Error reading subtask definition for entry '" + entry + "'", e);
            }
        });
        return subTasks;
    }

    /**
     * Split the textual subtask representation into lines and create map entries from every line. As the starting line
     * only contains a value we add the key before interpreting the list of attributes.
     */
    private ArrayListMultimap<String, String> createListOfSubTaskAttributes(String entry) {
        String entryWithSummary = "summary: " + entry;
        List<String> attributesAsList = Splitter
            .on(NEWLINE)
            .splitToList(entryWithSummary);
        Splitter keyValueSeparator = Splitter
            .on(":")
            .limit(2)
            .trimResults()
            .omitEmptyStrings();
        ArrayListMultimap<String, String> keyValues = ArrayListMultimap.create();
        attributesAsList.forEach(attribute -> {
            List<String> result = keyValueSeparator.splitToList(attribute);
            if (result.size() != 2) {
                throw new SyntaxFormatException("Attribute " + result.get(0) + " is missing a value!");
            }
            keyValues.put(result.get(0), result.get(1));
        });
        return keyValues;
    }

    /**
     * Split the provided input string whenever a minus sign is the first element after a newline. This is what we
     * interpret as the start of a new subtask. Since the first subtask has no newline before the minus sign we add
     * this before splitting.
     */
    private Iterable<String> splitInputIntoTasks(String cleanedUpInput) {
        String prependLeadingNewline = NEWLINE + cleanedUpInput;
        return Splitter
            .on(NEWLINE + MINUS_SIGN)
            .trimResults()
            .omitEmptyStrings()
            .split(prependLeadingNewline);
    }

    /**
     * Walk through the provided input string line by line and remove leading and trailing whitespace. Also ignore
     * completely empty lines and return a sanitized multi line string.
     */
    private String trimWhitespaceLineByLine(String input) {
        return new BufferedReader(new StringReader(input))
            .lines()
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .collect(Collectors.joining(NEWLINE));
    }

}
