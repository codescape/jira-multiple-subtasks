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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This action allows to run template imports for Multiple Subtasks for Jira.
 */
public class SubtaskTemplateImportAction extends JiraWebActionSupport {

    private static final String QUICK_SUBTASKS_PROJECT_TEMPLATES = "com.hascode.plugin.jira:subtask-templates";
    private static final String QUICK_SUBTASKS_USER_TEMPLATES_PREFIX = "subtasks-user-";
    private static final Pattern QUICK_SUBTASKS_TASK_PATTERN = Pattern.compile("^- *([^(?:/)]+)(?:/)?(?:(?: ([^:]+):\"([^\"]+)\")?(?:\\s*)(?:/)?(?:\\s*)(?: ([^:]+):\"([^\"]+)\")?(?:\\s*)(?:/)?(?:\\s*)(?: ([^:]+):\"([^\"]+)\")?(?:\\s*)(?:/)?(?:\\s*)(?: ([^:]+):\"([^\"]+)\")?(?:\\s*)(?:/)?(?:\\s*)(?: ([^:]+):\"([^\"]+)\")?(?:\\s*)(?:/)?(?:\\s*)(?: ([^:]+):\"([^\"]+)\")?(?:\\s*)(?:/)?(?:\\s*)(?: ([^:]+):\"([^\"]+)\")?(?:\\s*)(?:/)?(?:\\s*)(?: ([^:]+):\"([^\"]+)\")?(?:\\s*)(?:/)?(?:\\s*)(?: ([^:]+):\"([^\"]+)\")?(?:\\s*)(?:/)?(?:\\s*)(?: ([^:]+):\"([^\"]+)\")?(?:\\s*)(?:/)?(?:\\s*)(?: ([^:]+):\"([^\"]+)\")?(?:\\s*)(?:/)?(?:\\s*)(?: ([^:]+):\"([^\"]+)\")?(?:\\s*)(?:/)?(?:\\s*)(?: ([^:]+):\"([^\"]+)\")?(?:\\s*)(?:/)?(?:\\s*)(?: ([^:]+):\"([^\"]+)\")?(?:\\s*)(?:/)?(?:\\s*)(?: ([^:]+):\"([^\"]+)\")?(?:\\s*)(?:/)?(?:\\s*)(?: ([^:]+):\"([^\"]+)\")?(?:\\s*)(?:/)?(?:\\s*)|(?:[^\\r\\n]*))$");

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
        output.append("Scanning users...").append("\n");
        List<ApplicationUser> usersWithTemplates = getUsersWithQuickSubtasksTemplates();
        output.append("Users with Quick Subtasks for Jira templates: ").append(Integer.toString(usersWithTemplates.size())).append("\n");

        output.append("Scanning projects...").append("\n");
        List<Project> projectsWithTemplates = getProjectsWithQuickSubtasksTemplates();
        output.append("Projects with Quick Subtasks for Jira templates: ").append(Integer.toString(projectsWithTemplates.size())).append("\n");

        quickSubtasksCanImport = !projectsWithTemplates.isEmpty() || !usersWithTemplates.isEmpty();
        if (quickSubtasksCanImport) {
            output.append("Import can be started now!");
        } else {
            output.append("Nothing to import!");
        }
        return SUCCESS;
    }

    /**
     * Perform template import from Quick Subtasks for Jira.
     */
    @SupportedMethods({RequestMethod.GET})
    public String doQuickSubtasksImport() {
        output.append("Import started.").append("\n");
        output.append("Importing user templates...").append("\n");
        PluginSettings settings = pluginSettingsFactory.createGlobalSettings();
        getUsersWithQuickSubtasksTemplates().forEach(applicationUser -> {
            String templatesForUser = (String) settings.get(QUICK_SUBTASKS_USER_TEMPLATES_PREFIX + applicationUser.getUsername());
            if (templatesForUser != null) {
                importQuickSubtasksTemplatesForUser(applicationUser, templatesForUser);
            }
        });
        output.append("Importing project templates...").append("\n");
        getProjectsWithQuickSubtasksTemplates().forEach(project -> {
            PluginSettings settingsForProject = pluginSettingsFactory.createSettingsForKey(project.getKey());
            String templatesForProject = (String) settingsForProject.get(QUICK_SUBTASKS_PROJECT_TEMPLATES);
            if (templatesForProject != null) {
                importQuickSubtasksTemplatesForProject(project, templatesForProject);
            }
        });
        output.append("Import finished.").append("\n");
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
    private void importQuickSubtasksTemplatesForProject(Project project, String templatesForProject) {
        List<ShowSubtaskTemplate> templates = extractQuickSubtasksTemplatesFromXml(templatesForProject, true);
        List<SubtaskTemplate> existingTemplates = subtaskTemplateService.getProjectTemplates(project.getId());
        templates.forEach(template -> {
            if (existingTemplates.stream().filter(existingTemplate ->
                existingTemplate.getName().equals(template.getName()) && existingTemplate.getTemplate().equals(template.getTemplate())
            ).findFirst().orElse(null) == null) {
                subtaskTemplateService.saveProjectTemplate(project.getId(), jiraAuthenticationContext.getLoggedInUser().getId(), null, template.getName(), template.getTemplate());
            } else {
                log.info("Template already exists: " + template.getName());
            }
        });
    }

    /**
     * Imports all Quick Subtasks templates for users.
     */
    private void importQuickSubtasksTemplatesForUser(ApplicationUser applicationUser, String templatesForUser) {
        List<ShowSubtaskTemplate> templates = extractQuickSubtasksTemplatesFromXml(templatesForUser, false);
        List<SubtaskTemplate> existingTemplates = subtaskTemplateService.getUserTemplates(applicationUser.getId());
        templates.forEach(template -> {
                if (existingTemplates.stream().filter(existingTemplate ->
                    existingTemplate.getName().equals(template.getName()) && existingTemplate.getTemplate().equals(template.getTemplate())
                ).findFirst().orElse(null) == null) {
                    subtaskTemplateService.saveUserTemplate(applicationUser.getId(), null, template.getName(), template.getTemplate());
                } else {
                    log.info("Template already exists: " + template.getName());
                }
            }
        );
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
                    log.info("Importing template: " + title);
                    String newTemplate = transformQuickSubtasksTemplate(text);
                    if (newTemplate != null && !newTemplate.isEmpty()) {
                        templates.add(new ShowSubtaskTemplate(title, newTemplate));
                    } else {
                        log.error("Template cannot be parsed correctly: " + text);
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
        Arrays.stream(templateString.split("\n")).forEach(line -> {
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
                                switch (key) {
                                    // same keyword and value format
                                    case "priority":
                                    case "description":
                                    case "estimate":
                                    case "assignee":
                                    case "dueDate":
                                    case "reporter":
                                    case "issueType":
                                        output.append("  ").append(key).append(": ").append(value).append("\n");
                                        break;
                                    // different keyword same value
                                    case "fixversion":
                                        output.append("  ").append("fixVersion").append(": ").append(value).append("\n");
                                        break;
                                    case "affectedversion":
                                        output.append("  ").append("affectedVersion").append(": ").append(value).append("\n");
                                        break;
                                    // component (split values)
                                    case "component":
                                        String[] components = value.split(", *");
                                        Arrays.stream(components).forEach(component ->
                                            output.append("  ").append("component").append(": ").append(component).append("\n")
                                        );
                                        break;
                                    // labels (split values)
                                    case "labels":
                                        String[] labels = value.split(", *");
                                        Arrays.stream(labels).forEach(label ->
                                            output.append("  ").append("label").append(": ").append(label).append("\n")
                                        );
                                        break;
                                    // watcher (split values)
                                    case "watcher":
                                        String[] watchers = value.split(", *");
                                        Arrays.stream(watchers).forEach(watcher ->
                                            output.append("  ").append("watcher").append(": ").append(watcher).append("\n")
                                        );
                                        break;
                                    // not supported (will be ignored)
                                    default:
                                        log.error("Ignoring unknown attribute '" + key + "' with value: '" + value + "'");
                                }
                            }
                        } else {
                            // summary
                            output.append("- ").append(matcher.group(i).trim()).append("\n");
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
