package de.codescape.jira.plugins.multiplesubtasks.rest;

import de.codescape.jira.plugins.multiplesubtasks.ao.SubtaskConfig;
import de.codescape.jira.plugins.multiplesubtasks.rest.entities.ConfigurationEntity;
import de.codescape.jira.plugins.multiplesubtasks.service.MultipleSubtasksConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST endpoint to manage the plugin configuration.
 */
// TODO restrict this service to administrators
@Path("/configuration")
public class ConfigurationResource {

    private final MultipleSubtasksConfigurationService multipleSubtasksConfigurationService;

    @Autowired
    public ConfigurationResource(MultipleSubtasksConfigurationService multipleSubtasksConfigurationService) {
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
        SubtaskConfig configuration = multipleSubtasksConfigurationService.set(key, value);
        return configuration == null
            ? Response.noContent().build()
            : Response.ok(new ConfigurationEntity(configuration)).build();
    }

}
