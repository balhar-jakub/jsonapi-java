package net.balhar.jsonapi.hash;

import javassist.Modifier;
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
 * I do want to keep resource as I want to be able to provide fluent API to add.
 */
public class HashResource implements Resource {
    private Map<String, Map<String, Object>> links = new HashMap<>();
    private Map<String, String> topLevelLinks = new HashMap<>();

    private Object backingObject;
    private Document document;

    public HashResource(Object object, Document document) {
        this.backingObject = object;
        this.document = document;
    }

    @Override
    public HashResource link(String associationKey, String nestedKey, String location) {
        if (!links.containsKey(associationKey)) {
            links.put(associationKey, new HashMap<String, Object>());
        }
        links.get(associationKey).put(nestedKey, location);

        return this;
    }

    @Override
    public Resource link(String associationKey, String location) {
        topLevelLinks.put(associationKey, location);

        return this;
    }

    @Override
    public Resource linkage(String associationKey, Object payload) {
        // It must be more complex as you need to include in runtime type. Can I on the fly create subclass with
        // added field?

        if (!links.containsKey(associationKey)) {
            links.put(associationKey, new HashMap<String, Object>());
        }
        Map<String, Object> actualLinks = links.get(associationKey);

        if (!actualLinks.containsKey(ApiKeys.LINKAGE)) {
            actualLinks.put(ApiKeys.LINKAGE, new ArrayList<>());
        }
        Collection linkage = (Collection) actualLinks.get(ApiKeys.LINKAGE);
        linkage.add(payload);

        return this;
    }

    public Object transform() {
        Field[] fields = backingObject.getClass().getDeclaredFields();
        TransformedResource<String, Object> representation = new TransformedResource<>();

        try {
            for (Field attribute : fields) {
                Annotation ignoreThisField = attribute.getAnnotation(Ignored.class);
                if (ignoreThisField != null) {
                    continue;
                }

                Included included = attribute.getAnnotation(Included.class);
                attribute.setAccessible(true);
                if (included == null) {
                    representation.put(attribute.getName(), attribute.get(backingObject));
                } else if (attribute.get(backingObject) != null) {
                    handleIncluded(attribute, included);
                }
            }
        } catch (IllegalAccessException ex) {
            // This Exception should actually never happen.
            throw new RuntimeException("It wasn't possible to access attribute on given object.", ex);
        }

        Map fullLinks = new HashMap(links);
        // Add topLevelLinks to actual links.
        fullLinks.putAll(topLevelLinks);

        addCorrectType(representation, fullLinks);

        return representation;
    }

    // I would say for first iteration just copy attributes.
    private void handleIncluded(Field attribute, Included included) throws IllegalAccessException {
        if (attribute.get(backingObject) instanceof Collection) {
            Collection<Identifiable> toLink = (Collection<Identifiable>) attribute.get(backingObject);
            for (Identifiable link : toLink) {
                // Somehow map the attributes of the object instead of extending it. How do I want to proceed when
                // there are other objects nested inside?
                if (link != null) {
                    createLinkage(link, included.type());
                    includeInDocument(link, included.type());
                }
            }
        } else {
            Identifiable link = (Identifiable) attribute.get(backingObject);
            createLinkage(link, included.type());
            includeInDocument(link, included.type());
        }
    }

    private void createLinkage(Identifiable includedObject, String type) {
        Map linkageObject = new HashMap();
        linkageObject.put("uuid", includedObject.getUuid());
        linkageObject.put("type", type);
        linkage(type, linkageObject);
    }

    private void includeInDocument(Identifiable toBeIncluded, String type) throws IllegalAccessException {
        Map includedObject = new HashMap();
        mapFields(toBeIncluded, includedObject);
        includedObject.put("type", type);
        document.include(includedObject);
    }

    // Take into account the annotation if present.
    private void addCorrectType(TransformedResource representation, Map fullLinks) {
        Type typeAnnotation = backingObject.getClass().getAnnotation(Type.class);
        if (typeAnnotation == null) {
            representation.put(ApiKeys.TYPE, backingObject.getClass().getSimpleName());
        } else {
            representation.put(ApiKeys.TYPE, typeAnnotation.name());
        }
        representation.put(ApiKeys.LINKS, fullLinks);
    }

    private void mapFields(Object linkage, Map included) throws IllegalAccessException {
        // Get all fields from linkage and then apply
        Field[] fields = linkage.getClass().getDeclaredFields();
        for(Field field: fields) {
            field.setAccessible(true);
            // If it is static it won't work.
            if(!Modifier.isFinal(field.getModifiers())) {
                included.put(field.getName(), field.get(linkage));
            }
        }
    }
}
