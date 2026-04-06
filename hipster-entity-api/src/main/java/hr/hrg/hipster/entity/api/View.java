package hr.hrg.hipster.entity.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares read/write mode for entity view interfaces.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface View {
    WriteOption write() default WriteOption.DEFAULT;
    String discriminatorField() default "";
    Class<?>[] addons() default {};
}
