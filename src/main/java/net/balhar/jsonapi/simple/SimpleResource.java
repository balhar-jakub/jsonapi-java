package net.balhar.jsonapi.simple;

import net.balhar.jsonapi.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Again an envelope to represent the type? If it is so then probably exclude it out as an interface and create just
 * simple Resource interpretation.
 */
public class SimpleResource implements Resource {
    private Map<String, Collection<Link>> links = new HashMap<>();
    private Map<String, String> topLevelLinks = new HashMap<>();
    private Map<String, Collection<Object>> linkage = new HashMap<>();
    private Object backingObject;
    private Document document;

    public SimpleResource(Object object, Document document){
        this.backingObject = object;
        this.document = document;
    }

    @Override
    public SimpleResource link(String type, String action, String location) {
        if(!links.containsKey(type)){
            links.put(type, new ArrayList<Link>());
        }
        links.get(type).add(new Link(action, location));

        return this;
    }

    @Override
    public Resource link(String type, String location) {
        topLevelLinks.put(type, location);

        return this;
    }

    @Override
    public Resource linkage(String type, Object payload) {
        if(!linkage.containsKey(type)){
            Collection payloads = new ArrayList();
            payloads.add(payload);
            linkage.put(type, payloads);
        } else {
            linkage.get(type).add(payload);
        }

        return this;
    }

    public Object transform() {
        Field[] fields = backingObject.getClass().getDeclaredFields();
        TransformedResource<String, Object> representation =  new TransformedResource<>();
        // Add topLevelLinks to actual links.
        Map fullLinks = new HashMap(links);
        fullLinks.putAll(topLevelLinks);

        // Also add linkage.
        serializeLinkage(representation);

        // Take into account the annotation if present.
        representation.put("type", backingObject.getClass().getSimpleName());
        representation.put("links", fullLinks);

        try {
            for (Field attribute : fields) {
                Annotation ignoreThisField = attribute.getAnnotation(Ignored.class);
                if(ignoreThisField != null) {
                    continue;
                }

                Annotation included = attribute.getAnnotation(Included.class);
                attribute.setAccessible(true);
                if(included == null) {
                    representation.put(attribute.getName(), attribute.get(backingObject));
                } else {
                    // Add it correctly to the linkage
                    linkage(attribute.getGenericType().getClass().getName(), attribute.get(backingObject));
                    // I need to actually put this information in the document. Include in the document. If
                    // collection do this for every member of the collection.
                    document.include(attribute.get(backingObject));
                }
            }
        } catch(IllegalAccessException ex) {
            // This Exception should actually never happen.
            throw new RuntimeException("It wasn't possible to access attribute on given object.", ex);
        }

        return representation;
    }

    private void serializeLinkage(TransformedResource resource){
        // Find correct links and create them if they dont exist.
    }
}
