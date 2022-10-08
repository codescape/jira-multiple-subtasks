package de.codescape.jira.plugins.multiplesubtasks.ao;

import net.java.ao.RawEntity;
import net.java.ao.schema.AutoIncrement;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.PrimaryKey;

public interface EntityWithId extends RawEntity<Long> {

    /**
     * The technical unique identifier for a concrete entity extending this interface.
     */
    @AutoIncrement
    @NotNull
    @PrimaryKey("ID")
    Long getID();

}
