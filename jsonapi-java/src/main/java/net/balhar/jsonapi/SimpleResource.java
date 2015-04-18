package net.balhar.jsonapi;

import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
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
    private Object backingObject;

    public SimpleResource(Object object){
        backingObject = object;
    }

    public SimpleResource() {
        backingObject = new Object();
    }

    @Override
    public SimpleResource link(String type, String action, String location) {
        if(!links.containsKey(type)){
            links.put(type, new ArrayList<Link>());
        }
        links.get(type).add(new Link(action, location));

        return this;
    }

    public Object transform(){
        Field[] fields = backingObject.getClass().getDeclaredFields();
        Map<String, Object> representation =  new HashMap<>(fields.length + 10);
        representation.put("type", backingObject.getClass().getSimpleName());
        representation.put("links", links);

        try {
            for (Field attribute : fields) {
                attribute.setAccessible(true);
                representation.put(attribute.getName(), attribute.get(backingObject));
            }
        } catch(IllegalAccessException ex) {
            // This Exception should actually never happen.
            throw new RuntimeException("It wasn't possible to access attribute on given object.", ex);
        }

        return representation;
    }
}
