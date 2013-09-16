package mixedserver.protocol.jsonrpc.annotate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = {ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD})
@Retention(value = RetentionPolicy.RUNTIME)
@JpoxyAnnotation
public @interface JpoxyIgnore {
    public boolean value() default true;
}
