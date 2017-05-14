package com.github.warmuuh.metaannotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import lombok.SneakyThrows;

public class AnnotationReader implements AnnotatedElement {
	
	final Class<?> annotatedClass;

	public AnnotationReader(Class<?> annotatedClass) {
		super();
		this.annotatedClass = annotatedClass;
	}

	@Override
	public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
		List<Annotation> inheritencePath = lookupMetaAnnotationPath(annotationClass, annotatedClass);
		if (inheritencePath.isEmpty())
			return null;
		
		return getExtendedAnnotationProxy(inheritencePath, annotationClass);
	}

	@SuppressWarnings("unchecked") @SneakyThrows
	private <T extends Annotation> T getExtendedAnnotationProxy(final List<Annotation> inheritencePath, final Class<T> annotationClass) {
		InvocationHandler handler = PreprocessingMetaAnnotationHandler.newInstance(annotationClass, inheritencePath);
		return (T) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{annotationClass}, handler);
	}

	@Override
	public Annotation[] getAnnotations() {
		return annotatedClass.getAnnotations();
	}

	@Override
	public Annotation[] getDeclaredAnnotations() {
		return annotatedClass.getDeclaredAnnotations();
	}
	
	
	/**
	 * iterates over all annotation and checks their meta-annotations, if the queried annotation is there.
	 * recurses depth-first into those annotations
	 * 
	 * takes the  first match
	 * 
	 * @param annotationClass
	 * @return inheritence path of annotation with last element being the lookedup annotation
	 */
	private  <T extends Annotation> List<Annotation> lookupMetaAnnotationPath(Class<T> metaAnnotationClass, Class<?> curElement) {
		//is this element annotated with queried annotation?
		Annotation metaAnnotation = curElement.getAnnotation(metaAnnotationClass);
		if (metaAnnotation != null)
			return Arrays.asList(metaAnnotation);
		
		Annotation[] annotations = curElement.getAnnotations();
		for (Annotation annotation : annotations) {
			//is any annotation on this element annotated with the queried annotation?
			List<Annotation> metaAnnotationPath = lookupMetaAnnotationPath(metaAnnotationClass, annotation.annotationType());
			if (!metaAnnotationPath.isEmpty()){
				LinkedList<Annotation> newPath = new LinkedList<>(metaAnnotationPath);
				newPath.push(annotation);
				return newPath;
			}
		}
		return Collections.emptyList();
	}
}
