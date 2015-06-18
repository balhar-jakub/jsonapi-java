package net.balhar.jsonapi.reflection;

import net.avh4.test.junit.Nested;
import net.balhar.jsonapi.Document;
import net.balhar.jsonapi.Identifiable;
import net.balhar.jsonapi.Ignored;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Tests to verify correct behavior of ignored annotation.
 */
@RunWith(Nested.class)
public class IgnoredPropertyTest {
    private Document characters;

    @Before
    public void setUp() {
        characters = new ReflectionDocument(new Character(1, "Tommy", "Shy character playing chess and killing vampires " +
                "in free time.", true));
    }

    @Test
    public void idIsIgnored() throws Exception {
        Map<String, Object> transferredCharacter = (Map<String, Object>) characters.transform();
        Map data = (Map)transferredCharacter.get("data");

        String characterId = (String) data.get("id");
        assertThat(characterId, is(nullValue()));
    }
}

class Character implements Identifiable {
    @Ignored
    private int id;
    private String name;
    private String description;
    private boolean introvert;

    public Character(int id, String name, String description, boolean introvert) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.introvert = introvert;
    }

    @Override
    public String getUuid() {
        return "uuid";
    }
}
