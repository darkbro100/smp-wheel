package me.paul.lads.wheel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This is an Annotation that you tag classes with that you want to be auto
 * registered at runtime. This makes it easy to register {@link WheelEffect}
 * that are only a 1 time execution and don't require any extra parameters. This
 * SHOULDNT be used if you are creating a complex {@link WheelEffect}.
 * 
 * @author Paul
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.TYPE)
public @interface GenerateEffect {

	String key();

	String name();

	String description();

	boolean enabled() default true;

}