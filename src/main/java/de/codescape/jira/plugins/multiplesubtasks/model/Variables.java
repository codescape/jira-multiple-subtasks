package de.codescape.jira.plugins.multiplesubtasks.model;

import java.util.regex.Pattern;

/**
 * Variables are introduced by an `@` character and their value is surrounded by curly braces.
 */
public class Variables {

    /**
     * Pattern to find all variables inside a value.
     */
    public final static Pattern FIND_VARIABLES = Pattern.compile("@\\{([^}]+)}");

    /**
     * Pattern to extract all variable values from a value.
     */
    // FIXME REMOVE
    public final static Pattern FIND_VALUES = Pattern.compile("(?<=@\\{)([^}]+)(?=})");

}
