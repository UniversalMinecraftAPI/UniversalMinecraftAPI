package com.koenv.universalminecraftapi.http.rest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RestOperation {
    /**
     * The class this operation operates on.
     *
     * @return The class this operation operates on
     */
    Class<?> value();

    /**
     * The path that is used. If empty, will be determined automatically.
     * @return The path used
     */
    String path() default "";

    /**
     *
     */
    RestMethod method() default RestMethod.DEFAULT;
}
