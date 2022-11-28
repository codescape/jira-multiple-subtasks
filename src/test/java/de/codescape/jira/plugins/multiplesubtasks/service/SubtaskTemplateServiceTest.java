package de.codescape.jira.plugins.multiplesubtasks.service;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.user.ApplicationUser;
import de.codescape.jira.plugins.multiplesubtasks.MultipleSubtasksDatabaseUpdater;
import de.codescape.jira.plugins.multiplesubtasks.ao.SubtaskTemplate;
import de.codescape.jira.plugins.multiplesubtasks.model.SubtaskTemplateType;
import net.java.ao.EntityManager;
import net.java.ao.test.converters.NameConverters;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.Hsql;
import net.java.ao.test.jdbc.Jdbc;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(MultipleSubtasksDatabaseUpdater.class)
@Jdbc(Hsql.class)
@NameConverters
public class SubtaskTemplateServiceTest {

    private static final long CURRENT_USER = 42L;
    private static final long ANOTHER_USER = 21L;

    @SuppressWarnings("unused")
    private EntityManager entityManager;
    private TestActiveObjects activeObjects;

    private SubtaskTemplateService subtaskTemplateService;

    private ApplicationUser currentUser;
    private ApplicationUser anotherUser;

    @Before
    public void before() {
        activeObjects = new TestActiveObjects(entityManager);
        subtaskTemplateService = new SubtaskTemplateService(activeObjects);
        currentUser = mock(ApplicationUser.class);
        anotherUser = mock(ApplicationUser.class);
        deleteAllSubtaskTemplates();
    }

    /* tests for saveUserTemplate */

    @Test
    public void saveUserTemplateSavesNewTemplate() {
        when(currentUser.getId()).thenReturn(CURRENT_USER);

        subtaskTemplateService.saveUserTemplate(CURRENT_USER, null, "new template", "- task");

        SubtaskTemplate[] subtaskTemplates = activeObjects.find(SubtaskTemplate.class);
        assertThat(subtaskTemplates.length, is(equalTo(1)));
        assertThat(subtaskTemplates[0].getTemplateType(), is(equalTo(SubtaskTemplateType.USER)));
        assertThat(subtaskTemplates[0].getUserId(), is(equalTo(CURRENT_USER)));
        assertThat(subtaskTemplates[0].getName(), is(equalTo("new template")));
        assertThat(subtaskTemplates[0].getTemplate(), is(equalTo("- task")));
    }

    @Test
    public void saveUserTemplateUpdatesExistingTemplate() {
        when(currentUser.getId()).thenReturn(CURRENT_USER);
        subtaskTemplateService.saveUserTemplate(CURRENT_USER, null, "new template", "- task");
        SubtaskTemplate[] existingSubtaskTemplates = activeObjects.find(SubtaskTemplate.class);
        assertThat(existingSubtaskTemplates.length, is(equalTo(1)));

        subtaskTemplateService.saveUserTemplate(CURRENT_USER, existingSubtaskTemplates[0].getID(), "better name", "- better task");

        SubtaskTemplate[] subtaskTemplates = activeObjects.find(SubtaskTemplate.class);
        assertThat(subtaskTemplates.length, is(equalTo(1)));
        assertThat(subtaskTemplates[0].getTemplateType(), is(equalTo(SubtaskTemplateType.USER)));
        assertThat(subtaskTemplates[0].getUserId(), is(equalTo(CURRENT_USER)));
        assertThat(subtaskTemplates[0].getName(), is(equalTo("better name")));
        assertThat(subtaskTemplates[0].getTemplate(), is(equalTo("- better task")));
    }

    /* tests for getUserTemplates */

    @Test
    public void getUserTemplatesReturnsAllTemplatesForUser() {
        when(currentUser.getId()).thenReturn(CURRENT_USER);
        subtaskTemplateService.saveUserTemplate(CURRENT_USER, null, "first template", "- task");
        subtaskTemplateService.saveUserTemplate(CURRENT_USER, null, "second template", "- task");
        subtaskTemplateService.saveUserTemplate(CURRENT_USER, null, "third template", "- task");
        when(anotherUser.getId()).thenReturn(ANOTHER_USER);
        subtaskTemplateService.saveUserTemplate(ANOTHER_USER, null, "another template", "- task");

        List<SubtaskTemplate> userTemplates = subtaskTemplateService.getUserTemplates(CURRENT_USER);
        assertThat(userTemplates.size(), is(equalTo(3)));
        assertThat(userTemplates.stream().map(SubtaskTemplate::getName).collect(Collectors.toList()),
            hasItems("first template", "second template", "third template"));
    }

    @Test
    public void getUserTemplatesReturnsEmptyListForUserWithoutTemplates() {
        when(currentUser.getId()).thenReturn(CURRENT_USER);
        List<SubtaskTemplate> userTemplates = subtaskTemplateService.getUserTemplates(CURRENT_USER);
        assertThat(userTemplates, is(empty()));
    }

    /* tests for deleteUserTemplate */

    @Test
    public void deleteUserTemplateRemovesTemplateForCorrectUser() {
        when(currentUser.getId()).thenReturn(CURRENT_USER);
        subtaskTemplateService.saveUserTemplate(CURRENT_USER, null, "template", "- task");
        SubtaskTemplate[] existingSubtaskTemplates = activeObjects.find(SubtaskTemplate.class);
        assertThat(existingSubtaskTemplates.length, is(equalTo(1)));

        subtaskTemplateService.deleteUserTemplate(CURRENT_USER, existingSubtaskTemplates[0].getID());

        SubtaskTemplate[] subtaskTemplates = activeObjects.find(SubtaskTemplate.class);
        assertThat(subtaskTemplates, is(emptyArray()));
    }

    @Test
    public void deleteUserTemplateKeepsTemplateForDifferentUser() {
        when(currentUser.getId()).thenReturn(CURRENT_USER);
        subtaskTemplateService.saveUserTemplate(CURRENT_USER, null, "template", "- task");
        SubtaskTemplate[] existingSubtaskTemplates = activeObjects.find(SubtaskTemplate.class);
        assertThat(existingSubtaskTemplates.length, is(equalTo(1)));
        when(anotherUser.getId()).thenReturn(ANOTHER_USER);

        subtaskTemplateService.deleteUserTemplate(ANOTHER_USER, existingSubtaskTemplates[0].getID());

        SubtaskTemplate[] subtaskTemplates = activeObjects.find(SubtaskTemplate.class);
        assertThat(subtaskTemplates.length, is(equalTo(1)));
    }

    /* helper methods */

    private void deleteAllSubtaskTemplates() {
        SubtaskTemplate[] subtaskTemplates = activeObjects.find(SubtaskTemplate.class);
        Arrays.stream(subtaskTemplates).forEach(subtaskTemplate -> activeObjects.delete(subtaskTemplate));
    }

}
