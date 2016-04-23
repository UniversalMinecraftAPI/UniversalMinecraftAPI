package com.koenv.universalminecraftapi.methods;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Denotes that a parameter for an API method is optional.
 *
 * `null` will be passed to the method if this parameter is not passed.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Optional {
}
