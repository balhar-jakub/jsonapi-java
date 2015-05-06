package net.balhar.jsonapi.simple;

import net.balhar.jsonapi.ApiKeys;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *  It provides some nice functionality on top of the HashMap to retrieve just parts of the TransformedDocument
 */
public class TransformedDocument<K, V> extends HashMap<K, V> {
    public Map meta() {
        return (Map) get(ApiKeys.META);
    }

    public Collection data() {
        return (Collection) get(ApiKeys.DATA);
    }

    public Collection links(){
        return (Collection) get(ApiKeys.LINKS);
    }

    public Collection included() {
        return (Collection) get(ApiKeys.INCLUDED);
    }
}

