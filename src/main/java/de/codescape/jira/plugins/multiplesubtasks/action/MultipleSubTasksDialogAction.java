package de.codescape.jira.plugins.multiplesubtasks.action;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import de.codescape.jira.plugins.multiplesubtasks.service.SubTaskService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

// TODO add documentation
// TODO create tests
public class MultipleSubTasksDialogAction extends JiraWebActionSupport {

    private static final long serialVersionUID = 1L;

    private String issueKey;
    private List<Issue> createdSubTasks;

    private final SubTaskService subTaskService;

    @Autowired
    public MultipleSubTasksDialogAction(SubTaskService subTaskService) {
        this.subTaskService = subTaskService;
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
        System.out.println("doExecute in " + getClass().getSimpleName() + " called:");

        issueKey = getParameter("issueKey");
        System.out.println(" ~ issueKey: " + issueKey);

        String action = getParameter(Parameters.ACTION);
        System.out.println(" ~ action: " + action);
        if (action != null && action.equals("create")) {
            String tasks = getParameter("tasks");
            System.out.println(" ~ tasks: " + tasks);
            createdSubTasks = subTaskService.subTasksFromString(issueKey, tasks);
            createdSubTasks.forEach(System.out::println);
        }
        return SUCCESS;
    }

}
