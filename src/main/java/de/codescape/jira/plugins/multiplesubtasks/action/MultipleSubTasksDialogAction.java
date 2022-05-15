package de.codescape.jira.plugins.multiplesubtasks.action;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import de.codescape.jira.plugins.multiplesubtasks.service.MultipleSubTasksService;
import de.codescape.jira.plugins.multiplesubtasks.model.SyntaxFormatException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static de.codescape.jira.plugins.multiplesubtasks.action.MultipleSubTasksDialogAction.Parameters.INPUT_STRING;

// TODO add documentation
// TODO create tests
public class MultipleSubTasksDialogAction extends JiraWebActionSupport {

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


    }

    private final MultipleSubTasksService multipleSubTasksService;

    private String issueKey;
    private String inputString = "";
    private List<Issue> createdSubTasks;

    @Autowired
    public MultipleSubTasksDialogAction(MultipleSubTasksService multipleSubTasksService) {
        this.multipleSubTasksService = multipleSubTasksService;
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
    protected String doExecute() {
        issueKey = getParameter(Parameters.ISSUE_KEY);
        String action = getParameter(Parameters.ACTION);
        if (action != null) {
            switch (action) {
                case Actions.CREATE:
                    inputString = getParameter(INPUT_STRING);
                    try {
                        createdSubTasks = multipleSubTasksService.subTasksFromString(issueKey, inputString);
                    } catch (SyntaxFormatException e) {
                        addErrorMessage(e.getMessage());
                        return ERROR;
                    }
                    break;
                case Actions.RESET:
                    clearInputString();
                    break;
            }
        }
        return SUCCESS;
    }

    private void clearInputString() {
        inputString = "";
    }

}
