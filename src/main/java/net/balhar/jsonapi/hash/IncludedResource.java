package net.balhar.jsonapi.hash;

/**
 * It is possible to include resources in different ways, which may be represented by implementation of this interface.
 */
public interface IncludedResource {
    /**
     * It transforms passed in resource into object, which can be handled further by the integration.
     * @return Object which can be correctly marshalled into any type.
     */
    Object transform();
}
