package de.codescape.jira.plugins.multiplesubtasks.model;

/**
 * Sort order of templates.
 */
public enum TemplateSortOrder {

    NAME_ASC("NAME ASC", "name", "asc"),
    NAME_DESC("NAME DESC", "name", "desc"),
    AGE_ASC("ID ASC", "age", "asc"),
    AGE_DESC("ID DESC", "age", "desc");

    private final String sql;
    private final String bundle;
    private final String order;

    TemplateSortOrder(String sql, String bundle, String order) {
        this.sql = sql;
        this.bundle = bundle;
        this.order = order;
    }

    /**
     * Return the part of the query for the templates to be sorted.
     */
    public String getSql() {
        return sql;
    }

    /**
     * Return the part of the bundle name to be used for i18n.
     */
    public String getBundle() {
        return bundle;
    }

    /**
     * Return the order to be used for i18n.
     */
    public String getOrder() {
        return order;
    }

}
