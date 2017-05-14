package com.github.warmuuh.metaannotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

final class PreprocessingMetaAnnotationHandler implements InvocationHandler {
	private final Class<?> annotationClass;
	private final List<Annotation> inheritencePath;

	Map<String, Object> cachedValues = new HashMap<>();
	private Object annotationObject;
	
	
	
	public static InvocationHandler newInstance(Class<?> annotationClass, List<Annotation> inheritencePath) throws Exception{
		PreprocessingMetaAnnotationHandler h = new PreprocessingMetaAnnotationHandler(annotationClass, inheritencePath);
		h.initialize();
		return h;
	}
	
	private PreprocessingMetaAnnotationHandler(Class<?> annotationClass, List<Annotation> inheritencePath) {
		this.annotationClass = annotationClass;
		this.inheritencePath = inheritencePath;
		this.annotationObject = inheritencePath.get(inheritencePath.size() -1);
	}
	
	/**
	 * iterates though the inheritence path and picks up any @AliasFor annotation ment for this class and caches its value
	 * @throws Exception
	 */
	public void initialize() throws Exception {
		for (Iterator<Annotation> iterator = inheritencePath.iterator(); iterator.hasNext();) {
			Annotation annotation = iterator.next();
			
			//we dont have to check last annotation as it definitly does not contain overwrites
			//TODO: maybe AliasFor should be allowed for defining aliases within the own class
			if (!iterator.hasNext())
				break;
			
			for(Method aMethod: annotation.annotationType().getMethods()){
				AliasFor alias = aMethod.getAnnotation(AliasFor.class);
				if (alias != null && alias.annotation().equals(annotationClass)){
					Object value = aMethod.invoke(annotation);
					cachedValues.put(alias.attribute(), value);
				}
			}
		}
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		String attrName = method.getName();
		Object result = cachedValues.get(attrName);
		if (result != null)
			return result;
		
		return method.invoke(annotationObject, args);
	}

}