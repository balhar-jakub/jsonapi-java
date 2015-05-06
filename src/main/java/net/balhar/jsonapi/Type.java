package net.balhar.jsonapi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Default specification is that type will be inferred based on the name of the class you can override this behavior
 * by annotating the class with Type annotation and name, which should be used as part of payload as a type.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Type {
    String name();
}
