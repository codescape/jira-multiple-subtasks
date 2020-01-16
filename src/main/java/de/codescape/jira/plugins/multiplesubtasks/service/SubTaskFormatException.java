package de.codescape.jira.plugins.multiplesubtasks.service;

// TODO document class
public class SubTaskFormatException extends RuntimeException {

    public SubTaskFormatException(String message) {
        super(message);
    }

    public SubTaskFormatException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
