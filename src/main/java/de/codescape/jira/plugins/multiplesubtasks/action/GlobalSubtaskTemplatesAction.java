package de.codescape.jira.plugins.multiplesubtasks.action;

import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.security.request.RequestMethod;
import com.atlassian.jira.security.request.SupportedMethods;
import com.atlassian.jira.security.xsrf.RequiresXsrfCheck;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import de.codescape.jira.plugins.multiplesubtasks.ao.SubtaskTemplate;
import de.codescape.jira.plugins.multiplesubtasks.model.ShowSubtaskTemplate;
import de.codescape.jira.plugins.multiplesubtasks.model.SyntaxFormatException;
import de.codescape.jira.plugins.multiplesubtasks.service.SubtaskTemplateService;
import de.codescape.jira.plugins.multiplesubtasks.service.syntax.SubtasksSyntaxService;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This action is used to manage the page where users can create global subtasks templates.
 */
public class GlobalSubtaskTemplatesAction extends JiraWebActionSupport {

    private static final long serialVersionUID = 1L;
    private static final long MAXIMUM_TEMPLATE_LENGTH = 64000;
    private static final long MAXIMUM_TEMPLATE_NAME_LENGTH = 80;

    /**
     * Names of all parameters used on the page.
     */
    static final class Parameters {

        static final String ACTION = "action";
        static final String TEMPLATE_ID = "templateId";
        static final String TEMPLATE_NAME = "templateName";
        static final String TEMPLATE_TEXT = "templateText";

    }

    /**
     * Values of all actions used on the page.
     */
    static final class Actions {

        static final String SAVE = "save";
        static final String DELETE = "delete";
        static final String EDIT = "edit";

    }

    private final SubtaskTemplateService subtaskTemplateService;
    private final JiraAuthenticationContext jiraAuthenticationContext;
    private final SubtasksSyntaxService subtasksSyntaxService;

    private ShowSubtaskTemplate editTemplate;

    @Inject
    public GlobalSubtaskTemplatesAction(@ComponentImport JiraAuthenticationContext jiraAuthenticationContext,
                                        SubtaskTemplateService subtaskTemplateService,
                                        SubtasksSyntaxService subtasksSyntaxService) {
        this.jiraAuthenticationContext = jiraAuthenticationContext;
        this.subtaskTemplateService = subtaskTemplateService;
        this.subtasksSyntaxService = subtasksSyntaxService;
    }

    @Override
    @SupportedMethods({RequestMethod.GET})
    public String doDefault() {
        return SUCCESS;
    }

    @Override
    @RequiresXsrfCheck
    @SupportedMethods({RequestMethod.POST})
    protected String doExecute() {
        // use validation of doDefault() method
        if (doDefault().equals(ERROR)) {
            return ERROR;
        }

        // extract all parameters
        String action = getParameter(ProjectSubtaskTemplatesAction.Parameters.ACTION);
        String templateIdString = getParameter(ProjectSubtaskTemplatesAction.Parameters.TEMPLATE_ID);
        Long templateId = templateIdString != null ? Long.valueOf(templateIdString) : null;
        String templateName = getParameter(ProjectSubtaskTemplatesAction.Parameters.TEMPLATE_NAME);
        String templateText = getParameter(ProjectSubtaskTemplatesAction.Parameters.TEMPLATE_TEXT);

        // perform the requested action
        switch (action) {
            case GlobalSubtaskTemplatesAction.Actions.SAVE:
                try {
                    subtasksSyntaxService.parseString(templateText);
                } catch (SyntaxFormatException sfe) {
                    addErrorMessage(sfe.getMessage());
                    editTemplate = new ShowSubtaskTemplate(templateName, templateText);
                    return SUCCESS;
                }
                subtaskTemplateService.saveGlobalTemplate(jiraAuthenticationContext.getLoggedInUser().getId(), templateId, templateName, templateText);
                break;
            case GlobalSubtaskTemplatesAction.Actions.DELETE:
                subtaskTemplateService.deleteGlobalTemplate(templateId);
                break;
            case GlobalSubtaskTemplatesAction.Actions.EDIT:
                SubtaskTemplate subtaskTemplate = subtaskTemplateService.findGlobalTemplate(templateId);
                if (subtaskTemplate != null) {
                    editTemplate = new ShowSubtaskTemplate(subtaskTemplate);
                }
                break;
        }
        // render page
        return SUCCESS;
    }

    /**
     * Returns a list of all existing global subtask templates.
     */
    public List<ShowSubtaskTemplate> getGlobalTemplates() {
        return subtaskTemplateService.getGlobalTemplates()
            .stream()
            .map(ShowSubtaskTemplate::new)
            .collect(Collectors.toList());
    }

    /**
     * Returns the maximum number of characters to be used for a template.
     */
    public long getMaximumTemplateLength() {
        return MAXIMUM_TEMPLATE_LENGTH;
    }

    /**
     * Returns the maximum number of characters to be used for the template name.
     */
    public long getMaximumTemplateNameLength() {
        return MAXIMUM_TEMPLATE_NAME_LENGTH;
    }

    /**
     * Returns the selected subtask template for edit.
     */
    public ShowSubtaskTemplate getEditTemplate() {
        return editTemplate;
    }

    /* helper methods */

    private String getParameter(String parameterName) {
        return getHttpRequest().getParameter(parameterName);
    }

}
