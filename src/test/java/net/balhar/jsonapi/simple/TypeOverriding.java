package net.balhar.jsonapi.simple;

import net.avh4.test.junit.Nested;
import net.balhar.jsonapi.Document;
import net.balhar.jsonapi.Identifiable;
import net.balhar.jsonapi.Type;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test for overriding type of the document.
 */
@RunWith(Nested.class)
public class TypeOverriding {
    private Document dwarfWarrior;

    @Before
    public void setUp(){
        dwarfWarrior = new SimpleDocument(new DwarfDto("validUuid"));
    }

    @Test
    public void payloadHasCorrectType() {
        dwarfWarrior.transform();
    }
}

@Type(name = "dwarf")
class DwarfDto implements Identifiable {
    private String uuid;

    public DwarfDto(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String getUuid() {
        return uuid;
    }
}
