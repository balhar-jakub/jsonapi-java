package net.balhar.jsonapi.hash;

import net.balhar.jsonapi.ApiKeys;
import net.balhar.jsonapi.Document;
import net.balhar.jsonapi.Identifiable;
import net.balhar.jsonapi.Resource;

import java.util.*;

/**
 * There is concept of link on top level of the document.
 */
public class HashDocument implements Document {
    private String baseUrl = "";

    private Map<String, Resource> data = new HashMap<>();
    private Collection<Object> included = new HashSet<>();
    private Map<String, String> links = new HashMap<>();
    private Map<String, Object> meta = new HashMap<>();

    public HashDocument(Collection<? extends Identifiable> object, String baseUrl){
        this(object);
        this.baseUrl = baseUrl;
    }

    public HashDocument(Identifiable object, String baseUrl){
        this(object);
        this.baseUrl = baseUrl;
    }

    public HashDocument(Collection<? extends Identifiable> objects){
        // Retrieve uuid from the object.
        for(Identifiable object: objects) {
            data.put(object.getUuid(), new HashResource(object, this));
        }
    }

    public HashDocument(Identifiable object) {
        data.put(object.getUuid(), new HashResource(object, this));
    }

    @Override
    public Document link(String key, String location) {
        links.put(key, baseUrl + location);

        return this;
    }

    @Override
    public Document include(Object includable) {
        if(includable instanceof Collection) {
            Collection toBeIncluded = (Collection) includable;
            for(Object include: toBeIncluded) {
                included.add(include);
            }
        } else {
            included.add(includable);
        }

        return this;
    }

    @Override
    public Document meta(String key, Object value) {
        meta.put(key, value);

        return this;
    }

    @Override
    public Document link(String uuid, String associationKey, String nestedKey, String location) {
        Resource resource = data.get(uuid);
        assertResourceExists(resource);

        resource.link(associationKey, nestedKey, location);

        return this;
    }

    @Override
    public Document link(String uuid, String associationKey, String location) {
        Resource resource = data.get(uuid);
        assertResourceExists(resource);

        resource.link(associationKey, location);

        return this;
    }

    @Override
    public Document linkage(String uuid, String associationKey, Object payload) {
        Resource resource = data.get(uuid);
        assertResourceExists(resource);

        resource.linkage(associationKey, payload);

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
