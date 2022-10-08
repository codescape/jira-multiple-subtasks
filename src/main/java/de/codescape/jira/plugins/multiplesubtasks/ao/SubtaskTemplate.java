package de.codescape.jira.plugins.multiplesubtasks.ao;

import de.codescape.jira.plugins.multiplesubtasks.model.SubtaskTemplateType;
import net.java.ao.schema.Indexed;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.StringLength;

/**
 * A multiple subtasks template.
 */
public interface SubtaskTemplate extends EntityWithId {

    @Indexed
    @NotNull
    Long getUserId();

    void setUserId(Long userId);

    @NotNull
    SubtaskTemplateType getTemplateType();

    void setTemplateType(SubtaskTemplateType subtaskTemplateType);

    @Indexed
    Long getProjectId();

    void setProjectId(Long projectId);

    @NotNull
    String getName();

    void setName(String name);

    @StringLength(StringLength.UNLIMITED)
    @NotNull
    String getTemplate();

    void setTemplate(String template);

}
