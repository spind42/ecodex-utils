package eu.ecodex.utils.configuration.api.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface ConfigurationLabel {

    @AliasFor("label")
    public String value() default "";

    @AliasFor("value")
    public String label() default "";
}
