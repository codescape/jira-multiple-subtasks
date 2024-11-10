package de.codescape.jira.plugins.multiplesubtasks.action;

import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.security.request.RequestMethod;
import com.atlassian.jira.security.request.SupportedMethods;
import com.atlassian.jira.security.xsrf.RequiresXsrfCheck;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import de.codescape.jira.plugins.multiplesubtasks.model.CreatedSubtask;
import de.codescape.jira.plugins.multiplesubtasks.model.ShowSubtaskTemplate;
import de.codescape.jira.plugins.multiplesubtasks.service.MultipleSubtasksLicenseService;
import de.codescape.jira.plugins.multiplesubtasks.service.SubtaskTemplateService;
import de.codescape.jira.plugins.multiplesubtasks.service.SubtasksCreationService;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static de.codescape.jira.plugins.multiplesubtasks.action.MultipleSubtasksDialogAction.Parameters.INPUT_STRING;

/**
 * This action provides everything required for the subtask creation dialog.
 */
public class MultipleSubtasksDialogAction extends JiraWebActionSupport {

    private static final long serialVersionUID = 1L;
    private static final String EMPTY_STRING = "";

    /**
     * Names of all parameters used on this page.
     */
    static final class Parameters {

        static final String ACTION = "action";
        static final String INPUT_STRING = "inputString";
        static final String ISSUE_KEY = "issueKey";

    }

    /**
     * Values of all actions used on this page.
     */
    static final class Actions {

        static final String CREATE = "create";
        static final String RESET = "reset";
        static final String CLOSE = "close";

    }

    private final JiraAuthenticationContext jiraAuthenticationContext;
    private final IssueManager issueManager;
    private final SubtasksCreationService subtasksCreationService;
    private final SubtaskTemplateService subtaskTemplateService;
    private final MultipleSubtasksLicenseService multipleSubtasksLicenseService;

    private String issueKey;
    private String inputString = EMPTY_STRING;
    private List<CreatedSubtask> createdSubTasks;

    @Inject
    public MultipleSubtasksDialogAction(@ComponentImport JiraAuthenticationContext jiraAuthenticationContext,
                                        @ComponentImport IssueManager issueManager,
                                        SubtasksCreationService subtasksCreationService,
                                        SubtaskTemplateService subtaskTemplateService,
                                        MultipleSubtasksLicenseService multipleSubtasksLicenseService) {
        this.jiraAuthenticationContext = jiraAuthenticationContext;
        this.issueManager = issueManager;
        this.subtasksCreationService = subtasksCreationService;
        this.subtaskTemplateService = subtaskTemplateService;
        this.multipleSubtasksLicenseService = multipleSubtasksLicenseService;
    }

    /**
     * Returns the current issue key.
     */
    public String getIssueKey() {
        return issueKey;
    }

    /**
     * Returns the list of created subtasks.
     */
    public List<CreatedSubtask> getCreatedSubTasks() {
        return createdSubTasks;
    }

    /**
     * Returns the original input string.
     */
    public String getInputString() {
        return inputString;
    }

    @Override
    @SupportedMethods({RequestMethod.GET})
    public String doDefault() {
        if (!multipleSubtasksLicenseService.hasValidLicense()) {
            addErrorMessage("Invalid or missing plugin license.");
            return ERROR;
        }
        issueKey = getParameter(Parameters.ISSUE_KEY);
        if (issueKey == null) {
            addErrorMessage("No issue key provided!");
            return ERROR;
        }
        return SUCCESS;
    }

    @Override
    @RequiresXsrfCheck
    @SupportedMethods({RequestMethod.POST})
    protected String doExecute() {
        String action = getParameter(Parameters.ACTION);
        // always allow to close the dialog
        if (!Actions.CLOSE.equals(action) && ERROR.equals(doDefault())) {
            return ERROR;
        }
        if (action != null) {
            switch (action) {
                case Actions.CREATE:
                    inputString = getParameter(INPUT_STRING);
                    try {
                        createdSubTasks = subtasksCreationService.subtasksFromString(issueKey, inputString);
                    } catch (RuntimeException e) {
                        addErrorMessage(e.getMessage());
                        return ERROR;
                    }
                    break;
                case Actions.RESET:
                    clearInputString();
                    break;
                case Actions.CLOSE:
                    return returnComplete();
            }
            return SUCCESS;
        } else {
            return ERROR;
        }
    }

    /**
     * Returns a list of all existing user templates for the currently logged-in user.
     */
    public List<ShowSubtaskTemplate> getUserTemplates() {
        return subtaskTemplateService.getUserTemplates(jiraAuthenticationContext.getLoggedInUser().getId())
            .stream()
            .map(ShowSubtaskTemplate::new)
            .collect(Collectors.toList());
    }

    /**
     * Returns a list of all existing project templates for the current issue.
     */
    public List<ShowSubtaskTemplate> getProjectTemplates() {
        Project project = issueManager.getIssueByCurrentKey(issueKey).getProjectObject();
        if (project == null) {
            return Collections.emptyList();
        }
        return subtaskTemplateService.getProjectTemplates(project.getId())
            .stream()
            .map(ShowSubtaskTemplate::new)
            .collect(Collectors.toList());
    }

    /**
     * Returns a list of all existing global templates.
     */
    public List<ShowSubtaskTemplate> getGlobalTemplates() {
        return subtaskTemplateService.getGlobalTemplates()
            .stream()
            .map(ShowSubtaskTemplate::new)
            .collect(Collectors.toList());
    }

    /* helper methods */

    private void clearInputString() {
        inputString = EMPTY_STRING;
    }

    private String getParameter(String parameterName) {
        return getHttpRequest().getParameter(parameterName);
    }

}
