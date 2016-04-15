package com.koenv.jsonapi.http.model;

import com.koenv.jsonapi.methods.AbstractMethod;
import com.koenv.jsonapi.methods.ClassMethod;
import com.koenv.jsonapi.methods.Invoker;
import com.koenv.jsonapi.methods.NamespacedMethod;
import com.koenv.jsonapi.users.model.User;

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
