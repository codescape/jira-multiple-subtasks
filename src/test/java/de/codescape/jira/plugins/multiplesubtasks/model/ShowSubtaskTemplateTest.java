package de.codescape.jira.plugins.multiplesubtasks.model;

import de.codescape.jira.plugins.multiplesubtasks.ao.SubtaskTemplate;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ShowSubtaskTemplateTest {

    @Test
    public void exposesAllFieldsOfSubtaskTemplate() {
        SubtaskTemplate subtaskTemplate = mock(SubtaskTemplate.class);
        when(subtaskTemplate.getID()).thenReturn(42L);
        when(subtaskTemplate.getTemplateType()).thenReturn(SubtaskTemplateType.PROJECT);
        when(subtaskTemplate.getTemplate()).thenReturn("- a task");
        when(subtaskTemplate.getName()).thenReturn("my subtask template");
        when(subtaskTemplate.getUserId()).thenReturn(123L);
        when(subtaskTemplate.getProjectId()).thenReturn(345L);

        ShowSubtaskTemplate showSubtaskTemplate = new ShowSubtaskTemplate(subtaskTemplate);

        assertThat(showSubtaskTemplate.getId(), is(equalTo(42L)));
        assertThat(showSubtaskTemplate.getTemplateType(), is(equalTo(SubtaskTemplateType.PROJECT)));
        assertThat(showSubtaskTemplate.getTemplate(), is(equalTo("- a task")));
        assertThat(showSubtaskTemplate.getName(), is(equalTo("my subtask template")));
        assertThat(showSubtaskTemplate.getUserId(), is(equalTo(123L)));
        assertThat(showSubtaskTemplate.getProjectId(), is(equalTo(345L)));
    }

}
