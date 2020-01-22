package de.codescape.jira.plugins.multiplesubtasks.service;

/**
 * Exception class to be used for errors while parsing an input string and trying to extract a single or multiple
 * requests for new subtasks from it.
 */
public class SubTaskFormatException extends RuntimeException {

    /**
     * Create a new {@link SubTaskFormatException} with just an error message.
     *
     * @param message error message
     */
    public SubTaskFormatException(String message) {
        super(message);
    }

    /**
     * Create a new {@link SubTaskFormatException} with an error message and a root cause.
     *
     * @param message   error message
     * @param throwable root cause
     */
    public SubTaskFormatException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
