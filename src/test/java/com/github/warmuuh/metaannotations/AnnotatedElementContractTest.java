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
public class AnnotatedElementContractTest {

	@Inherited
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE})
	public @interface SimpleTestAnnotation {
		String simpleString();

		String defaultString() default "default";
	}
	
	@SimpleTestAnnotation(simpleString = "simple")
	public class AnnotatedClass{}
	
	public class DerivedClass extends AnnotatedClass{}
	
	@Test
	public void shouldReadSimpleProperties() {
		AnnotatedClass obj = new AnnotatedClass();
		SimpleTestAnnotation a = new AnnotationReader(obj.getClass()).getAnnotation(SimpleTestAnnotation.class);
		assertThat(a.simpleString()).isEqualTo("simple");		
		assertThat(a.defaultString()).isEqualTo("default");
	}
	
	@Test
	public void shouldFindAllAnnotations() {
		AnnotatedClass obj = new AnnotatedClass();
		Annotation[] as = new AnnotationReader(obj.getClass()).getAnnotations();
		assertThat(as).hasSize(1);
		assertThat(as[0]).isInstanceOf(SimpleTestAnnotation.class);
		SimpleTestAnnotation a = ((SimpleTestAnnotation)as[0]);
		assertThat(a.simpleString()).isEqualTo("simple");		
		assertThat(a.defaultString()).isEqualTo("default");
	}
	
	@Test
	public void shouldFindAllDeclaredAnnotations() {
		AnnotatedClass obj = new AnnotatedClass();
		Annotation[] as = new AnnotationReader(obj.getClass()).getDeclaredAnnotations();
		assertThat(as).hasSize(1);
		assertThat(as[0]).isInstanceOf(SimpleTestAnnotation.class);
		SimpleTestAnnotation a = ((SimpleTestAnnotation)as[0]);
		assertThat(a.simpleString()).isEqualTo("simple");		
		assertThat(a.defaultString()).isEqualTo("default");
	}
	
	@Test
	public void shouldOnlyFindDeclaredAnnotations() {
		DerivedClass obj = new DerivedClass();
		Annotation[] as = new AnnotationReader(obj.getClass()).getDeclaredAnnotations();
		assertThat(as).hasSize(0);
	}
	
	@Test
	public void shouldFindInheritedAnnotations() {
		DerivedClass obj = new DerivedClass();
		Annotation[] as = new AnnotationReader(obj.getClass()).getAnnotations();
		assertThat(as).hasSize(1);
		assertThat(as[0]).isInstanceOf(SimpleTestAnnotation.class);
	}
}
