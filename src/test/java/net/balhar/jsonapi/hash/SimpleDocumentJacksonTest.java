package net.balhar.jsonapi.hash;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.avh4.test.junit.Nested;
import net.balhar.jsonapi.*;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collection;

import static com.jayway.jsonassert.JsonAssert.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;

/**
 * Tests validating that integration with Jackson works correctly.
 */
@RunWith(Nested.class)
public class SimpleDocumentJacksonTest {
    private Document hauntedHouse;
    private ObjectMapper hauntedHouseDelivery;

    @Before
    public void setUp(){
        hauntedHouseDelivery = new ObjectMapper();
        hauntedHouseDelivery.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    public class Collections {
        @Before
        public void setUp(){
            Collection hauntedBy = new ArrayList();
            hauntedBy.add(new Ghost("uuid1", "Trevor", "Paints rooms to pink."));
            hauntedBy.add(new Ghost("uuid2", "Macey", "Freezes everyone in view by singing."));

            hauntedHouse = new HashDocument(hauntedBy);
        }

        @Test
        public void correctSimpleSerialization() throws Exception {
            String hauntedHouseForTransfer = hauntedHouseDelivery.writeValueAsString(hauntedHouse.transform());
            with(hauntedHouseForTransfer)
                    .assertThat("$.data", collectionWithSize(equalTo(2)))
                    .assertThat("$.data..type", containsInAnyOrder("Ghost","Ghost"))
                    .assertThat("$.data..uuid", contains("uuid1", "uuid2"))
                    .assertThat("$.links.*", emptyCollection());
        }
    }

    public class Objects {
        @Before
        public void setUp(){
            hauntedHouse = new HashDocument(new Ghost("uuid1", "Fred", "Messes up your sense of reality."));
        }

        @Test
        public void correctSimpleSerialization() throws Exception {
            String hauntedHouseForTransfer = hauntedHouseDelivery.writeValueAsString(hauntedHouse.transform());
            with(hauntedHouseForTransfer)
                    .assertThat("$.data.type", is("Ghost"))
                    .assertThat("$.data.uuid", is("uuid1"))
                    .assertThat("$.links.*", emptyCollection());
        }
    }

    public class Included {
        private String hauntedHouseForTransfer;

        @Before
        public void setUp() throws Exception {
            Collection<Elf> enemies = new ArrayList<>();
            enemies.add(new Elf("uuid1", "Thranduil"));
            enemies.add(new Elf("uuid2", "Legolas"));

            Warrior warrior = new Warrior("uuid1", enemies);
            hauntedHouse = new HashDocument(warrior);
            hauntedHouseForTransfer = hauntedHouseDelivery.writeValueAsString(hauntedHouse.transform());
        }

        @Test
        public void correctSerializationOfIncludedCollection() {
            with(hauntedHouseForTransfer)
                    .assertThat("$.included", collectionWithSize(equalTo(2)))
                    .assertThat("$.included..type", containsInAnyOrder("Elf", "Elf"));
        }

        @Test
        public void linkageContainsIncluded() {
            with(hauntedHouseForTransfer)
                    .assertThat("$.data.links.enemies.linkage", collectionWithSize(equalTo(2)))
                    .assertThat("$.data.links.enemies.linkage..type", containsInAnyOrder("Elf", "Elf"));
        }
    }
}

class Warrior implements Identifiable {
    private String uuid;
    @Included(type = "Elf", association = "enemies")
    private Collection<Elf> enemies;

    Warrior(String uuid, Collection<Elf> enemies) {
        this.uuid = uuid;
        this.enemies = enemies;
    }

    public String getUuid() {
        return uuid;
    }
}

class Ghost implements Identifiable{
    private String variety;
    private String location;
    private String uuid;

    Ghost(String uuid, String variety, String ability) {
        this.variety = variety;
        this.location = ability;
        this.uuid = uuid;
    }

    @Override
    public String getUuid() {
        return uuid;
    }
}