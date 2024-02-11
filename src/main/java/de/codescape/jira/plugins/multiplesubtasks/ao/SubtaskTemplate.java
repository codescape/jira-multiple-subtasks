package de.codescape.jira.plugins.multiplesubtasks.ao;

import de.codescape.jira.plugins.multiplesubtasks.model.SubtaskTemplateType;
import net.java.ao.OneToMany;
import net.java.ao.schema.Indexed;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.StringLength;

/**
 * A multiple subtasks template.
 */
public interface SubtaskTemplate extends BaseEntity {

    /**
     * The user who owns and created this subtask template.
     */
    @Indexed
    @NotNull
    Long getUserId();

    void setUserId(Long userId);

    /**
     * The type of the subtask template.
     */
    @NotNull
    SubtaskTemplateType getTemplateType();

    void setTemplateType(SubtaskTemplateType subtaskTemplateType);

    /**
     * (optional) The project this template is created for.
     */
    @Indexed
    Long getProjectId();

    void setProjectId(Long projectId);

    /**
     * The name of the template.
     */
    @NotNull
    String getName();

    void setName(String name);

    /**
     * The code of the template.
     */
    @StringLength(StringLength.UNLIMITED)
    @NotNull
    String getTemplate();

    void setTemplate(String template);

    /**
     * (optional) Shares for this template with other users or projects.
     */
    @OneToMany(reverse = "getTemplate")
    SubtaskShare[] getShares();

}
