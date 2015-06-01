package net.balhar.jsonapi.hash;

import net.balhar.jsonapi.Document;
import net.balhar.jsonapi.Identifiable;
import net.balhar.jsonapi.Resource;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * It represents simple resource which should be included as is, only additional linkage should be added.
 */
public class SimpleIncludedResource implements IncludedResource {
    private Document document;
    private Object toTransform;
    private String type;

    public SimpleIncludedResource(Document document, Resource resource, Object objectToTransform, String type) {
        this.document = document;
        this.toTransform = objectToTransform;
        this.type = type;
    }

    @Override
    public Object transform() {
        if (toTransform instanceof Collection) {
            Collection<Identifiable> toLink = (Collection<Identifiable>) toTransform;
            for (Identifiable link : toLink) {
                // Somehow map the attributes of the object instead of extending it. How do I want to proceed when
                // there are other objects nested inside?
                if (link != null) {
                    handleIncluded(link);
                }
            }
        } else {
            handleIncluded((Identifiable) toTransform);
        }

        Map transformed = new HashMap();

        includeInDocument();

        return transformed;
    }

    private void handleIncluded(Identifiable link) {
        Map includedObject = new HashMap();
        createLinkage(link.getUuid());
        //linkage(type, linkageObject);
        //mapFields(link, includedObject);
        includedObject.put("type", type);
        document.include(includedObject);
    }

    private Map createLinkage(String uuid) {
        Map linkageObject = new HashMap();
        linkageObject.put("uuid", uuid);
        linkageObject.put("type", type);
        return linkageObject;
    }

    private void includeInDocument() {

    }
}
