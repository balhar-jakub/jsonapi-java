package net.balhar.jsonapi;

/**
 * Resource represents what any Document is about and therefore it is key concept. I assume that most of the Resource
 * implementations will be simple Envelopes with some added functionality.
 */
public interface Resource {
    /**
     * It creates new link which is tied to the data provided by the resource.
     *
     * @param associationKey Type of the resource, which is also name in the collection of the links.
     * @param nestedKey action on given type of resource
     * @param location URL path to the given reesource.
     * @return Itself for chaining.
     */
    Resource link(String associationKey, String nestedKey, String location);

    /**
     * It creates new link, which is on top level in links. Links represent only links to given resources.
     *
     * @param associationKey Key under which the data will be stored.
     * @param location URL path to the resource.
     * @return Itself for further chaining.
     */
    Resource link(String associationKey, String location);

    /**
     * It adds linkage to the document. Based on this it should probably also be able to fill included. There must be
     * some interface, which provide way how to retrieve the relevant information. Probably try expecting the object,
     * which will contain all information. I would say by default add it to the JSON Document, but it isn't a way.
     *
     * @param type Type on which is the linkage added.
     * @param payload Payload for the linkage
     * @return Itself for chaining
     */
    Resource linkage(String type, Object payload);

    /**
     * Returns object, which is ready for serialization.
     *
     * @return Transformed object ready for serialization.
     */
    Object transform();
}
