package com.github.warmuuh.metaannotations;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.*;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@RunWith(JUnitPlatform.class)
public class AnnotationInheritenceTest {

	@Inherited
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE})
	public @interface SimpleTestAnnotation {
		String simpleString();

		String defaultString() default "default";
	}
	
	@Inherited
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE})
	@SimpleTestAnnotation(simpleString = "simple")
	public @interface MetaAnnotation {
		String metaAnnotationString();
	}
	
	@MetaAnnotation(metaAnnotationString = "meta")
	public class AnnotatedClass{}
	
	public class DerivedClass extends AnnotatedClass{}
	
	@Test
	public void shouldFindMetaAnnotation() {
		AnnotatedClass obj = new AnnotatedClass();
		MetaAnnotation a = new AnnotationReader(obj.getClass()).getAnnotation(MetaAnnotation.class);
		assertThat(a.metaAnnotationString()).isEqualTo("meta");		
	}
	
	@Test
	public void shouldFindSimpleAnnotation() {
		AnnotatedClass obj = new AnnotatedClass();
		SimpleTestAnnotation a = new AnnotationReader(obj.getClass()).getAnnotation(SimpleTestAnnotation.class);
		assertThat(a.simpleString()).isEqualTo("simple");		
		assertThat(a.defaultString()).isEqualTo("default");
	}
	
}
