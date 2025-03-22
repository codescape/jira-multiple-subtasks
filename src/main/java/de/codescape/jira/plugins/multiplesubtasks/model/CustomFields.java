package de.codescape.jira.plugins.multiplesubtasks.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class to deal with custom fields.
 */
public class CustomFields {

    // custom fields can be referenced by id: customfield_12345
    public static final Pattern CUSTOM_FIELD_ID_PATTERN = Pattern.compile("customfield_\\d{5}");

    // custom fields can be reference by name: customfield(fieldname)
    public static final Pattern CUSTOM_FIELD_NAME_PATTERN = Pattern.compile("customfield\\((?<name>(?:\\\\\\\\|\\\\\\)|[^)])++)\\)");

    /**
     * Retrieve the name of the customfield from an expression like <code>customfield(fieldName)</code>.
     */
    public static String extractCustomFieldName(String customFieldString) {
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
