package de.codescape.jira.plugins.multiplesubtasks.action;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.security.request.RequestMethod;
import com.atlassian.jira.security.request.SupportedMethods;
import com.atlassian.jira.security.xsrf.RequiresXsrfCheck;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import de.codescape.jira.plugins.multiplesubtasks.model.ShowSubtaskTemplate;
import de.codescape.jira.plugins.multiplesubtasks.model.SyntaxFormatException;
import de.codescape.jira.plugins.multiplesubtasks.service.MultipleSubtasksLicenseService;
import de.codescape.jira.plugins.multiplesubtasks.service.SubtaskTemplateService;
import de.codescape.jira.plugins.multiplesubtasks.service.SubtasksCreationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

import static de.codescape.jira.plugins.multiplesubtasks.action.MultipleSubtasksDialogAction.Parameters.INPUT_STRING;

// TODO add documentation
// TODO create tests
public class MultipleSubtasksDialogAction extends JiraWebActionSupport {

    private static final long serialVersionUID = 1L;

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
    private final SubtasksCreationService subtasksCreationService;
    private final SubtaskTemplateService subtaskTemplateService;
    private final MultipleSubtasksLicenseService multipleSubtasksLicenseService;

    private String issueKey;
    private String inputString = "";
    private List<Issue> createdSubTasks;

    @Autowired
    public MultipleSubtasksDialogAction(@ComponentImport JiraAuthenticationContext jiraAuthenticationContext,
                                        SubtasksCreationService subtasksCreationService,
                                        SubtaskTemplateService subtaskTemplateService,
                                        MultipleSubtasksLicenseService multipleSubtasksLicenseService) {
        this.jiraAuthenticationContext = jiraAuthenticationContext;
        this.subtasksCreationService = subtasksCreationService;
        this.subtaskTemplateService = subtaskTemplateService;
        this.multipleSubtasksLicenseService = multipleSubtasksLicenseService;
    }

    /**
     * Short form for resolving a parameter from the HTTP request.
     *
     * @param parameterName name of the parameter
     * @return value of the parameter
     */
    String getParameter(String parameterName) {
        return getHttpRequest().getParameter(parameterName);
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
    public List<Issue> getCreatedSubTasks() {
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
        if (ERROR.equals(doDefault())) {
            return ERROR;
        }

        String action = getParameter(Parameters.ACTION);
        if (action != null) {
            switch (action) {
                case Actions.CREATE:
                    inputString = getParameter(INPUT_STRING);
                    try {
                        createdSubTasks = subtasksCreationService.subtasksFromString(issueKey, inputString);
                    } catch (SyntaxFormatException e) {
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

    private void clearInputString() {
        inputString = "";
    }

}
