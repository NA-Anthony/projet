package annotationClass;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Date {
    String format() default "yyyy-MM-dd";
    String message() default "La date doit être au format yyyy-MM-dd";
}