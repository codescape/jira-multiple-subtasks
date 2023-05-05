package de.codescape.jira.plugins.multiplesubtasks.workflow;

import com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory;
import com.atlassian.jira.plugin.workflow.WorkflowPluginFunctionFactory;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.FunctionDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory that is used to provide all required data to the workflow function that allows to create subtasks during a
 * workflow transition.
 */
public class CreateSubtasksWorkflowFunctionFactory extends AbstractWorkflowPluginFactory implements WorkflowPluginFunctionFactory {

    private static final Logger log = LoggerFactory.getLogger(CreateSubtasksWorkflowFunctionFactory.class);
    public static final String SUBTASKS_TEMPLATE = "subtasksTemplate";

    @Override
    protected void getVelocityParamsForInput(Map<String, Object> velocityParams) {
        // intentionally left blank
    }

    @Override
    protected void getVelocityParamsForEdit(Map<String, Object> velocityParams, AbstractDescriptor abstractDescriptor) {
        this.getVelocityParamsForInput(velocityParams);
        String subtasksTemplate = (String) ((FunctionDescriptor) abstractDescriptor).getArgs().get(SUBTASKS_TEMPLATE);
        velocityParams.put(SUBTASKS_TEMPLATE, subtasksTemplate);
    }

    @Override
    protected void getVelocityParamsForView(Map<String, Object> velocityParams, AbstractDescriptor abstractDescriptor) {
        velocityParams.put(SUBTASKS_TEMPLATE, (String) ((FunctionDescriptor) abstractDescriptor).getArgs().get(SUBTASKS_TEMPLATE));
    }

    @Override
    public Map<String, ?> getDescriptorParams(Map<String, Object> conditionParams) {
        Map<String, String> params = new HashMap<>();
        try {
            String subtasksTemplate = extractSingleParam(conditionParams, SUBTASKS_TEMPLATE);
            params.put(SUBTASKS_TEMPLATE, subtasksTemplate);
        } catch (IllegalArgumentException e) {
            log.warn(e.getMessage(), e);
        }
        return params;
    }

}
