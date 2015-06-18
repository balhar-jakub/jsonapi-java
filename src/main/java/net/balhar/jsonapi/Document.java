package net.balhar.jsonapi;

/**
 * Basic abstraction used throughout this module. This abstraction allows you to create Documents containing links,
 * included and meta on the top level. It must also be capable of adding the links to the resource being modeled as
 * part of the document itself.
 * It must also be able to provide its version ready to be serialized by some mapper. Therefore as simple data
 * structure containing all necessary data on right places.
 * Unless needed for some specific reasons the object should be lazy. Meaning that representation for serialization
 * isn't created before it is needed.
 */
public interface Document {
    /**
     * It adds top level link, where key is the information about the link and location is the uri of the resource.
     *
     * @param key key which will be used by consumers to identify what is possible to do further from this resource.
     * @param location Url of resource representing some followup on this document.
     * @return Current document for further chaining.
     */
    Document link(String key, String location);

    /**
     * It includes the whole object into the final document. How exactly it will be done depends on the implementation.
     *
     * @param included Object to be included.
     * @return Current document for further chaining.
     */
    Document include(Object included);

    /**
     * It is possible to add any amount of meta data into the top level object. Only limitation is that they are in
     * the key, value format.
     *
     * @param key key under which the meta information is available
     * @param value value of the meta information
     * @return Current document for further chaining.
     */
    Document meta(String key, Object value);

    /**
     * Adds nested link to the resource with given uuid already present in the document. If there is no such resource in
     * the document then throw RuntimeException. It creates nested link in the resource identified by uuid. Nested
     * links are created under associationKey and further accessible as nestedKey.
     *
     * @param uuid uuid of resource to which the link belongs
     * @param associationKey Key under which nested link for this key will be present.
     * @param nestedKey Key under which this location is stored in associationKey store.
     * @param location Url associated with this link.
     * @return Current document for further chaining.
     */
    Document link(String uuid, String associationKey, String nestedKey, String location);

    /**
     * This method is used for creating top level links on the resource identified by the uuid. The link will be
     * under key associationKey with value location. If the resource with given uuid isn't present in the Document
     * throw RuntimeException.
     *
     * @param uuid Uuid of the resource already present in the document.
     * @param associationKey Key under which this location will be present in the links section of resources.
     * @param location Url associated with this link.
     * @return Current document for further chaining.
     */
    Document link(String uuid, String associationKey, String location);

    /**
     * Adds linkage section to resource identified by the link uuid. If the section already exists add payload to
     * this collection.
     *
     * @param associationKey Association key under which add this to the linkage.
     * @param linkUuid uuid of the resource to which this link belongs. It must be present.
     * @param payload Payload representing the linkage.
     * @return Current document for further chaining.
     */
    Document linkage(String linkUuid, String associationKey, Object payload);

    /**
     * This method returns representation of the object as a simple Dto or HashMap, which any serializer can simply
     * transform to final representation.
     *
     * @return Dto simply serializable into the data. Some of the implementation may even provide String representing
     * the object itself. Then it must be stated in implementation.
     */
    Object transform();
}
