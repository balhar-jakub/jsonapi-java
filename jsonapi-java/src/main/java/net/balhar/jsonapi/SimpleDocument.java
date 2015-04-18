package net.balhar.jsonapi;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * There is concept of link on top level of the document.
 */
public class SimpleDocument implements Document {
    // Data contain always just one resource. Data more or less means resource
    private Resource data;
    // Included is optional, it may not be present in the output.
    private Collection<Object> included = new ArrayList<>();
    private Collection<Link> links = new ArrayList<>();
    private Map<String, Object> meta = new HashMap<>();

    /**
     * It creates base for data based on this object. The question is how to add links to the data object instead of
     * into the main object. Only thing which is relevant
     *
     * @param object Resource to be used in any succeeding functions.
     */
    public SimpleDocument(Object object){
        data = new SimpleResource(object);
    }

    public SimpleDocument() {
        data = new SimpleResource();
    }

    @Override
    public Document link(String key, String location) {
        links.add(new Link(key, location));

        return this;
    }

    @Override
    public Document resourceLink(String type, String action, String location) {
        data.link(type, action, location);

        return this;
    }

    @Override
    public Document include(Object includable) {
        included.add(includable);

        return this;
    }

    @Override
    public Document meta(String key, Object value) {
        meta.put(key, value);

        return this;
    }

    @Override
    public Object transform(){
        HashMap<String, Object> result = new HashMap<>();

        result.put("data", data.transform());
        result.put("links", links);

        if(!meta.isEmpty()) result.put("meta", meta);
        if(!included.isEmpty()) result.put("included", included);

        return result;
    }
}
