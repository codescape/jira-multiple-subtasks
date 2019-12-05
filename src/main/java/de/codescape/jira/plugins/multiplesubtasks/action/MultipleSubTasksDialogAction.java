package de.codescape.jira.plugins.multiplesubtasks.action;

import com.atlassian.jira.web.action.JiraWebActionSupport;

public class MultipleSubTasksDialogAction extends JiraWebActionSupport {

    private static final long serialVersionUID = 1L;

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

    @Override
    protected String doExecute() {
        String action = getParameter(Parameters.ACTION);
        if (action != null && action.equals("create")) {
            System.out.println("############ CREATE pressed!");
            System.out.println("issueKey: " + getParameter("issueKey"));
            System.out.println("tasks: " + getParameter("tasks"));
        }
        return SUCCESS;
    }

}
