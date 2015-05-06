package net.balhar.jsonapi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotating field by included means that it will be serialized into the included section instead of as a part of
 * the payload. It also add its members to the linkage section of the document, where they will be serialized with
 * added type on location with associationKey of type.
 * It is marker annotation used for further processing.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Included {
    String type();
}
