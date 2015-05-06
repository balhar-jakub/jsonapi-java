package net.balhar.jsonapi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * If used on field it states that this field should be ignored by Json API Document and not added to the transformed
 * piece ready to be serialized.
 * It is just marker interface used for further processing of the data.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Ignored {}
