# meta-annotations

This project contains utility to process meta-annotations as used in spring.

It is intended to be used in case of non-spring projects or in spring prior to version 4. (in Spring 4, you have [AnnotationUtil](http://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/core/annotation/AnnotationUtils.html) with Meta-Annotation support and `@AliasFor` included)

Meta-annotations are annotations that override values from other annotations.

## installation

Step 1. Add the JitPack repository to your build file

	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
	
Step 2. Add the dependency

	<dependency>
	    <groupId>com.github.warmuuh</groupId>
	    <artifactId>meta-annotations</artifactId>
	    <version>0.0.1</version>
	</dependency>
	
## usage

Given a normal annotation

```
//retention etc
public @interface SimpleAnnotation {
	String simpleString();
}
```

a meta-annotation looks like this:

```
//retention etc
@SimpleAnnotation(simpleString = "anotherValue")
public @interface MetaAnnotation {
}
```

and this is how to read those annotations:

```
@MetaAnnotation()
public class AnnotatedClass{}

// in the code later on:
AnnotatedClass obj = new AnnotatedClass();
SimpleAnnotation a = new AnnotationReader(obj.getClass()).getAnnotation(SimpleAnnotation.class);
assertThat(a.simpleString()).isEqualTo("anotherValue");	
```

### aliasing
attributes can also be inherited and renamed so that an override can happen partially.

```
public @interface SimpleTestAnnotation {
	String stringA();
	String stringB();
}


@SimpleTestAnnotation(stringA = "", stringB = "world")
public @interface MetaAnnotation {
	@AliasFor( attribute = "stringA", annotation = SimpleTestAnnotation.class)
	String metaStringA();
}

@MetaAnnotation(metaStringA = "hello")
public class AnnotatedClass{}



AnnotatedClass obj = new AnnotatedClass();
SimpleTestAnnotation a = new AnnotationReader(obj.getClass()).getAnnotation(SimpleTestAnnotation.class);
assertThat(a.stringA()).isEqualTo("hello");		
assertThat(a.stringB()).isEqualTo("world");
```
