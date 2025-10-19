package de.codescape.jira.plugins.multiplesubtasks.workflow;

import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.workflow.function.issue.AbstractJiraFunctionProvider;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;
import de.codescape.jira.plugins.multiplesubtasks.model.CreatedSubtask;
import de.codescape.jira.plugins.multiplesubtasks.service.SubtasksCreationService;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static de.codescape.jira.plugins.multiplesubtasks.workflow.CreateSubtasksWorkflowFunctionFactory.SUBTASKS_TEMPLATE;

/**
 * Workflow function that allows to create subtasks during a workflow transition using a workflow post function.
 */
public class CreateSubtasksWorkflowFunction extends AbstractJiraFunctionProvider {

    private static final Logger log = LoggerFactory.getLogger(CreateSubtasksWorkflowFunction.class);

    private final SubtasksCreationService subtasksCreationService;

    @Inject
    public CreateSubtasksWorkflowFunction(SubtasksCreationService subtasksCreationService) {
        this.subtasksCreationService = subtasksCreationService;
    }

    @Override
    public void execute(Map transientVars, Map args, PropertySet propertySet) throws WorkflowException {
        // get current issue
        MutableIssue issue = getIssue(transientVars);

        // get template
        String subtasksTemplate = (String) args.get(SUBTASKS_TEMPLATE);

        // create subtasks
        log.info("Creating subtasks in workflow transition for issue {}", issue.getKey());
        try {
            List<CreatedSubtask> createdSubtasks = subtasksCreationService.subtasksFromString(issue.getKey(), subtasksTemplate);
            log.info("Created {} subtasks for issue {}", createdSubtasks.size(), issue.getKey());
        } catch (RuntimeException e) {
            log.error("Error while creating subtasks for issue {}", issue.getKey(), e);
        }
    }

}
