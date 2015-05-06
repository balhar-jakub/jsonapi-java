package net.balhar.jsonapi;

import java.util.Collection;

/**
 * Basic abstraction used throughout this module. This abstraction allows you to create Documents containing links,
 * included and meta on the top level. It must also be capable of adding the links to the resource being modeled as
 * part of the document itself.
 * It must also be able to provide its version ready to be serialized by some mapper. Therefore as simple data
 * structure containing all necessary data on right places.
 * Unless needed for some specific reasons the object should be lazy. Meaning that representation for serialization
 * isn't created before it is needed.
 *
 * @see http://jsonapi.org/ Specification for the JsonApiDocument.
 */
public interface Document {
    /**
     * It adds top level link, where key is the information about the link and location is the uri of the resource.
     *
     * @param key key which will be used by consumers to identify what is possible to do further from this resource.
     * @param location Url of resource representing some followup on this document.
     */
    Document link(String key, String location);

    /**
     * It includes the whole object into the final document. How exactly it will be done depends on the implementation.
     *
     * @param included Object to be included.
     */
    Document include(Object included);

    /**
     * It is possible to add any amount of meta data into the top level object. Only limitation is that they are in
     * the key, value format.
     *
     * @param key key under which the meta information is available
     * @param value value of the meta information
     */
    Document meta(String key, Object value);

    /**
     * Adds link to the resource with given uuid already present in the document. If there is no such resource in the
     * document then throw Exception.
     *
     * @param uuid uuid of resource to which the link belongs
     */
    Document link(String uuid, String type, String key, String location);

    Document link(String uuid, String type, String location);

    /**
     * Adds linkage section to resource identified by the link uuid. If the section already exists add payload to
     * this collection.
     *
     * @param linkUuid uuid of the resource to which this link belongs. It must be present.
     * @param payload Payload representing the linkage.
     */
    Document linkage(String linkUuid, Object payload);

    /**
     * This method returns representation of the object as a simple Dto or HashMap, which any serializer can simply
     * transform to final representation.
     *
     * @return Dto simply serializable into the data. Some of the implementation may even provide String representing
     * the object itself. Then it must be stated in implementation.
     */
    Object transform();
}
