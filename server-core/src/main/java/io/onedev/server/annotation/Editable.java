package io.onedev.server.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Editable {

	String group() default "";
	
	String icon() default "";
	
	String name() default "";
	
	String placeholder() default "";
	
	String placeholderProvider() default "";
	
	String rootPlaceholder() default "";
	
	String rootPlaceholderProvider() default "";
	
	boolean displayPlaceholderAsValue() default false;
	
	int order() default 0;
	
	String description() default "";
	
	String descriptionProvider() default "";

	boolean translatable() default true;
	
	boolean hidden() default false;
}
