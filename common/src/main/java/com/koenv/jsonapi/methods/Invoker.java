package com.koenv.jsonapi.methods;

@ExcludeFromDoc
public interface Invoker {
    default boolean checkPermission(AbstractMethod method) {
        return true;
    }
}
