package de.codescape.jira.plugins.multiplesubtasks.action;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import de.codescape.jira.plugins.multiplesubtasks.service.MultipleSubTasksService;
import de.codescape.jira.plugins.multiplesubtasks.service.SubTaskFormatException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

// TODO add documentation
// TODO create tests
public class MultipleSubTasksDialogAction extends JiraWebActionSupport {

    private static final long serialVersionUID = 1L;

    private String issueKey;
    private List<Issue> createdSubTasks;

    private final MultipleSubTasksService multipleSubTasksService;

    @Autowired
    public MultipleSubTasksDialogAction(MultipleSubTasksService multipleSubTasksService) {
        this.multipleSubTasksService = multipleSubTasksService;
    }

    /**
     * Names of all parameters used on the global configuration page.
     */
    static final class Parameters {

        static final String ACTION = "action";

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

    public String getIssueKey() {
        return issueKey;
    }

    public List<Issue> getCreatedSubTasks() {
        return createdSubTasks;
    }

    @Override
    protected String doExecute() {
        issueKey = getParameter("issueKey");
        String action = getParameter(Parameters.ACTION);
        if (action != null && action.equals("create")) {
            String tasks = getParameter("tasks");
            try {
                createdSubTasks = multipleSubTasksService.subTasksFromString(issueKey, tasks);
            } catch (SubTaskFormatException e) {
                addErrorMessage(e.getMessage());
                return ERROR;
            }
        }
        return SUCCESS;
    }

}
