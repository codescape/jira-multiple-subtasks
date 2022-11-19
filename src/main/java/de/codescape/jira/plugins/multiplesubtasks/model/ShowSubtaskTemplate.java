package de.codescape.jira.plugins.multiplesubtasks.model;

import de.codescape.jira.plugins.multiplesubtasks.ao.SubtaskTemplate;

/**
 * Wrapper object for a given {@link SubtaskTemplate} that can be used in velocity templates for displaying purposes.
 */
public class ShowSubtaskTemplate {

    private Long id;
    private SubtaskTemplateType templateType;
    private Long userId;
    private Long projectId;
    private String name;
    private String template;

    public ShowSubtaskTemplate(SubtaskTemplate subtaskTemplate) {
        this.id = subtaskTemplate.getID();
        this.templateType = subtaskTemplate.getTemplateType();
        this.userId = subtaskTemplate.getUserId();
        this.projectId = subtaskTemplate.getProjectId();
        this.name = subtaskTemplate.getName();
        this.template = subtaskTemplate.getTemplate();
    }

    public ShowSubtaskTemplate(String name, String template) {
        this.name = name;
        this.template = template;
    }

    public Long getId() {
        return id;
    }

    public SubtaskTemplateType getTemplateType() {
        return templateType;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public String getName() {
        return name;
    }

    public String getTemplate() {
        return template;
    }

}
