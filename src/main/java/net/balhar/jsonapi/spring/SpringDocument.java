package net.balhar.jsonapi.spring;

import net.balhar.jsonapi.Identifiable;
import net.balhar.jsonapi.simple.SimpleDocument;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collection;

/**
 * Integration of Document with Spring Framework. It should infer as much information as possible from meta information instead of specifying them explicitly.
 */
public class SpringDocument extends SimpleDocument {
    public SpringDocument(Collection<? extends Identifiable> object, String baseUrl) {
        super(object, baseUrl);
        addSelf();
    }

    public SpringDocument(Identifiable object, String baseUrl) {
        super(object, baseUrl);
        addSelf();
    }

    public SpringDocument(Collection<? extends Identifiable> objects) {
        super(objects);
        addSelf();
    }

    public SpringDocument(Identifiable object) {
        super(object);
        addSelf();
    }

    private void addSelf(){
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        RequestMapping metaInfo = elements[2].getClass().getAnnotation(RequestMapping.class);
        if(metaInfo != null){
            link("self", metaInfo.value()[0]);
        }
    }
}
