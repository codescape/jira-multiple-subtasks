package de.codescape.jira.plugins.multiplesubtasks.model;

import de.codescape.jira.plugins.multiplesubtasks.ao.SubtaskTemplate;

public class ShowSubtaskTemplate {

    private final Long id;
    private final SubtaskTemplateType templateType;
    private final Long userId;
    private final Long projectId;
    private final String name;
    private final String template;

    public ShowSubtaskTemplate(SubtaskTemplate subtaskTemplate) {
        this.id = subtaskTemplate.getID();
        this.templateType = subtaskTemplate.getTemplateType();
        this.userId = subtaskTemplate.getUserId();
        this.projectId = subtaskTemplate.getProjectId();
        this.name = subtaskTemplate.getName();
        this.template = subtaskTemplate.getTemplate();
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
