package de.codescape.jira.plugins.multiplesubtasks.model;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;

public class CustomFieldsTest {

    @Test
    public void extractCustomFieldNameForSimpleStringValue() {
        String result = CustomFields.extractCustomFieldName("customfield(Simple)");
        assertThat(result, is(equalTo("Simple")));
    }

    @Test
    public void extractCustomFieldNameForAlphanumericValue() {
        String result = CustomFields.extractCustomFieldName("customfield(Test123)");
        assertThat(result, is(equalTo("Test123")));
    }

    @Test
    public void extractCustomFieldNameForStringWithEscapedBracketsValue() {
        String result = CustomFields.extractCustomFieldName("customfield(Field\\(123\\))");
        assertThat(result, is(equalTo("Field(123)")));
    }

    @Test
    public void extractCustomFieldNameForStringWithEscapedColonValue() {
        String result = CustomFields.extractCustomFieldName("customfield(This\\:is\\:a\\:field)");
        assertThat(result, is(equalTo("This:is:a:field")));
    }

    @Test(expected = SyntaxFormatException.class)
    public void extractCustomFieldNameForInvalidInput() {
        CustomFields.extractCustomFieldName("CUSTOMFIELD(ILLEGAL)");
        fail();
    }

}
