package de.codescape.jira.plugins.multiplesubtasks.rest;

import com.atlassian.jira.permission.GlobalPermissionKey;
import com.atlassian.jira.security.GlobalPermissionManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import de.codescape.jira.plugins.multiplesubtasks.ao.SubtaskConfig;
import de.codescape.jira.plugins.multiplesubtasks.rest.entities.ConfigurationEntity;
import de.codescape.jira.plugins.multiplesubtasks.service.MultipleSubtasksConfigurationService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST endpoint to manage the plugin configuration.
 */
@Path("/configuration")
public class ConfigurationResource {

    private final MultipleSubtasksConfigurationService multipleSubtasksConfigurationService;
    private final JiraAuthenticationContext jiraAuthenticationContext;
    private final GlobalPermissionManager globalPermissionManager;

    @Inject
    public ConfigurationResource(@ComponentImport JiraAuthenticationContext jiraAuthenticationContext,
                                 @ComponentImport GlobalPermissionManager globalPermissionManager,
                                 MultipleSubtasksConfigurationService multipleSubtasksConfigurationService) {
        this.jiraAuthenticationContext = jiraAuthenticationContext;
        this.globalPermissionManager = globalPermissionManager;
        this.multipleSubtasksConfigurationService = multipleSubtasksConfigurationService;
    }

    /**
     * Get all configuration settings.
     *
     * @return list of configurations
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response get() {
        if (!currentUserIsAdmin()) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        List<ConfigurationEntity> configurations = multipleSubtasksConfigurationService.get().stream()
            .map(ConfigurationEntity::new)
            .collect(Collectors.toList());
        return Response.ok(configurations).build();
    }

    /**
     * Get a specific configuration setting.
     *
     * @param key key of the setting
     * @return configuration
     */
    @Path("/{key}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response get(@PathParam("key") String key) {
        if (!currentUserIsAdmin()) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        SubtaskConfig configuration = multipleSubtasksConfigurationService.get(key);
        return configuration == null
            ? Response.noContent().build()
            : Response.ok(new ConfigurationEntity(configuration)).build();
    }

    /**
     * Set a specific configuration setting.
     *
     * @param key   key of the setting
     * @param value value of the setting
     * @return configuration
     */
    @Path("/{key}")
    @POST
    @Produces({MediaType.APPLICATION_JSON})
    public Response set(@PathParam("key") String key, String value) {
        if (!currentUserIsAdmin()) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        SubtaskConfig configuration = multipleSubtasksConfigurationService.set(key, value);
        return configuration == null
            ? Response.noContent().build()
            : Response.ok(new ConfigurationEntity(configuration)).build();
    }

    /**
     * Check whether the currently logged-in user has one of the two required administrator permissions.
     */
    private boolean currentUserIsAdmin() {
        ApplicationUser loggedInUser = jiraAuthenticationContext.getLoggedInUser();
        return globalPermissionManager.hasPermission(GlobalPermissionKey.SYSTEM_ADMIN, loggedInUser) ||
            globalPermissionManager.hasPermission(GlobalPermissionKey.ADMINISTER, loggedInUser);
    }

}
