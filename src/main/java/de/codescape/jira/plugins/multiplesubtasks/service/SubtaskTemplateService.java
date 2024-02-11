package de.codescape.jira.plugins.multiplesubtasks.service;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.tx.Transactional;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import de.codescape.jira.plugins.multiplesubtasks.ao.SubtaskConfig;
import de.codescape.jira.plugins.multiplesubtasks.ao.SubtaskShare;
import de.codescape.jira.plugins.multiplesubtasks.ao.SubtaskTemplate;
import de.codescape.jira.plugins.multiplesubtasks.model.SubtaskTemplateType;
import de.codescape.jira.plugins.multiplesubtasks.model.TemplateSortOrder;
import net.java.ao.DBParam;
import net.java.ao.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Subtask template service to persist and load templates from and to the database.
 */
@Transactional
@Component
public class SubtaskTemplateService {

    private final ActiveObjects activeObjects;
    private final MultipleSubtasksConfigurationService multipleSubtasksConfigurationService;

    @Autowired
    public SubtaskTemplateService(@ComponentImport ActiveObjects activeObjects,
                                  MultipleSubtasksConfigurationService multipleSubtasksConfigurationService) {
        this.activeObjects = activeObjects;
        this.multipleSubtasksConfigurationService = multipleSubtasksConfigurationService;
    }

    /**
     * Save a user specific subtask template.
     */
    public SubtaskTemplate saveUserTemplate(Long userId, Long templateId, String templateName, String templateText) {
        SubtaskTemplate subtaskTemplate;
        if (templateId == null) {
            subtaskTemplate = activeObjects.create(SubtaskTemplate.class,
                new DBParam("TEMPLATE_TYPE", SubtaskTemplateType.USER),
                new DBParam("USER_ID", userId),
                new DBParam("NAME", templateName),
                new DBParam("TEMPLATE", templateText)
            );
            subtaskTemplate.save();
        } else {
            subtaskTemplate = findUserTemplate(userId, templateId);
            if (subtaskTemplate != null) {
                subtaskTemplate.setName(templateName);
                subtaskTemplate.setTemplate(templateText);
                subtaskTemplate.save();
            }
        }
        return subtaskTemplate;
    }

    /**
     * Return a list of all subtask templates for a user.
     */
    public List<SubtaskTemplate> getUserTemplates(Long userId, boolean includeSharedTemplates) {
        List<SubtaskTemplate> ownTemplates = Arrays.asList(activeObjects.find(SubtaskTemplate.class,
            Query.select()
                .where("USER_ID = ? and TEMPLATE_TYPE = ?", userId, SubtaskTemplateType.USER)
                .order(getTemplateSortOrder())));
        List<SubtaskTemplate> sharedTemplates = Collections.emptyList();
        if (includeSharedTemplates) {
            sharedTemplates = Arrays.asList(activeObjects.find(SubtaskTemplate.class,
                Query.select()
                    .alias(SubtaskTemplate.class, "ST")
                    .alias(SubtaskShare.class, "SS")
                    .join(SubtaskShare.class, "ST.ID = SS.TEMPLATE_ID")
                    .where("SS.USER_ID = ? and ST.TEMPLATE_TYPE = ?", userId, SubtaskTemplateType.USER)));
        }
        return Stream.concat(ownTemplates.stream(), sharedTemplates.stream()).collect(Collectors.toList());
    }

    /**
     * Delete a given subtask template for a user.
     */
    public void deleteUserTemplate(Long userId, Long templateId) {
        SubtaskTemplate subtaskTemplate = findUserTemplate(userId, templateId);
        if (subtaskTemplate != null) {
            Arrays.stream(subtaskTemplate.getShares()).forEach(activeObjects::delete);
            activeObjects.delete(subtaskTemplate);
        }
    }

    /**
     * Find a given subtask template for a user.
     */
    public SubtaskTemplate findUserTemplate(Long userId, Long templateId) {
        SubtaskTemplate[] subtaskTemplates = activeObjects.find(SubtaskTemplate.class,
            Query.select()
                .where("USER_ID = ? and ID = ? and TEMPLATE_TYPE = ?", userId, templateId, SubtaskTemplateType.USER));
        return subtaskTemplates.length > 0 ? subtaskTemplates[0] : null;
    }

    /**
     * Save a project specific subtask template.
     */
    public SubtaskTemplate saveProjectTemplate(Long projectId, Long userId, Long templateId, String templateName, String templateText) {
        SubtaskTemplate subtaskTemplate;
        if (templateId == null) {
            subtaskTemplate = activeObjects.create(SubtaskTemplate.class,
                new DBParam("TEMPLATE_TYPE", SubtaskTemplateType.PROJECT),
                new DBParam("PROJECT_ID", projectId),
                new DBParam("USER_ID", userId),
                new DBParam("NAME", templateName),
                new DBParam("TEMPLATE", templateText)
            );
            subtaskTemplate.save();
        } else {
            subtaskTemplate = findProjectTemplate(projectId, templateId);
            if (subtaskTemplate != null) {
                subtaskTemplate.setUserId(userId);
                subtaskTemplate.setName(templateName);
                subtaskTemplate.setTemplate(templateText);
                subtaskTemplate.save();
            }
        }
        return subtaskTemplate;
    }

    /**
     * Return a list of all subtask templates for a project.
     */
    public List<SubtaskTemplate> getProjectTemplates(Long projectId, boolean includeSharedTemplates) {
        List<SubtaskTemplate> ownTemplates = Arrays.asList(activeObjects.find(SubtaskTemplate.class,
            Query.select()
                .where("PROJECT_ID = ? and TEMPLATE_TYPE = ?", projectId, SubtaskTemplateType.PROJECT)
                .order(getTemplateSortOrder())));
        List<SubtaskTemplate> sharedTemplates = Collections.emptyList();
        if (includeSharedTemplates) {
            sharedTemplates = Arrays.asList(activeObjects.find(SubtaskTemplate.class,
                Query.select()
                    .alias(SubtaskTemplate.class, "ST")
                    .alias(SubtaskShare.class, "SS")
                    .join(SubtaskShare.class, "ST.ID = SS.TEMPLATE_ID")
                    .where("SS.PROJECT_ID = ? and ST.TEMPLATE_TYPE = ?", projectId, SubtaskTemplateType.PROJECT)));
        }
        return Stream.concat(ownTemplates.stream(), sharedTemplates.stream()).collect(Collectors.toList());
    }

    /**
     * Delete a given subtask template for a project.
     */
    public void deleteProjectTemplate(Long projectId, Long templateId) {
        SubtaskTemplate subtaskTemplate = findProjectTemplate(projectId, templateId);
        if (subtaskTemplate != null) {
            Arrays.stream(subtaskTemplate.getShares()).forEach(activeObjects::delete);
            activeObjects.delete(subtaskTemplate);
        }
    }

    /**
     * Find a given subtask template for a project.
     */
    public SubtaskTemplate findProjectTemplate(Long projectId, Long templateId) {
        SubtaskTemplate[] subtaskTemplates = activeObjects.find(SubtaskTemplate.class,
            Query.select()
                .where("PROJECT_ID = ? and ID = ? and TEMPLATE_TYPE = ?", projectId, templateId, SubtaskTemplateType.PROJECT));
        return subtaskTemplates.length > 0 ? subtaskTemplates[0] : null;
    }

    private String getTemplateSortOrder() {
        SubtaskConfig config = multipleSubtasksConfigurationService.get(MultipleSubtasksConfigurationService.TEMPLATES_SORT_ORDER);
        if (config != null) {
            return TemplateSortOrder.valueOf(config.getValue()).getSql();
        } else {
            return TemplateSortOrder.NAME_ASC.getSql();
        }
    }

    /**
     * Save a global subtask template.
     */
    public SubtaskTemplate saveGlobalTemplate(Long userId, Long templateId, String templateName, String templateText) {
        SubtaskTemplate subtaskTemplate;
        if (templateId == null) {
            subtaskTemplate = activeObjects.create(SubtaskTemplate.class,
                new DBParam("TEMPLATE_TYPE", SubtaskTemplateType.GLOBAL),
                new DBParam("USER_ID", userId),
                new DBParam("NAME", templateName),
                new DBParam("TEMPLATE", templateText)
            );
            subtaskTemplate.save();
        } else {
            subtaskTemplate = findGlobalTemplate(templateId);
            if (subtaskTemplate != null) {
                subtaskTemplate.setUserId(userId);
                subtaskTemplate.setName(templateName);
                subtaskTemplate.setTemplate(templateText);
                subtaskTemplate.save();
            }
        }
        return subtaskTemplate;
    }

    /**
     * Find a given global subtask template.
     */
    public SubtaskTemplate findGlobalTemplate(Long templateId) {
        SubtaskTemplate[] subtaskTemplates = activeObjects.find(SubtaskTemplate.class,
            Query.select()
                .where("ID = ? and TEMPLATE_TYPE = ?", templateId, SubtaskTemplateType.GLOBAL));
        return subtaskTemplates.length > 0 ? subtaskTemplates[0] : null;
    }

    /**
     * Delete a given global subtask template.
     */
    public void deleteGlobalTemplate(Long templateId) {
        SubtaskTemplate subtaskTemplate = findGlobalTemplate(templateId);
        if (subtaskTemplate != null) {
            Arrays.stream(subtaskTemplate.getShares()).forEach(activeObjects::delete);
            activeObjects.delete(subtaskTemplate);
        }
    }

    /**
     * Return a list of all global subtask templates.
     */
    public List<SubtaskTemplate> getGlobalTemplates() {
        return Arrays.asList(activeObjects.find(SubtaskTemplate.class,
            Query.select()
                .where("TEMPLATE_TYPE = ?", SubtaskTemplateType.GLOBAL)
                .order(getTemplateSortOrder()))
        );
    }

    /**
     * Create a share for a given user template with a given user.
     * <p>
     * A template can never be shared with the template owner and not with the same user more than one time.
     */
    public void shareUserTemplate(SubtaskTemplate template, ApplicationUser user) {
        SubtaskShare[] subtaskShares = activeObjects.find(SubtaskShare.class,
            Query.select()
                .where("TEMPLATE_ID = ? and USER_ID = ?", template.getID(), user.getId()));
        if (!Objects.equals(template.getUserId(), user.getId()) && subtaskShares.length == 0) {
            SubtaskShare subtaskShare = activeObjects.create(SubtaskShare.class,
                new DBParam("TEMPLATE_ID", template.getID()),
                new DBParam("USER_ID", user.getId()));
            subtaskShare.save();
        }
    }

    /**
     * Create a share for a given project template with a given project.
     * <p>
     * A template can never be shared with the owning project and not with the same project more than one time.
     */
    public void shareProjectTemplate(SubtaskTemplate template, Project project) {
        SubtaskShare[] subtaskShares = activeObjects.find(SubtaskShare.class,
            Query.select()
                .where("TEMPLATE_ID = ? and PROJECT_ID = ?", template.getID(), project.getId()));
        if (!Objects.equals(template.getProjectId(), project.getId()) && subtaskShares.length == 0) {
            SubtaskShare subtaskShare = activeObjects.create(SubtaskShare.class,
                new DBParam("TEMPLATE_ID", template.getID()),
                new DBParam("PROJECT_ID", project.getId()));
            subtaskShare.save();
        }
    }

}
