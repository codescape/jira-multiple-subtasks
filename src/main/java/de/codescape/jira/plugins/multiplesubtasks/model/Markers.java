package de.codescape.jira.plugins.multiplesubtasks.model;

/**
 * Markers for attributes with a special handling on subtask creation.
 */
public class Markers {

    /**
     * Inherit the value(s) of the parent task.
     */
    public static final String INHERIT_MARKER = "@inherit";

    /**
     * Use the current user for this attribute.
     */
    public static final String CURRENT_MARKER = "@current";

}
