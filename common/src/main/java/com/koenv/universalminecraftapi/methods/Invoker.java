package com.koenv.universalminecraftapi.methods;

@ExcludeFromDoc
public interface Invoker {
    /**
     * Checks whether access is allowed to this method. This could be used if this interface is implemented by an
     * invoker which has permissions.
     *
     * @param method The method for which access should be checked
     * @return True if permission is granted, false otherwise
     */
    default boolean checkPermission(AbstractMethod method) {
        return true;
    }
}
