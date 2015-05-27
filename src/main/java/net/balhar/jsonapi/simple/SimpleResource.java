package net.balhar.jsonapi.simple;

import net.balhar.jsonapi.*;
import net.balhar.jsonapi.reflection.TypedClass;

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
public class SimpleResource implements Resource {
    private Map<String, Map<String, Object>> links = new HashMap<>();
    private Map<String, String> topLevelLinks = new HashMap<>();

    private Object backingObject;
    private Document document;

    public SimpleResource(Object object, Document document) {
        this.backingObject = object;
        this.document = document;
    }

    @Override
    public SimpleResource link(String associationKey, String nestedKey, String location) {
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

    private void handleIncluded(Field attribute, Included included) throws IllegalAccessException {
        if (attribute.get(backingObject) instanceof Collection) {
            Collection toLink = (Collection) attribute.get(backingObject);
            for (Object link : toLink) {
                if (link != null) {
                    Object typedResult = new TypedClass(link).transform();
                    linkage(included.type(), typedResult);
                    document.include(typedResult);
                }
            }
        } else {
            Object typedResult = new TypedClass(attribute.get(backingObject)).transform();

            linkage(included.type(), typedResult);
            document.include(typedResult);
        }
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
}
