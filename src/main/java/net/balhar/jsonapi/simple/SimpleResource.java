package net.balhar.jsonapi.simple;

import javassist.*;
import net.balhar.jsonapi.*;
import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

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

    public SimpleResource(Object object, Document document){
        this.backingObject = object;
        this.document = document;
    }

    @Override
    public SimpleResource link(String associationKey, String nestedKey, String location) {
        if(!links.containsKey(associationKey)){
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

        if(!links.containsKey(associationKey)){
            links.put(associationKey, new HashMap<String, Object>());
        }
        Map<String, Object> actualLinks = links.get(associationKey);

        if(!actualLinks.containsKey(ApiKeys.LINKAGE)){
            actualLinks.put(ApiKeys.LINKAGE, new ArrayList<>());
        }
        Collection linkage = (Collection) actualLinks.get(ApiKeys.LINKAGE);
        linkage.add(payload);

        return this;
    }

    public Object transform() {
        Field[] fields = backingObject.getClass().getDeclaredFields();
        TransformedResource<String, Object> representation =  new TransformedResource<>();
        // Add topLevelLinks to actual links.

        try {
            for (Field attribute : fields) {
                Annotation ignoreThisField = attribute.getAnnotation(Ignored.class);
                if(ignoreThisField != null) {
                    continue;
                }

                Included included = attribute.getAnnotation(Included.class);
                attribute.setAccessible(true);
                if(included == null) {
                    representation.put(attribute.getName(), attribute.get(backingObject));
                } else {
                    if(attribute.get(backingObject) instanceof Collection) {
                        Collection toLink = (Collection) attribute.get(backingObject);
                        for(Object link: toLink) {
                            linkage(included.type(), addType(link));
                        }
                    } else {
                        linkage(included.type(), addType(attribute.get(backingObject)));
                    }
                    document.include(attribute.get(backingObject));
                }
            }
        } catch(IllegalAccessException ex) {
            // This Exception should actually never happen.
            throw new RuntimeException("It wasn't possible to access attribute on given object.", ex);
        }

        Map fullLinks = new HashMap(links);
        fullLinks.putAll(topLevelLinks);

        // Take into account the annotation if present.
        representation.put("type", backingObject.getClass().getSimpleName());
        representation.put("links", fullLinks);

        return representation;
    }

    private Object addType(Object linkage){
        Object typedLink;
        try {
            ClassPool pool = ClassPool.getDefault();
            Class instantiable;

            // Create the class, but first check whether it doesn't exist, if it exists use it.
            CtClass exists = pool.getOrNull(linkage.getClass().getCanonicalName() + "TypedJSONApi");
            if(exists == null) {
                CtClass subClass = pool.makeClass(linkage.getClass().getCanonicalName() + "TypedJSONApi");
                final CtClass superClass = pool.get(linkage.getClass().getName());
                subClass.setSuperclass(superClass);
                subClass.setModifiers(Modifier.PUBLIC);

                // Add a constructor which will call super( ... );
                final CtConstructor ctor = CtNewConstructor.make(null, null, CtNewConstructor.PASS_PARAMS, null, null,
                        subClass);
                subClass.addConstructor(ctor);
                CtClass string = ClassPool.getDefault().get(String.class.getCanonicalName());
                CtField typeToBeAdded = new CtField(string, ApiKeys.TYPE, subClass);
                subClass.addField(typeToBeAdded);
                instantiable = subClass.toClass();
            } else {
                instantiable = Class.forName(linkage.getClass().getCanonicalName() + "TypedJSONApi");
            }
            typedLink = instantiable.newInstance();

            mapFields(linkage, typedLink);
            Field type = typedLink.getClass().getDeclaredField(ApiKeys.TYPE);

            // Either to type based on the type annotation on the class or to the type as default.
            Type typeAnnotation = linkage.getClass().getAnnotation(Type.class);
            String typeValue;
            if(typeAnnotation != null) {
                typeValue = typeAnnotation.name();
            } else {
                typeValue = linkage.getClass().getSimpleName();
            }
            type.set(typedLink, typeValue);
        } catch (NotFoundException| CannotCompileException| InstantiationException| IllegalAccessException|
                NoSuchFieldException| ClassNotFoundException e) {
            throw new RuntimeException("It wasn't possible to modify class to typed.", e);
        }

        return typedLink;
    }

    private void mapFields(Object linkage, Object typedLink) throws IllegalAccessException {
        // Get all fields from linkage and then apply
        Field[] fields = linkage.getClass().getDeclaredFields();
        for(Field field: fields) {
            field.setAccessible(true);
            field.set(typedLink, field.get(linkage));
        }
    }
}
