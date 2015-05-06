package net.balhar.jsonapi.reflection;

import javassist.*;
import net.balhar.jsonapi.ApiKeys;
import net.balhar.jsonapi.Type;

import java.lang.reflect.Field;

/**
 * Reflection based modification of used class.
 */
public class TypedClass {
    private Object internal;

    public TypedClass(Object link) {
        this.internal = link;
    }

    private Object addType(Object linkage){
        Object typedLink;
        try {
            ClassPool pool = ClassPool.getDefault();
            Class instantiable;

            // Create the class, but first check whether it doesn't exist, if it exists use it.
            CtClass exists = pool.getOrNull(linkage.getClass().getCanonicalName() + "TypedJSONApi");
            if(exists == null) {
                CtClass subClass = pool.makeClass(linkage.getClass().getCanonicalName() + "TypedJSONApi");
                final CtClass superClass = pool.get(linkage.getClass().getName());
                subClass.setSuperclass(superClass);
                subClass.setModifiers(Modifier.PUBLIC);

                // Add a constructor which will call super( ... );
                final CtConstructor ctor = CtNewConstructor.make(null, null, CtNewConstructor.PASS_PARAMS, null, null,
                        subClass);
                subClass.addConstructor(ctor);
                CtClass string = ClassPool.getDefault().get(String.class.getCanonicalName());
                CtField typeToBeAdded = new CtField(string, ApiKeys.TYPE, subClass);
                subClass.addField(typeToBeAdded);
                instantiable = subClass.toClass();
            } else {
                instantiable = Class.forName(linkage.getClass().getCanonicalName() + "TypedJSONApi");
            }
            typedLink = instantiable.newInstance();

            mapFields(linkage, typedLink);
            Field type = typedLink.getClass().getDeclaredField(ApiKeys.TYPE);

            // Either to type based on the type annotation on the class or to the type as default.
            Type typeAnnotation = linkage.getClass().getAnnotation(Type.class);
            String typeValue;
            if(typeAnnotation != null) {
                typeValue = typeAnnotation.name();
            } else {
                typeValue = linkage.getClass().getSimpleName();
            }
            type.setAccessible(true);
            type.set(typedLink, typeValue);
        } catch (NotFoundException| CannotCompileException| InstantiationException| IllegalAccessException|
                NoSuchFieldException| ClassNotFoundException e) {
            throw new RuntimeException("It wasn't possible to modify class to typed.", e);
        }

        return typedLink;
    }

    private void mapFields(Object linkage, Object typedLink) throws IllegalAccessException {
        // Get all fields from linkage and then apply
        Field[] fields = linkage.getClass().getDeclaredFields();
        for(Field field: fields) {
            field.setAccessible(true);
            field.set(typedLink, field.get(linkage));
        }
    }

    public Object transform() {
        return addType(internal);
    }
}
