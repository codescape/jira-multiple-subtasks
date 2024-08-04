package de.codescape.jira.plugins.multiplesubtasks.rest;

import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.permission.ProjectPermissions;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.security.PermissionManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import de.codescape.jira.plugins.multiplesubtasks.rest.entities.CreatedSubtaskEntity;
import de.codescape.jira.plugins.multiplesubtasks.service.SubtasksCreationService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Rest API to create multiple subtasks.
 *
 * @since 24.08.0
 */
@Path("/subtasks")
public class SubtasksResource {

    private final JiraAuthenticationContext jiraAuthenticationContext;
    private final IssueManager issueManager;
    private final PermissionManager permissionManager;
    private final SubtasksCreationService subtasksCreationService;

    @Autowired
    public SubtasksResource(@ComponentImport JiraAuthenticationContext jiraAuthenticationContext,
                            @ComponentImport IssueManager issueManager,
                            @ComponentImport PermissionManager permissionManager,
                            SubtasksCreationService subtasksCreationService) {
        this.jiraAuthenticationContext = jiraAuthenticationContext;
        this.issueManager = issueManager;
        this.permissionManager = permissionManager;
        this.subtasksCreationService = subtasksCreationService;
    }

    @POST
    @Path("/{issue}")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public Response put(@PathParam("issue") String issueKey, String content) {
        // TODO consider to remove this check because it is already implemented in SubtasksCreationService
        MutableIssue issue = issueManager.getIssueByCurrentKey(issueKey);
        if (issue == null) {
            return Response
                .status(Response.Status.BAD_REQUEST)
                .entity("Issue not found")
                .type(MediaType.TEXT_PLAIN_TYPE)
                .build();
        }

        // TODO consider to move this check into SubtasksCreationService
        if (!jiraAuthenticationContext.isLoggedInUser() &&
            !permissionManager.hasPermission(ProjectPermissions.CREATE_ISSUES, issue, jiraAuthenticationContext.getLoggedInUser())) {
            return Response
                .status(Response.Status.FORBIDDEN)
                .build();
        }

        try {
            List<CreatedSubtaskEntity> createdSubtasks = subtasksCreationService.subtasksFromString(issueKey, content)
                .stream()
                .map(CreatedSubtaskEntity::new)
                .collect(Collectors.toList());
            return Response.ok(createdSubtasks).build();
        } catch (RuntimeException e) {
            return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(e.getMessage())
                .type(MediaType.TEXT_PLAIN_TYPE)
                .build();
        }
    }

}
