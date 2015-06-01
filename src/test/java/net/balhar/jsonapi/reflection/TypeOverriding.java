package net.balhar.jsonapi.reflection;

import net.avh4.test.junit.Nested;
import net.balhar.jsonapi.ApiKeys;
import net.balhar.jsonapi.Document;
import net.balhar.jsonapi.Identifiable;
import net.balhar.jsonapi.Type;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Test for overriding type of the document.
 */
@RunWith(Nested.class)
public class TypeOverriding {
    private Document dwarfWarrior;

    @Before
    public void setUp(){
        dwarfWarrior = new ReflectionDocument(new DwarfDto("validUuid"));
    }

    @Test
    public void payloadHasCorrectType() {
        TransformedDocument document = (TransformedDocument) dwarfWarrior.transform();
        TransformedResource warrior = (TransformedResource) document.get(ApiKeys.DATA);
        String warriorType = (String) warrior.get(ApiKeys.TYPE);
        assertThat(warriorType, is("dwarf"));
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
