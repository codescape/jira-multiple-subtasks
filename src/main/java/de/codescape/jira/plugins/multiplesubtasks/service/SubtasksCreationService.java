package de.codescape.jira.plugins.multiplesubtasks.service;

import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.bc.project.component.ProjectComponent;
import com.atlassian.jira.bc.project.component.ProjectComponentManager;
import com.atlassian.jira.bc.user.search.AssigneeService;
import com.atlassian.jira.config.PriorityManager;
import com.atlassian.jira.config.SubTaskManager;
import com.atlassian.jira.exception.CreateException;
import com.atlassian.jira.issue.*;
import com.atlassian.jira.issue.customfields.manager.OptionsManager;
import com.atlassian.jira.issue.customfields.option.Option;
import com.atlassian.jira.issue.customfields.option.Options;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.label.LabelManager;
import com.atlassian.jira.issue.priority.Priority;
import com.atlassian.jira.issue.watchers.WatcherManager;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import de.codescape.jira.plugins.multiplesubtasks.model.CreatedSubtask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service to create multiple subtasks from the results of the {@link SubtasksSyntaxService} for a given issue.
 */
@Component
public class SubtasksCreationService {

    /* markers */

    private static final String INHERIT_MARKER = "@inherit";
    private static final String CURRENT_MARKER = "@current";

    /* supported custom fields */

    static final String CUSTOM_FIELD_TYPE_NUMBER = "com.atlassian.jira.plugin.system.customfieldtypes:float";
    static final String CUSTOM_FIELD_TYPE_TEXT = "com.atlassian.jira.plugin.system.customfieldtypes:textfield";
    static final String CUSTOM_FIELD_TYPE_TEXTAREA = "com.atlassian.jira.plugin.system.customfieldtypes:textarea";
    static final String CUSTOM_FIELD_TYPE_RADIO = "com.atlassian.jira.plugin.system.customfieldtypes:radiobuttons";
    static final String CUSTOM_FIELD_TYPE_SELECT = "com.atlassian.jira.plugin.system.customfieldtypes:select";
    static final String CUSTOM_FIELD_TYPE_MULTISELECT = "com.atlassian.jira.plugin.system.customfieldtypes:multiselect";

    /* dependencies */

    private final IssueService issueService;
    private final IssueFactory issueFactory;
    private final IssueManager issueManager;
    private final SubTaskManager subTaskManager;
    private final PriorityManager priorityManager;
    private final AssigneeService assigneeService;
    private final UserManager userManager;
    private final ProjectComponentManager projectComponentManager;
    private final LabelManager labelManager;
    private final JiraAuthenticationContext jiraAuthenticationContext;
    private final WatcherManager watcherManager;
    private final CustomFieldManager customFieldManager;
    private final OptionsManager optionsManager;
    private final SubtasksSyntaxService subtasksSyntaxService;
    private final EstimateStringService estimateStringService;

    @Autowired
    public SubtasksCreationService(@ComponentImport IssueService issueService,
                                   @ComponentImport IssueFactory issueFactory,
                                   @ComponentImport IssueManager issueManager,
                                   @ComponentImport SubTaskManager subTaskManager,
                                   @ComponentImport PriorityManager priorityManager,
                                   @ComponentImport AssigneeService assigneeService,
                                   @ComponentImport UserManager userManager,
                                   @ComponentImport ProjectComponentManager projectComponentManager,
                                   @ComponentImport LabelManager labelManager,
                                   @ComponentImport JiraAuthenticationContext jiraAuthenticationContext,
                                   @ComponentImport WatcherManager watcherManager,
                                   @ComponentImport CustomFieldManager customFieldManager,
                                   @ComponentImport OptionsManager optionsManager,
                                   SubtasksSyntaxService subtasksSyntaxService,
                                   EstimateStringService estimateStringService) {
        this.issueService = issueService;
        this.issueFactory = issueFactory;
        this.issueManager = issueManager;
        this.subTaskManager = subTaskManager;
        this.priorityManager = priorityManager;
        this.assigneeService = assigneeService;
        this.userManager = userManager;
        this.projectComponentManager = projectComponentManager;
        this.labelManager = labelManager;
        this.jiraAuthenticationContext = jiraAuthenticationContext;
        this.watcherManager = watcherManager;
        this.customFieldManager = customFieldManager;
        this.optionsManager = optionsManager;
        this.subtasksSyntaxService = subtasksSyntaxService;
        this.estimateStringService = estimateStringService;
    }

    /**
     * Create subtasks for the given issue key using the provided input string and return all created subtasks.
     *
     * @param issueKey    issue key to create subtasks for
     * @param inputString input string to be used
     * @return list of all created subtasks
     */
    public List<CreatedSubtask> subtasksFromString(String issueKey, String inputString) {
        ArrayList<CreatedSubtask> subtasksCreated = new ArrayList<>();

        IssueService.IssueResult issueResult = issueService.getIssue(jiraAuthenticationContext.getLoggedInUser(), issueKey);
        MutableIssue parent = issueResult.getIssue();
        if (parent == null) {
            throw new RuntimeException("Parent issue not found.");
        }

        Project projectObject = parent.getProjectObject();
        if (projectObject == null) {
            throw new RuntimeException("Parent project not found.");
        }

        List<IssueType> subTaskTypes = projectObject.getIssueTypes().stream().filter(IssueType::isSubTask).collect(Collectors.toList());
        if (subTaskTypes.isEmpty()) {
            throw new RuntimeException("No sub-task types found.");
        }

        subtasksSyntaxService.parseString(inputString).forEach(subTaskRequest -> {
            // collect warnings during subtask creation
            List<String> warnings = new ArrayList<>();

            // create new issue
            MutableIssue newSubtask = issueFactory.getIssue();

            // parent issue
            newSubtask.setParentObject(parent);

            // project
            newSubtask.setProjectObject(parent.getProjectObject());

            // summary
            newSubtask.setSummary(subTaskRequest.getSummary());

            // description
            // use the optionally provided description
            if (subTaskRequest.getDescription() != null) {
                newSubtask.setDescription(subTaskRequest.getDescription().replaceAll("\\{n}", "\n"));
            }

            // priority
            // try to find provided priority otherwise fall back to priority of parent issue
            if (subTaskRequest.getPriority() != null) {
                // TODO a priority scheme can be configured per project
                // TODO add warning for invalid priority
                Priority priority = priorityManager.getPriorities().stream()
                    .filter(availablePriority -> availablePriority.getName().equals(subTaskRequest.getPriority()))
                    .findFirst()
                    .orElse(parent.getPriority());
                newSubtask.setPriority(priority);
            } else {
                newSubtask.setPriority(parent.getPriority());
            }

            // issueType
            // try to find provided issue type otherwise fall back and use first subtask type found
            if (subTaskRequest.getIssueType() != null) {
                IssueType issueType = subTaskTypes.stream()
                    .filter(availableIssueType -> availableIssueType.getName().equals(subTaskRequest.getIssueType()))
                    .findFirst().orElse(null);
                if (issueType == null) {
                    warnings.add("Invalid issue type: " + subTaskRequest.getIssueType());
                }
                newSubtask.setIssueType(issueType);
            }
            if (newSubtask.getIssueType() == null) {
                newSubtask.setIssueType(subTaskTypes.get(0));
            }

            // assignee
            // try to find provided assignee in the list of assignable users for current project and ignore users who are not assignable
            if (subTaskRequest.getAssignee() != null) {
                if (INHERIT_MARKER.equals(subTaskRequest.getAssignee())) {
                    newSubtask.setAssignee(parent.getAssignee());
                } else if (CURRENT_MARKER.equals(subTaskRequest.getAssignee())) {
                    newSubtask.setAssignee(jiraAuthenticationContext.getLoggedInUser());
                } else {
                    newSubtask.setAssignee(assigneeService.findAssignableUsers(subTaskRequest.getAssignee(), projectObject)
                        .stream().findFirst().orElse(null));
                    if (newSubtask.getAssignee() == null) {
                        warnings.add("Invalid assignee: " + subTaskRequest.getAssignee());
                    }
                }
            }

            // reporter
            // try to find provided reporter and otherwise use the current user
            ApplicationUser reporter = null;
            if (subTaskRequest.getReporter() != null) {
                reporter = userManager.getUserByName(subTaskRequest.getReporter());
                if (reporter == null) {
                    warnings.add("Invalid reporter: " + subTaskRequest.getReporter());
                }
            }
            newSubtask.setReporter(reporter != null ? reporter : jiraAuthenticationContext.getLoggedInUser());

            // component(s)
            // add optional components to the subtask and ignore components that do not exist
            // TODO add warnings for ignored components
            if (!subTaskRequest.getComponents().isEmpty()) {
                Set<ProjectComponent> components = subTaskRequest.getComponents().stream()
                    .filter(component -> !INHERIT_MARKER.equals(component))
                    .map(component -> projectComponentManager.findByComponentName(projectObject.getId(), component))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
                if (subTaskRequest.getComponents().contains(INHERIT_MARKER)) {
                    components.addAll(parent.getComponents());
                }
                newSubtask.setComponent(components);
            }

            // estimate
            // parse the estimate and set the duration in seconds
            if (subTaskRequest.getEstimate() != null) {
                newSubtask.setEstimate(estimateStringService.estimateStringToSeconds(subTaskRequest.getEstimate()));
            }

            // create and link the subtask to the parent issue
            try {
                issueManager.createIssueObject(jiraAuthenticationContext.getLoggedInUser(), newSubtask);
                subTaskManager.createSubTaskIssueLink(parent, newSubtask, jiraAuthenticationContext.getLoggedInUser());
                subtasksCreated.add(new CreatedSubtask(newSubtask, warnings));
            } catch (CreateException e) {
                throw new RuntimeException(e);
            }

            // watcher(s)
            // try to find each watcher as a user by the provided key and ignore users who do not exist
            // TODO add warnings for ignored watchers
            if (!subTaskRequest.getWatchers().isEmpty()) {
                subTaskRequest.getWatchers().stream()
                    .map(userManager::getUserByName)
                    .filter(Objects::nonNull)
                    .forEach(watcher -> watcherManager.startWatching(watcher, newSubtask));
            }

            // label(s)
            // add optional multiple labels to the just created subtask (we need the ID of the subtask to add them)
            if (!subTaskRequest.getLabels().isEmpty()) {
                if (subTaskRequest.getLabels().contains(INHERIT_MARKER)) {
                    parent.getLabels().forEach(label ->
                        labelManager.addLabel(jiraAuthenticationContext.getLoggedInUser(), newSubtask.getId(), label.getLabel(), false)
                    );
                }
                subTaskRequest.getLabels().stream()
                    .filter(label -> !INHERIT_MARKER.equals(label))
                    .forEach(label ->
                        labelManager.addLabel(jiraAuthenticationContext.getLoggedInUser(), newSubtask.getId(), label, false)
                    );
            }

            // custom field(s)
            // persist data to optional custom field(s) of the just created subtask and ignore invalid data
            if (!subTaskRequest.getCustomFields().isEmpty()) {
                subTaskRequest.getCustomFields().forEach((customFieldId, customFieldValues) ->
                    applyValuesToCustomField(newSubtask, warnings, customFieldId, customFieldValues));
            }
        });

        return subtasksCreated;
    }

    private void applyValuesToCustomField(MutableIssue newSubtask, List<String> warnings, String customFieldId, List<String> values) {
        CustomField customField = customFieldManager.getCustomFieldObject(customFieldId);
        if (customField == null) {
            warnings.add("Invalid custom field: " + customFieldId);
        } else {
            String customFieldKey = customField.getCustomFieldType().getKey();
            switch (customFieldKey) {
                case CUSTOM_FIELD_TYPE_NUMBER:
                    if (values.size() > 1) {
                        warnings.add("Custom field only allows single values: " + customFieldId);
                    }
                    try {
                        values.forEach(value ->
                            newSubtask.setCustomFieldValue(customField, Double.valueOf(value))
                        );
                    } catch (NumberFormatException numberFormatException) {
                        warnings.add("Invalid numeric value for custom field: " + customFieldId);
                    }
                    break;
                case CUSTOM_FIELD_TYPE_TEXT:
                    if (values.size() > 1) {
                        warnings.add("Custom field only allows single values: " + customFieldId);
                    }
                    values.forEach(value -> {
                        newSubtask.setCustomFieldValue(customField, value);
                    });
                    break;
                case CUSTOM_FIELD_TYPE_TEXTAREA:
                    if (values.size() > 1) {
                        warnings.add("Custom field only allows single values: " + customFieldId);
                    }
                    values.forEach(value -> {
                        newSubtask.setCustomFieldValue(customField, value.replaceAll("\\{n}", "\n"));
                    });
                    break;
                case CUSTOM_FIELD_TYPE_SELECT:
                case CUSTOM_FIELD_TYPE_RADIO:
                    Options options = optionsManager.getOptions(customField.getRelevantConfig(newSubtask));
                    if (values.size() > 1) {
                        warnings.add("Custom field only allows single values: " + customFieldId);
                    }
                    values.forEach(value -> {
                        Option selectedOption = options.getOptionForValue(value, null);
                        if (selectedOption == null) {
                            warnings.add("Invalid option (" + value + ") for custom field: " + customFieldId);
                        } else {
                            newSubtask.setCustomFieldValue(customField, selectedOption);
                        }
                    });
                    break;
                case CUSTOM_FIELD_TYPE_MULTISELECT:
                    Options optionsM = optionsManager.getOptions(customField.getRelevantConfig(newSubtask));
                    List<Option> selectedOptions = new ArrayList<>();
                    values.forEach(value -> {
                        Option selectedOption = optionsM.getOptionForValue(value, null);
                        if (selectedOption == null) {
                            warnings.add("Invalid option (" + value + ") for custom field: " + customFieldId);
                        } else {
                            selectedOptions.add(selectedOption);
                        }
                    });
                    if (!selectedOptions.isEmpty()) {
                        newSubtask.setCustomFieldValue(customField, selectedOptions);
                    }
                    break;
                default:
                    warnings.add("Unsupported custom field type: " + customFieldKey);
            }
        }

        try {
            issueManager.updateIssue(jiraAuthenticationContext.getLoggedInUser(), newSubtask, UpdateIssueRequest.builder().build());
        } catch (RuntimeException e) {
            warnings.add("Unexpected error applying custom field values: " + e.getMessage());
        }
    }

}
