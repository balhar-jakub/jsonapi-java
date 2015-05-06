package net.balhar.jsonapi.simple;

import net.avh4.test.junit.Nested;
import net.balhar.jsonapi.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.MalformedURLException;
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
    private TransformedDocument result;

    public class EmptyDocument {
        @Before
        public void setUp(){
            document = new SimpleDocument(new Identifiable() {
                @Override
                public String getUuid() {
                    return "uuid";
                }
            });
            result = (TransformedDocument) document.transform();
        }

        @Test
        public void containEmptyLinks(){
            assertThat(result.links().size(), is(0));
        }

        @Test
        public void containEmptyData(){
            assertNotNull(result.data());
        }

        @Test
        public void doesntContainMeta(){
            assertNull(result.meta());
        }

        @Test
        public void doesntContainIncluded(){
            assertNull(result.included());
        }
    }

    public class ComplexDocument {
        private Collection links;
        private Collection included;
        private Map meta;
        private TransformedResource singlePlatypus;

        @Before
        public void setUp() throws MalformedURLException {
            document = new SimpleDocument(new Platypus());

            document.link("next", "http://test.balhar.net/api/previous")
                    .link("previous", "http://another.balhar.net/api/next")
                    .link("self", "http://test.balhar.net/api")

                    .meta("meta1", "meta2data")
                    .meta("meta2", "meta3data")

                    .include(new Platypus())

                    .link("uuid","person", "self", "http://test.balhar.net/api/person/1");

            result = (TransformedDocument) document.transform();

            links = result.links();
            included = result.included();
            meta = result.meta();
            singlePlatypus = (TransformedResource) (result.data()).iterator().next();
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
            assertThat((String) singlePlatypus.get("name"), is("simpleName"));
            assertThat((String) singlePlatypus.get("type"), is("Platypus"));
            assertThat((int) singlePlatypus.get("id"), is(1));
            assertThat(((String[]) singlePlatypus.get("subspecies")).length, is(2));
        }

        @Test
        public void dataContainsCorrectLinks() throws Exception{
            Map resourceLinks = singlePlatypus.links();
            assertThat(((String)((Map) resourceLinks.get("person")).get("self")), is("http://test.balhar.net/api/person/1"));
        }
    }

    public class WithBaseUrl {
        @Before
        public void setUp(){
            document = new SimpleDocument(new Platypus(),"http://test.balhar.net/api/");

            document.link("next", "previous");

            result = (TransformedDocument) document.transform();
        }

        @Test
        public void linkContainsBaseUrl() {
            assertThat(result.links().contains(new Link("next", "http://test" +
                    ".balhar.net/api/previous")), is(true));
        }
    }
}

@SuppressWarnings("MismatchedReadAndWriteOfArray")
class Platypus implements Identifiable {
    private String name = "simpleName";
    private int id = 1;
    private String[] subspecies = new String[]{"spec1", "spec2"};

    @Override
    public String getUuid() {
        return "uuid";
    }
}