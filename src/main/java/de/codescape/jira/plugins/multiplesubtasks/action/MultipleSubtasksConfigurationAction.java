package de.codescape.jira.plugins.multiplesubtasks.action;

import com.atlassian.jira.security.request.RequestMethod;
import com.atlassian.jira.security.request.SupportedMethods;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import de.codescape.jira.plugins.multiplesubtasks.service.MultipleSubtasksConfigurationService;

/**
 * This action allows to configure Multiple Subtasks for Jira.
 */
public class MultipleSubtasksConfigurationAction extends JiraWebActionSupport {

    @Override
    @SupportedMethods({RequestMethod.GET})
    public String doDefault() {
        return SUCCESS;
    }

    /**
     * Return the key for allowed templates per user.
     */
    public String getTemplatesPerUserKey() {
        return MultipleSubtasksConfigurationService.TEMPLATES_PER_USER;
    }

    /**
     * Return the key for allowed templates per project.
     */
    public String getTemplatesPerProjectKey() {
        return MultipleSubtasksConfigurationService.TEMPLATES_PER_PROJECT;
    }

}
