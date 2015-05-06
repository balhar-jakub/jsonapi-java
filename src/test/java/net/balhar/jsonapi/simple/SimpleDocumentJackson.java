package net.balhar.jsonapi.simple;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.avh4.test.junit.Nested;
import net.balhar.jsonapi.Document;
import net.balhar.jsonapi.Identifiable;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collection;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Tests validating that integration with Jackson works correctly.
 */
@RunWith(Nested.class)
@Ignore
public class SimpleDocumentJackson {
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

            hauntedHouse = new SimpleDocument(hauntedBy);
        }

        @Test
        public void correctSimpleSerialization() throws Exception {
            String hauntedHouseForTransfer = hauntedHouseDelivery.writeValueAsString(hauntedHouse.transform());
            assertThat(hauntedHouseForTransfer, is(
                    "{\"data\":[{\"location\":\"Freezes everyone in view by singing.\",\"links\":{},\"uuid\":\"uuid2\",\"type\":\"Ghost\",\"variety\":\"Macey\"},{\"location\":\"Paints rooms to pink.\",\"links\":{},\"uuid\":\"uuid1\",\"type\":\"Ghost\",\"variety\":\"Trevor\"}],\"links\":[]}"));
        }
    }

    public class Objects {
        @Before
        public void setUp(){
            hauntedHouse = new SimpleDocument(new Ghost("uuid1", "Fred", "Messes up your sense of reality."));
        }

        @Test
        public void correctSimpleSerialization() throws Exception {
            String hauntedHouseForTransfer = hauntedHouseDelivery.writeValueAsString(hauntedHouse.transform());
            assertThat(hauntedHouseForTransfer, is(
                    "{\"data\":[{\"location\":\"Messes up your sense of reality.\",\"links\":{},\"uuid\":\"uuid1\",\"type\":\"Ghost\",\"variety\":\"Fred\"}],\"links\":[]}"));
        }
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