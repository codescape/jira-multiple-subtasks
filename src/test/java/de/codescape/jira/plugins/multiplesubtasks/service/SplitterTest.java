package de.codescape.jira.plugins.multiplesubtasks.service;

import com.google.common.base.Splitter;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Map;
import java.util.stream.Collectors;

public class SplitterTest {

    @Test
    public void shouldParseComplexCombinationOfTasks() {
        String input = "- ein Task ohne weitere Details\n" +
            "- ein Task mit einem Bearbeiter\n" +
            "  assignee: codescape\n" +
            "- ein Task mit Bearbeiter und Priorität\n" +
            "  assignee: codescape\n" +
            "  priority: 3\n" +
            "     \n" +
            "- ein Task mit etwas vielen Leerzeichen am Ende    \n" +
            "-       ein Task mit etwas vielen Leerzeichen am Anfang\n" +
            "- ein Task mit - Zeichen im Text\n" +
            " - ein Task mit Leerzeichen vor dem - Zeichen im Text";
        cleanUpAndPrint(input);
    }

    @Test
    public void shouldParseASingleSimpleTask() {
        cleanUpAndPrint("- ein einfacher Task");
    }

    @Test(expected = UnsupportedTaskFormatException.class)
    public void shouldRejectASingleLineOfText() {
        cleanUpAndPrint("ein Task?");
    }

    @Test(expected = UnsupportedTaskFormatException.class)
    public void shouldRejectMultipleLinesOfText() {
        cleanUpAndPrint("ein Task?\nund noch mehr Text?");
    }

    @Test(expected = UnsupportedTaskFormatException.class)
    public void shouldRejectMultipleLinesOfTextNotStartingWithATaskInFirstLine() {
        cleanUpAndPrint("kein Task\n- und jetzt ein Task");
    }

    @Test(expected = UnsupportedTaskFormatException.class)
    public void shouldRejectTaskWithAttributesThatAreNotKeyValueAttributes() {
        cleanUpAndPrint("- Ein Task mit Attribut ohne Wert\n" +
            "  assignee");
    }

    private void cleanUpAndPrint(String input) {
        System.out.println("Input:\n" + input + "\n");
        try {
            printTasks(Splitter.on("\n-").trimResults().omitEmptyStrings().split(cleanUpInput(input)));
        } catch (UnsupportedTaskFormatException e) {
            System.out.println("Fehler:\n" + e);
            throw e;
        }
    }

    private String cleanUpInput(String input) {
        String cleanedUpInput = new BufferedReader(new StringReader(input))
            .lines()
            .map(String::trim)
            .collect(Collectors.joining("\n"));
        if (!cleanedUpInput.startsWith("-")) {
            throw new UnsupportedTaskFormatException("Tasks müssen mit einem - eingeleitet werden.");
        }
        return "\n" + cleanedUpInput;
    }

    private void printTasks(Iterable<String> result) {
        System.out.println("Result: ");
        result.forEach(entry -> {
            if (entry.contains("\n")) {
                try {
                    Map<String, String> split = Splitter
                        .on("\n")
                        .withKeyValueSeparator(Splitter
                            .on(":")
                            .trimResults()
                            .omitEmptyStrings())
                        .split("summary: " + entry);
                    System.out.println("Complex Task: [" + split + "]");
                } catch (IllegalArgumentException e) {
                    throw new UnsupportedTaskFormatException("Ungültiges Format für Task.", e);
                }
            } else {
                System.out.println("Simple Task: [{summary=" + entry + "}]");
            }
        });
    }

    private static class UnsupportedTaskFormatException extends RuntimeException {

        public UnsupportedTaskFormatException(String message) {
            super(message);
        }

        public UnsupportedTaskFormatException(String message, Throwable throwable) {
            super(message, throwable);
        }

    }

}
