package de.codescape.jira.plugins.multiplesubtasks.ao;

import net.java.ao.Preload;
import net.java.ao.schema.Indexed;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.StringLength;

@Preload
public interface SubtaskConfig extends BaseEntity {

    /**
     * The unique key of the setting.
     */
    @NotNull
    @Indexed
    @StringLength(255)
    String getKey();

    void setKey(String key);

    /**
     * The value of the setting.
     */
    @StringLength(StringLength.UNLIMITED)
    String getValue();

    void setValue(String value);

}
