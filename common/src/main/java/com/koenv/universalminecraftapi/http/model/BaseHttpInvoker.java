package com.koenv.universalminecraftapi.http.model;

import com.koenv.universalminecraftapi.methods.AbstractMethod;
import com.koenv.universalminecraftapi.methods.ClassMethod;
import com.koenv.universalminecraftapi.methods.Invoker;
import com.koenv.universalminecraftapi.methods.NamespacedMethod;
import com.koenv.universalminecraftapi.users.model.User;

public abstract class BaseHttpInvoker implements Invoker {
    private User user;

    public BaseHttpInvoker(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    @Override
    public boolean checkPermission(AbstractMethod method) {
        if (method instanceof NamespacedMethod) {
            NamespacedMethod namespacedMethod = (NamespacedMethod) method;
            return user.canAccessNamespaceMethod(namespacedMethod.getNamespace(), namespacedMethod.getName());
        } else if (method instanceof ClassMethod) {
            ClassMethod classMethod = (ClassMethod) method;
            return user.canAccessClassMethod(classMethod.getClass().getSimpleName(), classMethod.getName());
        }
        return false;
    }
}
