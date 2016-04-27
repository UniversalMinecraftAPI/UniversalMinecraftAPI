package com.koenv.universalminecraftapi.http.model;

import com.koenv.universalminecraftapi.methods.Invoker;
import com.koenv.universalminecraftapi.permissions.Permissible;
import com.koenv.universalminecraftapi.permissions.PermissionUtils;
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
    public boolean checkPermission(Permissible object) {
        return user.hasPermission(PermissionUtils.getPermissionPath(object.getJavaMethod()));
    }
}
