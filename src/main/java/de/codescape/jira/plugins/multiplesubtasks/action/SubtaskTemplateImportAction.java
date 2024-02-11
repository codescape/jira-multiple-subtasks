package de.codescape.jira.plugins.multiplesubtasks.action;

import com.atlassian.jira.bc.user.search.UserSearchParams;
import com.atlassian.jira.bc.user.search.UserSearchService;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.security.request.RequestMethod;
import com.atlassian.jira.security.request.SupportedMethods;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import de.codescape.jira.plugins.multiplesubtasks.ao.SubtaskTemplate;
import de.codescape.jira.plugins.multiplesubtasks.model.ShowSubtaskTemplate;
import de.codescape.jira.plugins.multiplesubtasks.model.Subtask;
import de.codescape.jira.plugins.multiplesubtasks.service.SubtaskTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This action allows to run template imports for Multiple Subtasks for Jira.
 */
public class SubtaskTemplateImportAction extends JiraWebActionSupport {

    private static final String QUICK_SUBTASKS_PROJECT_TEMPLATES = "com.hascode.plugin.jira:subtask-templates";
    private static final String QUICK_SUBTASKS_USER_TEMPLATES_PREFIX = "subtasks-user-";
    private static final Pattern QUICK_SUBTASKS_TASK_PATTERN = Pattern.compile("^\\s*- *([^/]+)/?(?:\\s*(?: ([^:]+):\"([^\"]+)\")?\\s*/?\\s*(?: ([^:]+):\"([^\"]+)\")?\\s*/?\\s*(?: ([^:]+):\"([^\"]+)\")?\\s*/?\\s*(?: ([^:]+):\"([^\"]+)\")?\\s*/?\\s*(?: ([^:]+):\"([^\"]+)\")?\\s*/?\\s*(?: ([^:]+):\"([^\"]+)\")?\\s*/?\\s*(?: ([^:]+):\"([^\"]+)\")?\\s*/?\\s*(?: ([^:]+):\"([^\"]+)\")?\\s*/?\\s*(?: ([^:]+):\"([^\"]+)\")?\\s*/?\\s*(?: ([^:]+):\"([^\"]+)\")?\\s*/?\\s*(?: ([^:]+):\"([^\"]+)\")?\\s*/?\\s*(?: ([^:]+):\"([^\"]+)\")?\\s*/?\\s*(?: ([^:]+):\"([^\"]+)\")?\\s*/?\\s*(?: ([^:]+):\"([^\"]+)\")?\\s*/?\\s*(?: ([^:]+):\"([^\"]+)\")?\\s*/?\\s*(?: ([^:]+):\"([^\"]+)\")?\\s*/?\\s*|[^\\r\\n]*)$");
    private static final String NEWLINE = "\n";

    private final UserSearchService userSearchService;
    private final PluginSettingsFactory pluginSettingsFactory;
    private final ProjectManager projectManager;
    private final JiraAuthenticationContext jiraAuthenticationContext;
    private final SubtaskTemplateService subtaskTemplateService;

    private final StringWriter output = new StringWriter();
    private boolean quickSubtasksCanImport;

    @Autowired
    public SubtaskTemplateImportAction(@ComponentImport UserSearchService userSearchService,
                                       @ComponentImport PluginSettingsFactory pluginSettingsFactory,
                                       @ComponentImport ProjectManager projectManager,
                                       @ComponentImport JiraAuthenticationContext jiraAuthenticationContext,
                                       SubtaskTemplateService subtaskTemplateService) {
        this.userSearchService = userSearchService;
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.projectManager = projectManager;
        this.jiraAuthenticationContext = jiraAuthenticationContext;
        this.subtaskTemplateService = subtaskTemplateService;
    }

    /**
     * Display the overview page.
     */
    @Override
    @SupportedMethods({RequestMethod.GET})
    public String doDefault() {
        return SUCCESS;
    }

    /**
     * Perform a pre-check for template import from Quick Subtasks for Jira.
     */
    @SupportedMethods({RequestMethod.GET})
    public String doQuickSubtasksPreCheck() {
        output.append("Scanning users...").append(NEWLINE);
        List<ApplicationUser> usersWithTemplates = getUsersWithQuickSubtasksTemplates();
        output.append("Users with Quick Subtasks for Jira templates: ").append(Integer.toString(usersWithTemplates.size())).append(NEWLINE);

        output.append("Scanning projects...").append(NEWLINE);
        List<Project> projectsWithTemplates = getProjectsWithQuickSubtasksTemplates();
        output.append("Projects with Quick Subtasks for Jira templates: ").append(Integer.toString(projectsWithTemplates.size())).append(NEWLINE);

        quickSubtasksCanImport = !projectsWithTemplates.isEmpty() || !usersWithTemplates.isEmpty();
        if (quickSubtasksCanImport) {
            output.append("Import can be started now!").append(NEWLINE);
        } else {
            output.append("Nothing to import!").append(NEWLINE);
        }
        return SUCCESS;
    }

    /**
     * Perform template import from Quick Subtasks for Jira.
     */
    @SupportedMethods({RequestMethod.GET})
    public String doQuickSubtasksImport() {
        output.append("Import started").append(NEWLINE);
        log.info("Import started");
        output.append("Importing user templates").append(NEWLINE);
        log.info("Importing user templates");
        PluginSettings settings = pluginSettingsFactory.createGlobalSettings();
        getUsersWithQuickSubtasksTemplates().forEach(user -> {
            String templatesForUser = (String) settings.get(QUICK_SUBTASKS_USER_TEMPLATES_PREFIX + user.getUsername());
            if (templatesForUser != null) {
                long counter = importQuickSubtasksTemplatesForUser(user, templatesForUser);
                output.append("Imported templates for user ").append(user.getUsername()).append(": ").append(Long.toString(counter)).append(NEWLINE);
            }
        });
        output.append("Importing project templates").append(NEWLINE);
        log.info("Importing project templates");
        getProjectsWithQuickSubtasksTemplates().forEach(project -> {
            PluginSettings settingsForProject = pluginSettingsFactory.createSettingsForKey(project.getKey());
            String templatesForProject = (String) settingsForProject.get(QUICK_SUBTASKS_PROJECT_TEMPLATES);
            if (templatesForProject != null) {
                long counter = importQuickSubtasksTemplatesForProject(project, templatesForProject);
                output.append("Imported templates for project ").append(project.getKey()).append(": ").append(Long.toString(counter)).append(NEWLINE);
            }
        });
        output.append("Check the log files for more details").append(NEWLINE);
        output.append("Import finished").append(NEWLINE);
        log.info("Import finished");
        return SUCCESS;
    }

    /**
     * Return the results for Quick Subtasks related actions.
     */
    public String getQuickSubtasksResults() {
        return output.toString();
    }

    /**
     * Return whether import can be started.
     */
    public boolean isQuickSubtasksCanImport() {
        return quickSubtasksCanImport;
    }

    /**
     * Return a list of all projects with templates for Quick Subtasks for Jira.
     */
    private List<Project> getProjectsWithQuickSubtasksTemplates() {
        List<Project> projectsWithTemplates = new ArrayList<>();
        projectManager.getProjectObjects().forEach(project -> {
            PluginSettings settingsForProject = pluginSettingsFactory.createSettingsForKey(project.getKey());
            String templatesForProject = (String) settingsForProject.get(QUICK_SUBTASKS_PROJECT_TEMPLATES);
            if (templatesForProject != null) {
                projectsWithTemplates.add(project);
            }
        });
        return projectsWithTemplates;
    }

    /**
     * Return a list of all users with templates for Quick Subtasks for Jira.
     */
    private List<ApplicationUser> getUsersWithQuickSubtasksTemplates() {
        PluginSettings settings = pluginSettingsFactory.createGlobalSettings();
        List<ApplicationUser> usersWithTemplates = new ArrayList<>();
        userSearchService.findUsers("", new UserSearchParams.Builder(50000).allowEmptyQuery(true).includeActive(true).includeInactive(false).build())
            .forEach(applicationUser -> {
                String templatesForUser = (String) settings.get(QUICK_SUBTASKS_USER_TEMPLATES_PREFIX + applicationUser.getUsername());
                if (templatesForUser != null) {
                    usersWithTemplates.add(applicationUser);
                }
            });
        return usersWithTemplates;
    }

    /**
     * Imports all Quick Subtasks templates for projects.
     */
    private long importQuickSubtasksTemplatesForProject(Project project, String templatesForProject) {
        AtomicLong counter = new AtomicLong();
        log.info("Extracting templates for project '" + project.getKey() + "'");
        List<ShowSubtaskTemplate> templates = extractQuickSubtasksTemplatesFromXml(templatesForProject, true);
        List<SubtaskTemplate> existingTemplates = subtaskTemplateService.getProjectTemplates(project.getId(), false);
        templates.forEach(template -> {
            log.info("Importing template for project '" + project.getKey() + "' with template name '" + template.getName() + "'");
            if (existingTemplates.stream().noneMatch(existingTemplate ->
                existingTemplate.getName().equals(template.getName()) && existingTemplate.getTemplate().equals(template.getTemplate())
            )) {
                subtaskTemplateService.saveProjectTemplate(project.getId(), jiraAuthenticationContext.getLoggedInUser().getId(), null, template.getName(), template.getTemplate());
                counter.getAndIncrement();
            } else {
                log.info("Template already exists: " + template.getName());
            }
        });
        return counter.get();
    }

    /**
     * Imports all Quick Subtasks templates for users.
     */
    private long importQuickSubtasksTemplatesForUser(ApplicationUser applicationUser, String templatesForUser) {
        AtomicLong counter = new AtomicLong();
        log.info("Extracting templates for user '" + applicationUser.getUsername() + "'");
        List<ShowSubtaskTemplate> templates = extractQuickSubtasksTemplatesFromXml(templatesForUser, false);
        List<SubtaskTemplate> existingTemplates = subtaskTemplateService.getUserTemplates(applicationUser.getId(), false);
        templates.forEach(template -> {
            log.info("Importing template for user '" + applicationUser.getUsername() + "' with template name '" + template.getName() + "'");
            if (existingTemplates.stream().noneMatch(existingTemplate ->
                existingTemplate.getName().equals(template.getName()) && existingTemplate.getTemplate().equals(template.getTemplate())
            )) {
                subtaskTemplateService.saveUserTemplate(applicationUser.getId(), null, template.getName(), template.getTemplate());
                counter.getAndIncrement();
            } else {
                log.info("Template already exists: " + template.getName());
            }
        });
        return counter.get();
    }

    /**
     * Returns {@link ShowSubtaskTemplate} objects from the given XML.
     */
    private List<ShowSubtaskTemplate> extractQuickSubtasksTemplatesFromXml(String templatesXml, boolean isProjectTemplate) {
        List<ShowSubtaskTemplate> templates = new ArrayList<>();
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(templatesXml.getBytes()));
            doc.getDocumentElement().normalize();

            // templates for users and projects are saved with different element names
            NodeList templateNodes = isProjectTemplate ?
                doc.getElementsByTagName("subtaskTemplate") :
                doc.getElementsByTagName("com.hascode.plugin.quick__subtasks.dto.IdDecoratingSubtaskTemplate");
            log.info("Templates found: " + templateNodes.getLength());

            for (int i = 0; i < templateNodes.getLength(); i++) {
                Node template = templateNodes.item(i);
                NodeList attributes = template.getChildNodes();

                String title = null;
                String text = null;

                for (int j = 0; j < attributes.getLength(); j++) {
                    Node item = attributes.item(j);
                    if (item.getNodeName().equals("text")) {
                        text = item.getTextContent();
                    }
                    if (item.getNodeName().equals("title")) {
                        title = item.getTextContent();
                    }
                }

                if (title != null && text != null) {
                    log.info("Extracting template with name '" + title + "'");
                    String newTemplate = transformQuickSubtasksTemplate(text);
                    if (newTemplate != null && !newTemplate.isEmpty()) {
                        templates.add(new ShowSubtaskTemplate(title, newTemplate));
                    } else {
                        log.error("Template with name '" + title + "' cannot be parsed correctly: " + text);
                    }
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            log.error("Error parsing template with xml " + templatesXml, e);
        }
        return templates;
    }

    String transformQuickSubtasksTemplate(String templateString) {
        StringWriter output = new StringWriter();
        Arrays.stream(templateString.split(NEWLINE)).forEach(line -> {
            Matcher matcher = QUICK_SUBTASKS_TASK_PATTERN.matcher(line);
            if (matcher.matches()) {
                String key = "";
                String value;
                for (int i = 1; i < matcher.groupCount(); i++) {
                    if (matcher.group(i) != null) {
                        if (i > 1) {
                            if (i % 2 == 0) {
                                key = matcher.group(i);
                            } else {
                                value = matcher.group(i);
                                switch (key.toLowerCase()) {
                                    // same keyword and value format
                                    case "priority":
                                    case "description":
                                    case "estimate":
                                    case "assignee":
                                    case "reporter":
                                        output.append("  ").append(key.toLowerCase()).append(": ").append(value).append(NEWLINE);
                                        break;
                                    // different keyword same value
                                    case "issuetype":
                                        output.append("  ").append(Subtask.Attributes.ISSUE_TYPE).append(": ").append(value).append(NEWLINE);
                                        break;
                                    case "duedate":
                                        output.append("  ").append(Subtask.Attributes.DUE_DATE).append(": ").append(value).append(NEWLINE);
                                        break;
                                    case "fixversion":
                                        output.append("  ").append(Subtask.Attributes.FIX_VERSION).append(": ").append(value).append(NEWLINE);
                                        break;
                                    case "affectedversion":
                                        output.append("  ").append(Subtask.Attributes.AFFECTED_VERSION).append(": ").append(value).append(NEWLINE);
                                        break;
                                    // component (split values)
                                    case "component":
                                        String[] components = value.split(", *");
                                        Arrays.stream(components).forEach(component ->
                                            output.append("  ").append(Subtask.Attributes.COMPONENT).append(": ").append(component).append(NEWLINE)
                                        );
                                        break;
                                    // labels (split values)
                                    case "labels":
                                        String[] labels = value.split(", *");
                                        Arrays.stream(labels).forEach(label ->
                                            output.append("  ").append(Subtask.Attributes.LABEL).append(": ").append(label).append(NEWLINE)
                                        );
                                        break;
                                    // watcher (split values)
                                    case "watcher":
                                        String[] watchers = value.split(", *");
                                        Arrays.stream(watchers).forEach(watcher ->
                                            output.append("  ").append(Subtask.Attributes.WATCHER).append(": ").append(watcher).append(NEWLINE)
                                        );
                                        break;
                                    // custom fields
                                    case "cfield":
                                        String[] tokens = value.split(":", 2);
                                        if (tokens.length == 2) {
                                            String customFieldName = tokens[0]
                                                .replaceAll("\\(", "\\\\(")
                                                .replaceAll("\\)", "\\\\)")
                                                .replaceAll(":", "\\:")
                                                .trim();
                                            output.append("  ").append("customfield(").append(customFieldName).append("): ").append(tokens[1]).append(NEWLINE);
                                        } else {
                                            log.error("Invalid custom field attributes: " + value);
                                        }
                                        break;
                                    // not supported (will be ignored)
                                    default:
                                        log.error("Ignoring unknown attribute '" + key + "' with value: '" + value + "'");
                                }
                            }
                        } else {
                            // summary
                            output.append("- ").append(matcher.group(i).trim()).append(NEWLINE);
                        }
                    }
                }
            } else {
                log.error("Ignoring invalid line: " + line);
            }
        });
        return output.toString();
    }

}
