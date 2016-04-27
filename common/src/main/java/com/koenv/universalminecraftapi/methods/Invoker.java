package com.koenv.universalminecraftapi.methods;

import com.koenv.universalminecraftapi.permissions.Permissible;

@ExcludeFromDoc
public interface Invoker {
    /**
     * Checks whether access is allowed to this method. This could be used if this interface is implemented by an
     * invoker which has permissions.
     *
     * @param object The method for which access should be checked
     * @return True if permission is granted, false otherwise
     */
    default boolean checkPermission(Permissible object) {
        return true;
    }
}
