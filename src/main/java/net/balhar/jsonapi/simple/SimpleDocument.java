package net.balhar.jsonapi.simple;

import net.balhar.jsonapi.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * There is concept of link on top level of the document.
 */
public class SimpleDocument implements Document {
    private String baseUrl = "";

    private Map<String, Resource> data = new HashMap<>();
    private Collection<Object> included = new ArrayList<>();
    private Collection<Link> links = new ArrayList<>();
    private Map<String, Object> meta = new HashMap<>();

    public SimpleDocument(Collection<? extends Identifiable> object, String baseUrl){
        this(object);
        this.baseUrl = baseUrl;
    }

    public SimpleDocument(Identifiable object, String baseUrl){
        this(object);
        this.baseUrl = baseUrl;
    }

    public SimpleDocument(Collection<? extends Identifiable> objects){
        // Retrieve uuid from the object.
        for(Identifiable object: objects) {
            data.put(object.getUuid(), new SimpleResource(object, this));
        }
    }

    public SimpleDocument(Identifiable object) {
        data.put(object.getUuid(), new SimpleResource(object, this));
    }

    @Override
    public Document link(String key, String location) {
        links.add(new Link(key, baseUrl + location));

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
    public Document link(String uuid, String type, String key, String location) {
        Resource resource = data.get(uuid);
        assertResourceExists(resource);

        resource.link(type, key, location);

        return this;
    }

    @Override
    public Document link(String uuid, String type, String location) {
        Resource resource = data.get(uuid);
        assertResourceExists(resource);

        resource.link(type, location);

        return this;
    }

    @Override
    public Document linkage(String linkUuid, Object payload) {
        Resource resource = data.get(linkUuid);
        assertResourceExists(resource);

        resource.linkage("", payload);

        return this;
    }

    private void assertResourceExists(Resource resource){
        if(resource == null) {
            throw new RuntimeException("It isn't possible to link to resource which doesn't exist yet.");
        }
    }

    @Override
    public Object transform(){
        TransformedDocument<String, Object> result = new TransformedDocument<>();

        Collection<Object> transformedResources = new ArrayList<>();
        Collection<Resource> resources = data.values();
        for(Resource resource: resources) {
            transformedResources.add(resource.transform());
        }
        result.put(ApiKeys.DATA, transformedResources);
        result.put(ApiKeys.LINKS, links);

        if(!meta.isEmpty()) result.put(ApiKeys.META, meta);
        if(!included.isEmpty()) result.put(ApiKeys.INCLUDED, included);

        return result;
    }
}
