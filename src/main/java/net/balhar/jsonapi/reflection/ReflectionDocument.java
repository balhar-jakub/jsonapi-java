package net.balhar.jsonapi.reflection;

import net.balhar.jsonapi.ApiKeys;
import net.balhar.jsonapi.Document;
import net.balhar.jsonapi.Identifiable;
import net.balhar.jsonapi.Resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * There is concept of link on top level of the document.
 */
public class ReflectionDocument implements Document {
    private String baseUrl = "";

    private Map<String, Resource> data = new HashMap<>();
    private Collection<Object> included = new ArrayList<>();
    private Map<String, String> links = new HashMap<>();
    private Map<String, Object> meta = new HashMap<>();

    private boolean isSingleItem = false;
    private Resource singleData;

    public ReflectionDocument(Collection<? extends Identifiable> object, String baseUrl){
        this(object);
        this.baseUrl = baseUrl;
    }

    public ReflectionDocument(Identifiable object, String baseUrl){
        this(object);
        this.baseUrl = baseUrl;
    }

    public ReflectionDocument(Collection<? extends Identifiable> objects){
        // Retrieve uuid from the object.
        for(Identifiable object: objects) {
            data.put(object.getUuid(), new ReflectionResource(object, this));
        }
    }

    public ReflectionDocument(Identifiable object) {
        singleData = new ReflectionResource(object, this);
        data.put(object.getUuid(), singleData);
        isSingleItem = true;
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

        if(!isSingleItem) {
            Collection<Object> transformedResources = new ArrayList<>();
            Collection<Resource> resources = data.values();
            for (Resource resource : resources) {
                transformedResources.add(resource.transform());
            }
            result.put(ApiKeys.DATA, transformedResources);
        } else {
            result.put(ApiKeys.DATA, singleData.transform());
        }

        result.put(ApiKeys.LINKS, links);
        if(!meta.isEmpty()) result.put(ApiKeys.META, meta);
        if(!included.isEmpty()) result.put(ApiKeys.INCLUDED, included);

        return result;
    }
}
