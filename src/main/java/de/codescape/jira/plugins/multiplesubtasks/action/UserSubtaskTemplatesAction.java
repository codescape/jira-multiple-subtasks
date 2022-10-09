package de.codescape.jira.plugins.multiplesubtasks.action;

import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.security.request.RequestMethod;
import com.atlassian.jira.security.request.SupportedMethods;
import com.atlassian.jira.security.xsrf.RequiresXsrfCheck;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import de.codescape.jira.plugins.multiplesubtasks.model.ShowSubtaskTemplate;
import de.codescape.jira.plugins.multiplesubtasks.service.SubtaskTemplateService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This action is used to manage the page where users can create their own subtasks templates.
 */
public class UserSubtaskTemplatesAction extends JiraWebActionSupport {

    private static final long serialVersionUID = 1L;

    /**
     * Names of all parameters used on the page.
     */
    static final class Parameters {

        static final String ACTION = "action";

    }

    /**
     * Values of all actions used on the page.
     */
    static final class Actions {

        static final String SAVE = "save";
        static final String DELETE = "delete";

    }

    private final SubtaskTemplateService subtaskTemplateService;
    private final JiraAuthenticationContext jiraAuthenticationContext;

    @Autowired
    public UserSubtaskTemplatesAction(@ComponentImport JiraAuthenticationContext jiraAuthenticationContext,
                                      SubtaskTemplateService subtaskTemplateService) {
        this.jiraAuthenticationContext = jiraAuthenticationContext;
        this.subtaskTemplateService = subtaskTemplateService;
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
        String action = getParameter(Parameters.ACTION);
        switch (action) {
            case Actions.SAVE:
                String templateName = getParameter("templateName");
                String template = getParameter("template");
                subtaskTemplateService.saveUserTemplate(jiraAuthenticationContext.getLoggedInUser(), templateName, template);
                break;
            case Actions.DELETE:
                String templateId = getParameter("templateId");
                subtaskTemplateService.deleteUserTemplate(jiraAuthenticationContext.getLoggedInUser(), Long.valueOf(templateId));
                break;
        }
        return SUCCESS;
    }

    /**
     * Returns a list of all existing user templates for the currently logged in user.
     */
    public List<ShowSubtaskTemplate> getUserTemplates() {
        return subtaskTemplateService.getUserTemplates(jiraAuthenticationContext.getLoggedInUser())
            .stream()
            .map(ShowSubtaskTemplate::new)
            .collect(Collectors.toList());
    }

    /* helper methods */

    private String getParameter(String parameterName) {
        return getHttpRequest().getParameter(parameterName);
    }

}
