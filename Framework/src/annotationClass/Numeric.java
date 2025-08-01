package annotationClass;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Numeric {
    String message() default "Ce champ doit être un nombre valide.";
}