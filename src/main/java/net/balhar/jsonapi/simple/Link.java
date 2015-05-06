package net.balhar.jsonapi.simple;

import java.util.Objects;

/**
 * Simple representation of Link as key, url pair.
 */
public class Link {
    private String key;
    private String url;

    public Link(String key, String url){
        this.key = key;
        this.url = url;
    }

    public String getKey() {
        return key;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Link link = (Link) o;
        return Objects.equals(key, link.key) &&
                Objects.equals(url, link.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, url);
    }
}
