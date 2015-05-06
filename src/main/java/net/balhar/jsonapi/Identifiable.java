package net.balhar.jsonapi;

/**
 * This interface tells me that the entity is identifiable but it doesn't tell me anything about data, which are part
 * of the complex entities.
 */
public interface Identifiable {
    String getUuid();
}
