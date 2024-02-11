package de.codescape.jira.plugins.multiplesubtasks.ao;

import net.java.ao.schema.Indexed;
import net.java.ao.schema.NotNull;

public interface SubtaskShare extends BaseEntity {

    /**
     * The template that is shared.
     */
    SubtaskTemplate getTemplate();

    void setTemplate(SubtaskTemplate template);

    /**
     * (optional) The user the template is shared with.
     */
    @Indexed
    @NotNull
    Long getUserId();

    void setUserId(Long userId);

    /**
     * (optional) The project the template is shared with.
     */
    @Indexed
    Long getProjectId();

    void setProjectId(Long projectId);

}
