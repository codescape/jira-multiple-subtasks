package de.codescape.jira.plugins.multiplesubtasks.service;

import com.atlassian.crowd.embedded.api.Group;
import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.bc.project.component.ProjectComponent;
import com.atlassian.jira.bc.project.component.ProjectComponentManager;
import com.atlassian.jira.bc.user.search.AssigneeService;
import com.atlassian.jira.config.PriorityManager;
import com.atlassian.jira.config.SubTaskManager;
import com.atlassian.jira.exception.CreateException;
import com.atlassian.jira.exception.IssueFieldsCharacterLimitExceededException;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.IssueFactory;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.customfields.manager.OptionsManager;
import com.atlassian.jira.issue.customfields.option.Option;
import com.atlassian.jira.issue.customfields.option.Options;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.label.Label;
import com.atlassian.jira.issue.label.LabelManager;
import com.atlassian.jira.issue.priority.Priority;
import com.atlassian.jira.issue.watchers.WatcherManager;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.version.Version;
import com.atlassian.jira.project.version.VersionManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.security.groups.GroupManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import de.codescape.jira.plugins.multiplesubtasks.model.CreatedSubtask;
import de.codescape.jira.plugins.multiplesubtasks.model.Subtask;
import de.codescape.jira.plugins.multiplesubtasks.model.SyntaxFormatException;
import de.codescape.jira.plugins.multiplesubtasks.service.syntax.DateTimeStringService;
import de.codescape.jira.plugins.multiplesubtasks.service.syntax.EstimateStringService;
import de.codescape.jira.plugins.multiplesubtasks.service.syntax.SubtasksSyntaxService;
import jakarta.inject.Inject;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import static de.codescape.jira.plugins.multiplesubtasks.model.Markers.CURRENT_MARKER;
import static de.codescape.jira.plugins.multiplesubtasks.model.Markers.INHERIT_MARKER;

/**
 * Service to create multiple subtasks from the results of the {@link SubtasksSyntaxService} for a given issue.
 */
@Component
public class SubtasksCreationService {

    /* supported custom fields */

    static final String CUSTOM_FIELD_TYPE_NUMBER = "com.atlassian.jira.plugin.system.customfieldtypes:float";
    static final String CUSTOM_FIELD_TYPE_TEXT = "com.atlassian.jira.plugin.system.customfieldtypes:textfield";
    static final String CUSTOM_FIELD_TYPE_TEXTAREA = "com.atlassian.jira.plugin.system.customfieldtypes:textarea";
    static final String CUSTOM_FIELD_TYPE_RADIO = "com.atlassian.jira.plugin.system.customfieldtypes:radiobuttons";
    static final String CUSTOM_FIELD_TYPE_SELECT = "com.atlassian.jira.plugin.system.customfieldtypes:select";
    static final String CUSTOM_FIELD_TYPE_CASCADING_SELECT = "com.atlassian.jira.plugin.system.customfieldtypes:cascadingselect";
    static final String CUSTOM_FIELD_TYPE_MULTISELECT = "com.atlassian.jira.plugin.system.customfieldtypes:multiselect";
    static final String CUSTOM_FIELD_TYPE_CHECKBOXES = "com.atlassian.jira.plugin.system.customfieldtypes:multicheckboxes";
    static final String CUSTOM_FIELD_TYPE_URL = "com.atlassian.jira.plugin.system.customfieldtypes:url";
    static final String CUSTOM_FIELD_TYPE_USER = "com.atlassian.jira.plugin.system.customfieldtypes:userpicker";
    static final String CUSTOM_FIELD_TYPE_USERS = "com.atlassian.jira.plugin.system.customfieldtypes:multiuserpicker";
    static final String CUSTOM_FIELD_TYPE_DATE = "com.atlassian.jira.plugin.system.customfieldtypes:datepicker";
    static final String CUSTOM_FIELD_TYPE_DATETIME = "com.atlassian.jira.plugin.system.customfieldtypes:datetime";
    static final String CUSTOM_FIELD_TYPE_LABELS = "com.atlassian.jira.plugin.system.customfieldtypes:labels";
    static final String CUSTOM_FIELD_TYPE_GROUP = "com.atlassian.jira.plugin.system.customfieldtypes:grouppicker";
    static final String CUSTOM_FIELD_TYPE_GROUPS = "com.atlassian.jira.plugin.system.customfieldtypes:multigrouppicker";

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
    private final VersionManager versionManager;
    private final GroupManager groupManager;
    private final SubtasksSyntaxService subtasksSyntaxService;
    private final EstimateStringService estimateStringService;
    private final DateTimeStringService dateTimeStringService;

    @Inject
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
                                   @ComponentImport VersionManager versionManager,
                                   @ComponentImport GroupManager groupManager,
                                   SubtasksSyntaxService subtasksSyntaxService,
                                   EstimateStringService estimateStringService,
                                   DateTimeStringService dateTimeStringService) {
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
        this.versionManager = versionManager;
        this.groupManager = groupManager;
        this.subtasksSyntaxService = subtasksSyntaxService;
        this.estimateStringService = estimateStringService;
        this.dateTimeStringService = dateTimeStringService;
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

        if (parent.isSubTask()) {
            throw new RuntimeException("Cannot create subtasks for a subtask.");
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

            // project (always same as parent)
            newSubtask.setProjectObject(parent.getProjectObject());

            // issue level security
            applySecurityLevel(parent, newSubtask);

            // summary
            applySummary(subTaskRequest, newSubtask, parent);

            // description
            applyDescription(subTaskRequest, parent, newSubtask);

            // priority
            applyPriority(subTaskRequest, newSubtask, parent, warnings);

            // issueType
            applyIssueType(subTaskRequest, subTaskTypes, warnings, newSubtask);

            // assignee
            applyAssignee(subTaskRequest, newSubtask, parent, projectObject, warnings);

            // fixVersion(s)
            applyFixVersions(subTaskRequest, parent, warnings, newSubtask);

            // affectedVersion(s)
            applyAffectedVersions(subTaskRequest, parent, warnings, newSubtask);

            // dueDate
            applyDueDate(subTaskRequest, parent, newSubtask, warnings);

            // reporter
            applyReporter(subTaskRequest, parent, warnings, newSubtask);

            // component(s)
            applyComponents(subTaskRequest, projectObject, warnings, parent, newSubtask);

            // estimate
            applyEstimate(subTaskRequest, parent, newSubtask);

            // custom field(s) by id
            // persist data to optional custom field(s) of the just created subtask and ignore invalid data
            if (!subTaskRequest.getCustomFieldsById().isEmpty()) {
                subTaskRequest.getCustomFieldsById().forEach((customFieldId, customFieldValues) ->
                    applyValuesToCustomFieldByCustomFieldId(parent, newSubtask, warnings, customFieldId, customFieldValues));
            }

            // custom field(s) by name
            // persist data to optional custom field(s) of the just created subtask and ignore invalid data
            if (!subTaskRequest.getCustomFieldsByName().isEmpty()) {
                subTaskRequest.getCustomFieldsByName().forEach((customFieldName, customFieldValues) ->
                    applyValuesToCustomFieldByCustomFieldName(parent, newSubtask, warnings, customFieldName, customFieldValues));
            }

            // create and link the subtask to the parent issue
            try {
                issueManager.createIssueObject(jiraAuthenticationContext.getLoggedInUser(), newSubtask);
                subTaskManager.createSubTaskIssueLink(parent, newSubtask, jiraAuthenticationContext.getLoggedInUser());
                subtasksCreated.add(new CreatedSubtask(newSubtask, warnings));
            } catch (IssueFieldsCharacterLimitExceededException e) {
                throw new SyntaxFormatException("Character limited exceeded: " + String.join(",", e.getInvalidFieldIds()), e);
            } catch (RuntimeException | CreateException e) {
                if (e.getMessage() != null) {
                    throw new SyntaxFormatException("Error during creation of subtask: " + e.getMessage(), e);
                } else {
                    throw new SyntaxFormatException("Error during creation of subtask.", e);
                }
            }

            // label(s)
            applyLabels(subTaskRequest, parent, newSubtask);

            // watcher(s)
            applyWatchers(subTaskRequest, newSubtask, parent, warnings);
        });

        return subtasksCreated;
    }

    /**
     * Any new subtask inherits the security level of the parent issue.
     */
    private static void applySecurityLevel(MutableIssue parent, MutableIssue newSubtask) {
        if (parent.getSecurityLevelId() != null) {
            newSubtask.setSecurityLevelId(parent.getSecurityLevelId());
        }
    }

    /**
     * We try to find each watcher as a user by the provided key and ignore users who do not exist.
     */
    private void applyWatchers(Subtask subTaskRequest, MutableIssue newSubtask, MutableIssue parent, List<String> warnings) {
        if (!subTaskRequest.getWatchers().isEmpty()) {
            if (subTaskRequest.getWatchers().contains(CURRENT_MARKER)) {
                watcherManager.startWatching(jiraAuthenticationContext.getLoggedInUser(), newSubtask);
            } else if (subTaskRequest.getWatchers().contains(INHERIT_MARKER)) {
                watcherManager.getWatchersUnsorted(parent).forEach(user ->
                    watcherManager.startWatching(user, newSubtask)
                );
            }
            subTaskRequest.getWatchers().stream()
                .filter(watcher -> !INHERIT_MARKER.equals(watcher))
                .filter(watcher -> !CURRENT_MARKER.equals(watcher))
                .forEach(watcher -> {
                    ApplicationUser userByName = userManager.getUserByName(watcher);
                    if (userByName != null) {
                        watcherManager.startWatching(userByName, newSubtask);
                    } else {
                        warnings.add("Invalid watcher: " + watcher);
                    }
                });
        }
    }

    /**
     * We add optional multiple labels to the just created subtask (we need the ID of the subtask to add them so we do
     * that after creation and before adding watchers)
     */
    private void applyLabels(Subtask subTaskRequest, MutableIssue parent, MutableIssue newSubtask) {
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
    }

    /**
     * If an estimate is given we need to parse it and calculate the estimate value from it (duration in seconds).
     */
    private void applyEstimate(Subtask subTaskRequest, MutableIssue parent, MutableIssue newSubtask) {
        if (subTaskRequest.getEstimate() != null) {
            if (INHERIT_MARKER.equals(subTaskRequest.getEstimate())) {
                if (parent.getEstimate() != null) {
                    newSubtask.setEstimate(parent.getEstimate());
                }
                if (parent.getOriginalEstimate() != null) {
                    newSubtask.setOriginalEstimate(parent.getOriginalEstimate());
                }
            } else {
                Long estimateToSet = estimateStringService.estimateStringToSeconds(subTaskRequest.getEstimate());
                newSubtask.setEstimate(estimateToSet);
                newSubtask.setOriginalEstimate(estimateToSet);
            }
        }
    }

    /**
     * Every subtasks needs to have a summary. We are trying to replace inherit markers if available and apply the
     * summary to the new subtask.
     */
    private static void applySummary(Subtask subTaskRequest, MutableIssue newSubtask, MutableIssue parent) {
        newSubtask.setSummary(subTaskRequest.getSummary()
            .replaceAll("(?<!\\\\)" + INHERIT_MARKER, parent.getSummary())
            .replaceAll("\\\\" + INHERIT_MARKER, INHERIT_MARKER)
        );
    }

    /**
     * If a description has been provided in the subtask request we try to find and replace inherit markers and apply
     * this description to the new subtask.
     */
    private static void applyDescription(Subtask subTaskRequest, MutableIssue parent, MutableIssue newSubtask) {
        if (subTaskRequest.getDescription() != null) {
            String descriptionFromParent = parent.getDescription() != null ? parent.getDescription() : "";
            String description = subTaskRequest.getDescription()
                .replaceAll("\\{n}", "\n")
                .replaceAll("(?<!\\\\)" + INHERIT_MARKER, descriptionFromParent)
                .replaceAll("\\\\" + INHERIT_MARKER, INHERIT_MARKER)
                .trim();
            if (!description.isEmpty()) {
                newSubtask.setDescription(description);
            }
        }
    }

    /**
     * We add  components to the subtask if given in the request and ignore components that do not exist with a warning.
     */
    private void applyComponents(Subtask subTaskRequest, Project projectObject, List<String> warnings, MutableIssue parent, MutableIssue newSubtask) {
        if (!subTaskRequest.getComponents().isEmpty()) {
            Set<ProjectComponent> components = new HashSet<>();
            subTaskRequest.getComponents().forEach(component -> {
                if (!INHERIT_MARKER.equals(component)) {
                    ProjectComponent foundComponent = projectComponentManager.findByComponentName(projectObject.getId(), component);
                    if (foundComponent == null) {
                        warnings.add("Invalid component: " + component);
                    } else {
                        components.add(foundComponent);
                    }
                }
            });
            if (subTaskRequest.getComponents().contains(INHERIT_MARKER)) {
                components.addAll(parent.getComponents());
            }
            if (!components.isEmpty()) {
                newSubtask.setComponent(components);
            }
        }
    }

    /**
     * As any task needs to have a reporter we try to find the provided reporter and otherwise use the current user.
     */
    private void applyReporter(Subtask subTaskRequest, MutableIssue parent, List<String> warnings, MutableIssue newSubtask) {
        ApplicationUser reporter = null;
        if (subTaskRequest.getReporter() != null) {
            if (INHERIT_MARKER.equals(subTaskRequest.getReporter())) {
                reporter = parent.getReporter();
            } else if (CURRENT_MARKER.equals(subTaskRequest.getReporter())) {
                reporter = jiraAuthenticationContext.getLoggedInUser();
            } else {
                reporter = userManager.getUserByName(subTaskRequest.getReporter());
                if (reporter == null) {
                    warnings.add("Invalid reporter: " + subTaskRequest.getReporter());
                }
            }
        }
        newSubtask.setReporter(reporter != null ? reporter : jiraAuthenticationContext.getLoggedInUser());
    }

    /**
     * If a due date is given we can set it.
     */
    private void applyDueDate(Subtask subTaskRequest, MutableIssue parent, MutableIssue newSubtask, List<String> warnings) {
        if (subTaskRequest.getDueDate() != null) {
            Timestamp timestamp = dateTimeStringService.dateStringToTimestamp(subTaskRequest.getDueDate(), parent.getDueDate());
            if (timestamp != null) {
                newSubtask.setDueDate(timestamp);
            } else {
                warnings.add("Invalid dueDate: " + subTaskRequest.getDueDate());
            }
        }
    }

    /**
     * We add provided affected versions if they exist in the project and ignore non-existing versions with a warning.
     */
    private void applyAffectedVersions(Subtask subTaskRequest, MutableIssue parent, List<String> warnings, MutableIssue newSubtask) {
        if (!subTaskRequest.getAffectedVersions().isEmpty()) {
            List<Version> availableVersions = versionManager.getVersions(parent.getProjectObject());
            List<Version> affectedVersions = new ArrayList<>();
            subTaskRequest.getAffectedVersions().forEach(requestedVersion -> {
                if (INHERIT_MARKER.equals(requestedVersion) && !parent.getAffectedVersions().isEmpty()) {
                    affectedVersions.addAll(parent.getAffectedVersions());
                } else {
                    Version affectedVersion = availableVersions.stream()
                        .filter(version -> version.getName().equals(requestedVersion))
                        .findFirst()
                        .orElse(null);
                    if (affectedVersion == null) {
                        warnings.add("Invalid affectedVersion: " + requestedVersion);
                    } else {
                        affectedVersions.add(affectedVersion);
                    }
                }
            });
            if (!affectedVersions.isEmpty()) {
                newSubtask.setAffectedVersions(affectedVersions);
            }
        }
    }

    /**
     * We try to find provided assignee as a user and check whether he is allowed to be assigned in the current project.
     * In case of an error we ignore the assignee with a warning.
     */
    private void applyAssignee(Subtask subTaskRequest, MutableIssue newSubtask, MutableIssue parent, Project projectObject, List<String> warnings) {
        if (subTaskRequest.getAssignee() != null) {
            if (INHERIT_MARKER.equals(subTaskRequest.getAssignee())) {
                newSubtask.setAssignee(parent.getAssignee());
            } else if (CURRENT_MARKER.equals(subTaskRequest.getAssignee())) {
                newSubtask.setAssignee(jiraAuthenticationContext.getLoggedInUser());
            } else {
                ApplicationUser assignee = userManager.getUserByName(subTaskRequest.getAssignee());
                if (assignee != null) {
                    if (assigneeService.isAssignable(projectObject, assignee)) {
                        newSubtask.setAssignee(assignee);
                    } else {
                        warnings.add("User is no valid assignee: " + subTaskRequest.getAssignee());
                    }
                } else {
                    warnings.add("User not found: " + subTaskRequest.getAssignee());
                }
            }
        }
    }

    /**
     * We try to find provided issue type otherwise fall back and use first subtask type found.
     */
    private static void applyIssueType(Subtask subTaskRequest, List<IssueType> subTaskTypes, List<String> warnings, MutableIssue newSubtask) {
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
    }

    /**
     * We try to find provided priority otherwise fall back to priority of parent issue.
     */
    private void applyPriority(Subtask subTaskRequest, MutableIssue newSubtask, MutableIssue parent, List<String> warnings) {
        if (subTaskRequest.getPriority() != null) {
            if (INHERIT_MARKER.equals(subTaskRequest.getPriority())) {
                newSubtask.setPriority(parent.getPriority());
            } else {
                // TODO a priority scheme can be configured per project
                Priority priority = priorityManager.getPriorities().stream()
                    .filter(availablePriority -> availablePriority.getName().equals(subTaskRequest.getPriority()))
                    .findFirst()
                    .orElse(null);
                if (priority == null) {
                    warnings.add("Invalid priority: " + subTaskRequest.getPriority());
                    newSubtask.setPriority(parent.getPriority());
                } else {
                    newSubtask.setPriority(priority);
                }
            }
        } else {
            newSubtask.setPriority(parent.getPriority());
        }
    }

    /**
     * We add provided fix versions if they exist in the project and ignore non-existing versions with a warning.
     */
    private void applyFixVersions(Subtask subTaskRequest, MutableIssue parent, List<String> warnings, MutableIssue newSubtask) {
        if (!subTaskRequest.getFixVersions().isEmpty()) {
            List<Version> availableVersions = versionManager.getVersions(parent.getProjectObject());
            List<Version> fixVersions = new ArrayList<>();
            subTaskRequest.getFixVersions().forEach(requestedVersion -> {
                if (INHERIT_MARKER.equals(requestedVersion) && !parent.getFixVersions().isEmpty()) {
                    fixVersions.addAll(parent.getFixVersions());
                } else {
                    Version foundVersion = availableVersions.stream()
                        .filter(version -> version.getName().equals(requestedVersion))
                        .findFirst()
                        .orElse(null);
                    if (foundVersion == null) {
                        warnings.add("Invalid fixVersion: " + requestedVersion);
                    } else {
                        fixVersions.add(foundVersion);
                    }
                }
            });
            if (!fixVersions.isEmpty()) {
                newSubtask.setFixVersions(fixVersions);
            }
        }
    }

    private void applyValuesToCustomFieldByCustomFieldName(MutableIssue parent, MutableIssue newSubtask, List<String> warnings, String customFieldName, List<String> values) {
        Collection<CustomField> customFields = customFieldManager.getCustomFieldObjectsByName(customFieldName);
        if (customFields.isEmpty()) {
            warnings.add("Invalid custom field: " + customFieldName);
        } else if (customFields.size() == 1) {
            applyValuesToCustomField(parent, newSubtask, warnings, customFields.iterator().next(), values);
        } else {
            warnings.add("Custom field name is not unique: " + customFieldName);
        }
    }

    private void applyValuesToCustomFieldByCustomFieldId(MutableIssue parent, MutableIssue newSubtask, List<String> warnings, String customFieldId, List<String> values) {
        CustomField customField = customFieldManager.getCustomFieldObject(customFieldId);
        if (customField == null) {
            warnings.add("Invalid custom field: " + customFieldId);
        } else {
            applyValuesToCustomField(parent, newSubtask, warnings, customField, values);
        }
    }

    private void applyValuesToCustomField(MutableIssue parent, MutableIssue newSubtask, List<String> warnings, CustomField customField, List<String> values) {
        String customFieldType = customField.getCustomFieldType().getKey();
        String customFieldName = customField.getFieldName();
        switch (customFieldType) {
            case CUSTOM_FIELD_TYPE_NUMBER:
                if (values.size() > 1) {
                    warnings.add("Custom field only allows single values: " + customFieldName);
                } else {
                    try {
                        values.forEach(value -> {
                            if (INHERIT_MARKER.equals(value)) {
                                if (parent.getCustomFieldValue(customField) != null) {
                                    newSubtask.setCustomFieldValue(customField, parent.getCustomFieldValue(customField));
                                }
                            } else {
                                newSubtask.setCustomFieldValue(customField, Double.valueOf(value));
                            }
                        });
                    } catch (NumberFormatException numberFormatException) {
                        warnings.add("Invalid numeric value for custom field: " + customFieldName);
                    }
                }
                break;
            case CUSTOM_FIELD_TYPE_TEXT:
                if (values.size() > 1) {
                    warnings.add("Custom field only allows single values: " + customFieldName);
                } else {
                    String parentValue = parent.getCustomFieldValue(customField) != null ? (String) parent.getCustomFieldValue(customField) : "";
                    values.forEach(value -> {
                        String cleanedUpString = value
                            .replaceAll("(?<!\\\\)" + INHERIT_MARKER, parentValue)
                            .replaceAll("\\\\" + INHERIT_MARKER, INHERIT_MARKER)
                            .trim();
                        if (!cleanedUpString.isEmpty()) {
                            newSubtask.setCustomFieldValue(customField, cleanedUpString);
                        }
                    });
                }
                break;
            case CUSTOM_FIELD_TYPE_URL:
                if (values.size() > 1) {
                    warnings.add("Custom field only allows single values: " + customFieldName);
                } else {
                    values.forEach(value -> {
                        if (INHERIT_MARKER.equals(value)) {
                            if (parent.getCustomFieldValue(customField) != null) {
                                newSubtask.setCustomFieldValue(customField, parent.getCustomFieldValue(customField));
                            }
                        } else {
                            if (isValidURL(value)) {
                                newSubtask.setCustomFieldValue(customField, value);
                            } else {
                                warnings.add("Invalid url value for custom field: " + customFieldName);
                            }
                        }
                    });
                }
                break;
            case CUSTOM_FIELD_TYPE_TEXTAREA:
                if (values.size() > 1) {
                    warnings.add("Custom field only allows single values: " + customFieldName);
                } else {
                    String parentValue = parent.getCustomFieldValue(customField) != null ? (String) parent.getCustomFieldValue(customField) : "";
                    values.forEach(value -> {
                        String cleanedUpString = value.replaceAll("\\{n}", "\n")
                            .replaceAll("(?<!\\\\)" + INHERIT_MARKER, parentValue)
                            .replaceAll("\\\\" + INHERIT_MARKER, INHERIT_MARKER)
                            .trim();
                        if (!cleanedUpString.isEmpty()) {
                            newSubtask.setCustomFieldValue(customField, cleanedUpString);
                        }
                    });
                }
                break;
            case CUSTOM_FIELD_TYPE_SELECT:
            case CUSTOM_FIELD_TYPE_RADIO:
                Options options = optionsManager.getOptions(customField.getRelevantConfig(newSubtask));
                if (values.size() > 1) {
                    warnings.add("Custom field only allows single values: " + customFieldName);
                } else {
                    values.forEach(value -> {
                        if (INHERIT_MARKER.equals(value)) {
                            Object optionFromParent = parent.getCustomFieldValue(customField);
                            if (optionFromParent instanceof Option) {
                                newSubtask.setCustomFieldValue(customField, optionFromParent);
                            }
                        } else {
                            Option selectedOption = options.getOptionForValue(value, null);
                            if (selectedOption == null) {
                                warnings.add("Invalid option (" + value + ") for custom field: " + customFieldName);
                            } else {
                                newSubtask.setCustomFieldValue(customField, selectedOption);
                            }
                        }
                    });
                }
                break;
            case CUSTOM_FIELD_TYPE_CASCADING_SELECT:
                if (values.size() > 1) {
                    warnings.add("Custom field only allows single values: " + customFieldName);
                } else {
                    values.forEach(value -> {
                        if (INHERIT_MARKER.equals(value)) {
                            Object optionFromParent = parent.getCustomFieldValue(customField);
                            if (optionFromParent instanceof Map) {
                                newSubtask.setCustomFieldValue(customField, optionFromParent);
                            }
                        } else {
                            Map<String, Option> newOptions = new HashMap<>();
                            String[] strings = value.split(" > ");
                            List<Option> allowedOptions = null;
                            boolean applyNewOptions = true;
                            for (int i = 0; i < strings.length; i++) {
                                // get allowed options for the first level
                                if (i == 0) {
                                    allowedOptions = optionsManager.getOptions(customField.getRelevantConfig(newSubtask));
                                }
                                int finalI = i;
                                Option foundOption = allowedOptions.stream()
                                    .filter(option -> option.getValue().equals(strings[finalI].trim()))
                                    .findFirst()
                                    .orElse(null);
                                if (foundOption == null) {
                                    warnings.add("Invalid option (" + value + ") for custom field: " + customFieldName);
                                    applyNewOptions = false;
                                    break;
                                } else {
                                    // add the option to the map (key for first level = null; key for any other level = level)
                                    newOptions.put(newOptions.isEmpty() ? null : "" + i, foundOption);
                                    // update allowed options for the next level
                                    allowedOptions = foundOption.getChildOptions();
                                }
                            }
                            if (applyNewOptions && !newOptions.isEmpty()) {
                                newSubtask.setCustomFieldValue(customField, newOptions);
                            }
                        }
                    });
                }
                break;
            case CUSTOM_FIELD_TYPE_MULTISELECT:
            case CUSTOM_FIELD_TYPE_CHECKBOXES:
                Options optionsM = optionsManager.getOptions(customField.getRelevantConfig(newSubtask));
                List<Option> selectedOptions = new ArrayList<>();
                values.forEach(value -> {
                    if (INHERIT_MARKER.equals(value)) {
                        Object optionsFromParent = parent.getCustomFieldValue(customField);
                        if (optionsFromParent instanceof List) {
                            //noinspection unchecked
                            selectedOptions.addAll((List<Option>) optionsFromParent);
                        }
                    } else {
                        Option selectedOption = optionsM.getOptionForValue(value, null);
                        if (selectedOption == null) {
                            warnings.add("Invalid option (" + value + ") for custom field: " + customFieldName);
                        } else {
                            selectedOptions.add(selectedOption);
                        }
                    }
                });
                if (!selectedOptions.isEmpty()) {
                    newSubtask.setCustomFieldValue(customField, selectedOptions);
                }
                break;
            case CUSTOM_FIELD_TYPE_DATE:
                if (values.size() > 1) {
                    warnings.add("Custom field only allows single values: " + customFieldName);
                } else {
                    values.forEach(value -> {
                        if (INHERIT_MARKER.equals(value)) {
                            if (parent.getCustomFieldValue(customField) != null) {
                                newSubtask.setCustomFieldValue(customField, parent.getCustomFieldValue(customField));
                            }
                        } else {
                            Timestamp parentValue = (Timestamp) parent.getCustomFieldValue(customField);
                            Timestamp timestamp = dateTimeStringService.dateStringToTimestamp(value, parentValue);
                            if (timestamp != null) {
                                newSubtask.setCustomFieldValue(customField, timestamp);
                            } else {
                                warnings.add("Invalid date (" + value + ") for custom field: " + customFieldName);
                            }
                        }
                    });
                }
                break;
            case CUSTOM_FIELD_TYPE_DATETIME:
                if (values.size() > 1) {
                    warnings.add("Custom field only allows single values: " + customFieldName);
                } else {
                    values.forEach(value -> {
                        if (INHERIT_MARKER.equals(value)) {
                            if (parent.getCustomFieldValue(customField) != null) {
                                newSubtask.setCustomFieldValue(customField, parent.getCustomFieldValue(customField));
                            }
                        } else {
                            Timestamp parentValue = (Timestamp) parent.getCustomFieldValue(customField);
                            Timestamp timestamp = dateTimeStringService.dateAndTimeStringToTimestamp(value, parentValue);
                            if (timestamp != null) {
                                newSubtask.setCustomFieldValue(customField, timestamp);
                            } else {
                                warnings.add("Invalid date and time (" + value + ") for custom field: " + customFieldName);
                            }
                        }
                    });
                }
                break;
            case CUSTOM_FIELD_TYPE_USER:
                if (values.size() > 1) {
                    warnings.add("Custom field only allows single values: " + customFieldName);
                } else {
                    values.forEach(value -> {
                        if (INHERIT_MARKER.equals(value)) {
                            if (parent.getCustomFieldValue(customField) != null) {
                                newSubtask.setCustomFieldValue(customField, parent.getCustomFieldValue(customField));
                            }
                        } else {
                            ApplicationUser userByName = userManager.getUserByName(value);
                            if (userByName == null) {
                                warnings.add("Invalid user (" + value + ") for custom field: " + customFieldName);
                            } else {
                                newSubtask.setCustomFieldValue(customField, userByName);
                            }
                        }
                    });
                }
                break;
            case CUSTOM_FIELD_TYPE_USERS:
                List<ApplicationUser> users = new ArrayList<>();
                values.forEach(value -> {
                    if (INHERIT_MARKER.equals(value)) {
                        Object usersFromParent = parent.getCustomFieldValue(customField);
                        if (usersFromParent instanceof List) {
                            //noinspection unchecked
                            users.addAll((List<ApplicationUser>) usersFromParent);
                        }
                    } else {
                        ApplicationUser userByName = userManager.getUserByName(value);
                        if (userByName == null) {
                            warnings.add("Invalid user (" + value + ") for custom field: " + customFieldName);
                        } else {
                            users.add(userByName);
                        }
                    }
                    if (!users.isEmpty()) {
                        newSubtask.setCustomFieldValue(customField, users);
                    }
                });
                break;
            case CUSTOM_FIELD_TYPE_LABELS:
                Set<Label> labels = new HashSet<>();
                values.forEach(label -> {
                    String cleanLabel = label.trim();
                    if (INHERIT_MARKER.equals(cleanLabel)) {
                        Object labelsFromParent = parent.getCustomFieldValue(customField);
                        if (labelsFromParent instanceof Set) {
                            //noinspection unchecked
                            labels.addAll((Set<Label>) labelsFromParent);
                        }
                    } else {
                        if (cleanLabel.contains(" ")) {
                            warnings.add("Invalid label (" + cleanLabel + ") contains whitespace for custom field: " + customFieldName);
                        } else if (cleanLabel.length() > 255) {
                            warnings.add("Invalid label (" + cleanLabel + ") too long for custom field: " + customFieldName);
                        } else {
                            labels.add(new Label(null, newSubtask.getId(), customField.getIdAsLong(), label));
                        }
                    }
                });
                if (!labels.isEmpty()) {
                    newSubtask.setCustomFieldValue(customField, labels);
                }
                break;
            case CUSTOM_FIELD_TYPE_GROUP:
                if (values.size() > 1) {
                    warnings.add("Custom field only allows single values: " + customFieldName);
                } else {
                    values.forEach(value -> {
                        if (INHERIT_MARKER.equals(value)) {
                            if (parent.getCustomFieldValue(customField) != null) {
                                newSubtask.setCustomFieldValue(customField, parent.getCustomFieldValue(customField));
                            }
                        } else {
                            Group group = groupManager.getGroup(value);
                            if (group != null) {
                                newSubtask.setCustomFieldValue(customField, Collections.singletonList(group));
                            } else {
                                warnings.add("Invalid group (" + value + ") for custom field: " + customFieldName);
                            }
                        }
                    });
                }
                break;
            case CUSTOM_FIELD_TYPE_GROUPS:
                Collection<Group> groups = new ArrayList<>();
                values.forEach(groupName -> {
                    if (INHERIT_MARKER.equals(groupName)) {
                        Object groupsFromParent = parent.getCustomFieldValue(customField);
                        if (groupsFromParent instanceof List) {
                            //noinspection unchecked
                            groups.addAll((List<Group>) groupsFromParent);
                        }
                    } else {
                        Group group = groupManager.getGroup(groupName);
                        if (group != null) {
                            groups.add(group);
                        } else {
                            warnings.add("Invalid group (" + groupName + ") for custom field: " + customFieldName);
                        }
                    }
                    if (!groups.isEmpty()) {
                        newSubtask.setCustomFieldValue(customField, groups);
                    }
                });
                break;
            default:
                warnings.add("Unsupported custom field type (" + customFieldType + ") for custom field: " + customFieldName);
        }
    }

    /**
     * Verify the provided URL string is a valid URL to be persisted to a custom field of type URL.
     */
    private boolean isValidURL(String url) {
        try {
            new URL(url).toURI();
            return true;
        } catch (MalformedURLException | URISyntaxException e) {
            return false;
        }
    }

}
