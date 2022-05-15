package de.codescape.jira.plugins.multiplesubtasks.model;

/**
 * Exception class to be used for errors while parsing an input string and trying to extract a single or multiple
 * requests for new subtasks from it.
 */
public class SyntaxFormatException extends RuntimeException {

    /**
     * Create a new {@link SyntaxFormatException} with just an error message.
     *
     * @param message error message
     */
    public SyntaxFormatException(String message) {
        super(message);
    }

    /**
     * Create a new {@link SyntaxFormatException} with an error message and a root cause.
     *
     * @param message   error message
     * @param throwable root cause
     */
    public SyntaxFormatException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
