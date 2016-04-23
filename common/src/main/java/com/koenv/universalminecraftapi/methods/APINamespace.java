package com.koenv.universalminecraftapi.methods;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Can be added at the top of a class to not have to specify the namespace on every method
 *
 * For example:
 * <pre>
 *     &#64;APINamespace("test")
 *     public class TestMethods {
 *          &#64;APIMethod
 *          public static String getIt() {
 *              return "test":
 *          }
 *     }
 * </pre>
 * This method is now callable by using <code>test.getIt()</code>.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface APINamespace {
    String value();
}
