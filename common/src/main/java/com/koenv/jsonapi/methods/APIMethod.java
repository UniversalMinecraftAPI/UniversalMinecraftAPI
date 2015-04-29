package com.koenv.jsonapi.methods;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface APIMethod {
    String namespace() default "";
    Class<?> operatesOn() default DEFAULT.class;
    String description() default "";

    String returnDescription() default "";

    String[] argumentDescriptions() default {};

    static final class DEFAULT {};
}