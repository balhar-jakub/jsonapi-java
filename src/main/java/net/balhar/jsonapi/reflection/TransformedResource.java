package net.balhar.jsonapi.reflection;

import net.balhar.jsonapi.ApiKeys;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple envelope about transformed resource, which purpose is to provide convenient methods.
 */
public class TransformedResource<K, V> extends HashMap<K, V> {
    public Map links(){
        return (Map) get(ApiKeys.LINKS);
    }

    public Map links(String associationKey) {
        return (Map) links().get(associationKey);
    }
}
