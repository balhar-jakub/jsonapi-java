package net.balhar.jsonapi;

/**
 * Resource represents what any Document is about and therefore it is key concept. I assume that most of the Resource
 * implementations will be simple Envelopes with some added functionality.
 */
public interface Resource {
    /**
     * It creates new link which is tied to the data provided by the resource.
     *
     * @param type Type of the resource, which is also name in the collection of the links.
     * @param action action on given type of resource
     * @param location URL path to the given reesource.
     * @return Itself for chaining.
     */
    Resource link(String type, String action, String location);

    /**
     * Returns object, which is ready for serialization.
     *
     * @return Transformed object ready for serialization.
     */
    Object transform();
}
