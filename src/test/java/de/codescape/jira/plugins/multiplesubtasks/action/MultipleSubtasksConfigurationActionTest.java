package de.codescape.jira.plugins.multiplesubtasks.action;

import de.codescape.jira.plugins.multiplesubtasks.model.TemplateSortOrder;
import de.codescape.jira.plugins.multiplesubtasks.service.MultipleSubtasksConfigurationService;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class MultipleSubtasksConfigurationActionTest {

    private MultipleSubtasksConfigurationAction action;

    @Before
    public void before() {
        action = new MultipleSubtasksConfigurationAction();
    }

    /* doDefault */

    @Test
    public void actionShouldAlwaysReturnSuccessPage() {
        assertThat(action.doDefault(), is(equalTo(action.SUCCESS)));
    }

    /* getTemplatesSortOrderKey */

    @Test
    public void configurationKeyForTemplatesSortOrderIsExposed() {
        assertThat(action.getTemplatesSortOrderKey(), is(equalTo(
            MultipleSubtasksConfigurationService.TEMPLATES_SORT_ORDER)));
    }

    /* getTemplatesSortOrderOptions */

    @Test
    public void configurationOptionsForTemplatesSortOrderAreExposed() {
        assertThat(action.getTemplatesSortOrderOptions(), hasSize(4));
        assertThat(action.getTemplatesSortOrderOptions(), containsInAnyOrder(
            TemplateSortOrder.NAME_ASC,
            TemplateSortOrder.NAME_DESC,
            TemplateSortOrder.AGE_ASC,
            TemplateSortOrder.AGE_DESC
        ));
    }

    /* getTemplatesPerProjectKey */

    @Test
    public void configurationKeyForTemplatesPerProjectIsExposed() {
        assertThat(action.getTemplatesPerProjectKey(), is(equalTo(
            MultipleSubtasksConfigurationService.TEMPLATES_PER_PROJECT)));
    }

    /* getTemplatesPerUserKey */

    @Test
    public void configurationKeyForTemplatesPerUserIsExposed() {
        assertThat(action.getTemplatesPerUserKey(), is(equalTo(
            MultipleSubtasksConfigurationService.TEMPLATES_PER_USER)));
    }

}
