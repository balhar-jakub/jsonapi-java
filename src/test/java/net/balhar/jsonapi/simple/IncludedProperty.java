package net.balhar.jsonapi.simple;

import net.avh4.test.junit.Nested;
import net.balhar.jsonapi.ApiKeys;
import net.balhar.jsonapi.Document;
import net.balhar.jsonapi.Identifiable;
import net.balhar.jsonapi.Included;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Test whether included works properly.
 * As part of including attach the resources into the linkage section of given resource?
 */
@RunWith(Nested.class)
public class IncludedProperty {
    TransformedDocument warriorTransfer;

    public class ValidIncludedProperty {
        @Before
        public void setUp() {
            Collection<Elf> enemies = new ArrayList<>();
            enemies.add(new Elf("uuid1", "Thranduil"));
            enemies.add(new Elf("uuid2", "Legolas"));
            Dwarf universalWarrior = new Dwarf("uuid1", "Gimli", enemies);

            Document document = new SimpleDocument(universalWarrior);
            warriorTransfer = (TransformedDocument) document.transform();
        }

        @Test
        public void includedIsMissingInPayload() {
            Map transferredWarrior = (Map) warriorTransfer.data().iterator().next();
            assertThat(transferredWarrior.get("enemies"), is(nullValue()));
        }

        @Test
        public void includedIsInLinkageInLinks() throws Exception {
            TransformedResource transferredWarrior = (TransformedResource) warriorTransfer.data().iterator().next();
            Map linksToEnemies = transferredWarrior.links("Elf");
            Collection linkage = (Collection) linksToEnemies.get(ApiKeys.LINKAGE);
            assertThat(linkage.size(), is(2));

            Elf enemy = (Elf) linkage.iterator().next();
            String type = (String) enemy.getClass().getDeclaredField(ApiKeys.TYPE).get(enemy);
            assertThat(type, is("Elf"));
            assertThat(enemy.getUuid(), is("uuid1"));
        }

        @Test
        public void includedIsPresentInIncludedSection() throws Exception {
            assertThat(warriorTransfer.included().size(), is(2));
            Elf includedContainsType = (Elf) warriorTransfer.included().iterator().next();
            String type = (String) includedContainsType.getClass().getDeclaredField(ApiKeys.TYPE).get(includedContainsType);
            assertThat(type, is("Elf"));
        }
    }

    public class NullProperty {
        @Before
        public void setUp() {
            Dwarf universalWarrior = new Dwarf("uuid1", "Gimli", null);

            Document document = new SimpleDocument(universalWarrior);
            warriorTransfer = (TransformedDocument) document.transform();
        }

        @Test
        public void includedIsntPresent() {
            assertThat(warriorTransfer.included(), nullValue());
        }
    }
}

class Dwarf implements Identifiable {
    private String uuid;
    private String name;
    @Included(type="Elf")
    private Elf mortalEnemy;
    @Included(type = "Elf")
    private Collection<Elf> enemies;

    public Dwarf(String uuid, String name, Collection<Elf> enemies) {
        this.uuid = uuid;
        this.name = name;
        this.enemies = enemies;
    }

    @Override
    public String getUuid() {
        return uuid;
    }
}
