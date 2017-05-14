package com.github.warmuuh.metaannotations;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target( ElementType.METHOD)
public @interface AliasFor {

	/**
	 * The name of the attribute that <em>this</em> attribute is an alias for.
	 */
	String attribute();

	/**
	 * The type of annotation in which the aliased {@link #attribute} is declared.
	 */
	Class<? extends Annotation> annotation();
}
