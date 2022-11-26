package de.codescape.jira.plugins.multiplesubtasks.service;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.tx.Transactional;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import de.codescape.jira.plugins.multiplesubtasks.ao.SubtaskTemplate;
import de.codescape.jira.plugins.multiplesubtasks.model.SubtaskTemplateType;
import net.java.ao.DBParam;
import net.java.ao.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Subtask template service to persist and load templates from and to the database.
 */
@Transactional
@Component
public class SubtaskTemplateService {

    private final ActiveObjects activeObjects;

    @Autowired
    public SubtaskTemplateService(@ComponentImport ActiveObjects activeObjects) {
        this.activeObjects = activeObjects;
    }

    /**
     * Save a user specific subtask template.
     */
    public void saveUserTemplate(ApplicationUser applicationUser, Long id, String name, String template) {
        if (id == null) {
            SubtaskTemplate subtaskTemplate = activeObjects.create(SubtaskTemplate.class,
                new DBParam("TEMPLATE_TYPE", SubtaskTemplateType.USER),
                new DBParam("USER_ID", applicationUser.getId()),
                new DBParam("NAME", name),
                new DBParam("TEMPLATE", template)
            );
            subtaskTemplate.save();
        } else {
            SubtaskTemplate userTemplate = findUserTemplate(applicationUser, id);
            if (userTemplate != null) {
                userTemplate.setName(name);
                userTemplate.setTemplate(template);
                userTemplate.save();
            }
        }
    }

    /**
     * Return a list of all subtask templates for a user.
     */
    public List<SubtaskTemplate> getUserTemplates(ApplicationUser applicationUser) {
        return Arrays.asList(activeObjects.find(SubtaskTemplate.class,
            Query.select()
                .where("USER_ID = ? and TEMPLATE_TYPE = ?", applicationUser.getId(), SubtaskTemplateType.USER)
                .order("ID DESC"))
        );
    }

    /**
     * Delete a given subtask template for a user.
     */
    public void deleteUserTemplate(ApplicationUser applicationUser, Long templateId) {
        SubtaskTemplate userTemplate = findUserTemplate(applicationUser, templateId);
        if (userTemplate != null) {
            activeObjects.delete(userTemplate);
        }
    }

    /**
     * Find a given subtask template for a user.
     */
    public SubtaskTemplate findUserTemplate(ApplicationUser applicationUser, Long templateId) {
        SubtaskTemplate[] subtaskTemplates = activeObjects.find(SubtaskTemplate.class,
            Query.select().where("USER_ID = ? and ID = ? and TEMPLATE_TYPE = ?", applicationUser.getId(), templateId, SubtaskTemplateType.USER));
        return subtaskTemplates.length > 0 ? subtaskTemplates[0] : null;
    }

}
