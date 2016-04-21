package com.koenv.universalminecraftapi.methods;

@ExcludeFromDoc
public interface Invoker {
    default boolean checkPermission(AbstractMethod method) {
        return true;
    }
}
