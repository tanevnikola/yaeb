package ft.eventpublisher.annotation;

import ft.eventpublisher.matcher.EventMatcher;
import ft.eventpublisher.matcher.RegexEventMatcher;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE})
public @interface Matches {
    Class<? extends EventMatcher> matcher() default RegexEventMatcher.class;

    String[] value();
}
