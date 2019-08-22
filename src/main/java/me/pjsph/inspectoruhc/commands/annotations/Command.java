package me.pjsph.inspectoruhc.commands.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Command {

    String name();

    String permission() default "";

    boolean inheritPermission() default true;

    boolean noPermission() default false;

}
