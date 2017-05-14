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
public class AliasForTest {

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
	@SimpleTestAnnotation(simpleString = "")
	public @interface MetaAnnotation {
		String metaAnnotationString();
		
		@AliasFor( attribute = "simpleString", annotation = SimpleTestAnnotation.class)
		String metaSimpleString();
	}
	
	@MetaAnnotation(metaAnnotationString = "meta", metaSimpleString = "metaSimpleString")
	public class AnnotatedClass{}
	
	public class DerivedClass extends AnnotatedClass{}
	

	@Test
	public void shouldReadOverridenValues() {
		AnnotatedClass obj = new AnnotatedClass();
		SimpleTestAnnotation a = new AnnotationReader(obj.getClass()).getAnnotation(SimpleTestAnnotation.class);
		assertThat(a.simpleString()).isEqualTo("metaSimpleString");		
		assertThat(a.defaultString()).isEqualTo("default");
	}
	
}
