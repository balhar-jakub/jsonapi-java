package net.balhar.jsonapi.document;

import net.avh4.test.junit.Nested;
import net.balhar.jsonapi.Document;
import net.balhar.jsonapi.Link;
import net.balhar.jsonapi.SimpleDocument;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * Tests for simple representation of correct document.
 */
@RunWith(Nested.class)
public class SimpleDocumentTest {
    private Document document;
    private Map result;

    public class EmptyDocument {
        @Before
        public void setUp(){
            document = new SimpleDocument();
            result = (Map) document.transform();
        }

        @Test
        public void containEmptyLinks(){
            assertThat(((Collection) result.get(JsonApiData.links.name())).size(), is(0));
        }

        @Test
        public void containEmptyData(){
            assertNotNull(result.get(JsonApiData.data.name()));
        }

        @Test
        public void doesntContainMeta(){
            assertNull(result.get(JsonApiData.meta.name()));
        }

        @Test
        public void doesntContainIncluded(){
            assertNull(result.get(JsonApiData.included.name()));
        }
    }

    public class ComplexDocument {
        private Collection links;
        private Collection included;
        private Map meta;
        private Map data;

        @Before
        public void setUp() throws MalformedURLException {
            document = new SimpleDocument(new Platypus());

            document.link("next", "http://test.balhar.net/api/previous")
                    .link("previous", "http://another.balhar.net/api/next")
                    .link("self", "http://test.balhar.net/api")

                    .meta("meta1", "meta2data")
                    .meta("meta2", "meta3data")

                    .include(new Platypus())

                    .resourceLink("person", "self", "http://test.balhar.net/api/person/1");

            result = (Map) document.transform();

            links = (Collection)result.get(JsonApiData.links.name());
            included = (Collection) result.get(JsonApiData.included.name());
            meta = (Map) result.get(JsonApiData.meta.name());
            data = (Map) result.get(JsonApiData.data.name());
        }

        @Test
        public void containsExactlyThreeLinks(){
            assertThat(links.size(), is(3));
        }

        @Test
        public void containsCorrectLinks() throws Exception {
            assertThat(links.contains(new Link("next", "http://test.balhar.net/api/previous")), is(true));
            assertThat(links.contains(new Link("previous", "http://another.balhar.net/api/next")), is(true));
            assertThat(links.contains(new Link("self", "http://test.balhar.net/api")), is(true));
        }

        @Test
        public void containsExactlyTwoMetaDataKeys(){
            assertThat(meta.size(), is(2));
        }

        @Test
        public void containsCorrectMetaData() {
            assertThat(((String)meta.get("meta1")), is("meta2data"));
            assertThat(((String)meta.get("meta2")), is("meta3data"));
        }

        @Test
        public void containsExactlyOneIncludedObject(){
            assertThat(included.size(), is(1));
        }

        @Test
        public void containsCorrectIncludedObject(){
            Iterator it = included.iterator();
            assertEquals(it.next().getClass(), Platypus.class);
        }

        @Test
        public void containsCorrectData(){
            assertThat((String)data.get("name"), is("simpleName"));
            assertThat((String)data.get("type"), is("Platypus"));
            assertThat((int)data.get("id"), is(1));
            assertThat(((String[])data.get("subspecies")).length, is(2));
        }

        @Test
        public void dataContainsCorrectLinks() throws Exception{
            Map resourceLinks = ((Map) data.get(JsonApiData.links.name()));
            assertThat(((Collection) resourceLinks.get("person")).contains(new Link("self", "http://test.balhar.net/api/person/1")), is(true));
        }
    }
}

enum JsonApiData {
    meta,links,included,data
}

@SuppressWarnings("MismatchedReadAndWriteOfArray")
class Platypus {
    private String name = "simpleName";
    private int id = 1;
    private String[] subspecies = new String[]{"spec1", "spec2"};
}