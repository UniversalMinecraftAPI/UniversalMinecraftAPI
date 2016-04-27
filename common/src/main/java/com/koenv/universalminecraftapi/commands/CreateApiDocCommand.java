package com.koenv.universalminecraftapi.commands;

import com.koenv.universalminecraftapi.ChatColor;
import com.koenv.universalminecraftapi.UniversalMinecraftAPIInterface;
import com.koenv.universalminecraftapi.http.rest.*;
import com.koenv.universalminecraftapi.methods.*;
import com.koenv.universalminecraftapi.permissions.PermissionUtils;
import com.koenv.universalminecraftapi.util.json.JSONArray;
import com.koenv.universalminecraftapi.util.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

public class CreateApiDocCommand extends Command {
    @Override
    public void onCommand(UniversalMinecraftAPIInterface uma, CommandSource commandSource, String[] args) {
        if (args.length < 1) {
            args = new String[]{uma.getProvider().getPlatform() + ".json"}; // default file name: <platform>.json
        }

        File file = new File(args[0]);
        if (file.exists()) {
            commandSource.sendMessage(ChatColor.RED, "File " + file.getPath() + " already exists, aborting");
            return;
        }

        // v1 methods
        MethodInvoker methodInvoker = uma.getMethodInvoker();

        JSONObject root = new JSONObject();

        JSONObject v1 = new JSONObject();

        JSONArray namespaces = new JSONArray();

        methodInvoker.getNamespaces().values().stream().flatMap(map -> map.values().stream()).forEach(method -> namespaces.put(getV1JsonMethod(method)));

        v1.put("namespaces", namespaces);

        JSONArray classes = new JSONArray();

        methodInvoker.getClasses().values().stream().flatMap(map -> map.values().stream()).forEach(method -> classes.put(getV1JsonMethod(method)));

        v1.put("classes", classes);

        JSONArray streams = new JSONArray();
        uma.getStreamManager().getStreams().stream().forEach(streams::put);

        v1.put("streams", streams);

        root.put("v1", v1);

        // v2 methods
        RestHandler restHandler = uma.getRestHandler();
        JSONObject v2 = new JSONObject();

        JSONArray resources = new JSONArray();

        restHandler.getResources().stream().forEach(method -> resources.put(getV2RestResourceMethod(method)));

        v2.put("resources", resources);

        JSONArray operations = new JSONArray();

        restHandler.getOperations().values().stream().flatMap(map -> map.values().stream()).forEach(method -> operations.put(getV2RestOperationMethod(method)));

        v2.put("operations", operations);

        root.put("v2", v2);

        JSONObject platform = new JSONObject();
        platform.put("name", uma.getProvider().getPlatform());
        platform.put("version", uma.getProvider().getPlatformVersion());
        platform.put("umaVersion", uma.getProvider().getUMAVersion());

        root.put("platform", platform);

        try (PrintWriter printWriter = new PrintWriter(file)) {
            printWriter.write(root.toString(4));
        } catch (IOException e) {
            e.printStackTrace();
            commandSource.sendMessage(ChatColor.RED, "Failed to write to file: " + e.toString());
            return;
        }

        commandSource.sendMessage(ChatColor.GREEN, "API documentation saved to file " + file.getPath());
    }

    private JSONObject getV1JsonMethod(AbstractMethod methodEntry) {
        JSONObject jsonMethod = new JSONObject();

        Method method = methodEntry.getJavaMethod();

        jsonMethod.put("name", methodEntry.getName());

        int parameterCount = method.getParameters().length;
        Stream<Parameter> stream = Arrays.stream(method.getParameters());
        if (methodEntry instanceof ClassMethod) {
            stream = stream.skip(1);
            parameterCount--;
        }

        if (parameterCount > 0) {
            JSONArray arguments = new JSONArray();
            stream
                    .filter(parameter -> !MethodUtils.shouldExcludeFromDoc(parameter))
                    .forEach(parameter -> {
                        JSONObject json = new JSONObject();
                        json.put("name", parameter.getName());
                        json.put("type", parameter.getType().getSimpleName());
                        json.put("optional", parameter.getAnnotation(OptionalParam.class) != null);
                        arguments.put(json);
                    });
            jsonMethod.put("arguments", arguments);
        } else {
            jsonMethod.put("arguments", new JSONArray());
        }

        jsonMethod.put("returns", getReturnType(method));

        if (methodEntry instanceof ClassMethod) {
            jsonMethod.put("operatesOn", ((ClassMethod) methodEntry).getOperatesOn().getSimpleName());
        } else if (methodEntry instanceof NamespacedMethod) {
            jsonMethod.put("namespace", ((NamespacedMethod) methodEntry).getNamespace());
        }

        jsonMethod.put("permission", PermissionUtils.getPermissionPath(methodEntry.getJavaMethod()));

        return jsonMethod;
    }

    private JSONObject getV2RestResourceMethod(RestResourceMethod methodEntry) {
        JSONObject jsonMethod = new JSONObject();

        Method method = methodEntry.getJavaMethod();

        jsonMethod.put("path", methodEntry.getPath());

        JSONArray pathParams = new JSONArray();
        JSONArray queryParams = new JSONArray();

        Arrays.stream(method.getParameters())
                .filter(parameter -> !MethodUtils.shouldExcludeFromDoc(parameter))
                .forEach(parameter -> {
                    if (parameter.getAnnotation(RestPath.class) != null) {
                        JSONObject json = new JSONObject();
                        json.put("name", parameter.getAnnotation(RestPath.class).value());
                        json.put("type", parameter.getType().getSimpleName());
                        pathParams.put(json);
                    } else if (parameter.getAnnotation(RestQuery.class) != null) {
                        JSONObject json = new JSONObject();
                        json.put("name", parameter.getAnnotation(RestQuery.class).value());
                        json.put("type", parameter.getType().getSimpleName());
                        queryParams.put(json);
                    }
                });

        jsonMethod.put("pathParams", pathParams);
        jsonMethod.put("queryParams", queryParams);

        jsonMethod.put("returns", getReturnType(method));

        jsonMethod.put("permission", PermissionUtils.getPermissionPath(method));

        return jsonMethod;
    }

    private JSONObject getV2RestOperationMethod(RestOperationMethod methodEntry) {
        JSONObject jsonMethod = new JSONObject();

        Method method = methodEntry.getJavaMethod();

        jsonMethod.put("path", methodEntry.getPath());

        JSONArray bodyParams = new JSONArray();

        Arrays.stream(method.getParameters())
                .filter(parameter -> !MethodUtils.shouldExcludeFromDoc(parameter))
                .forEach(parameter -> {
                    if (parameter.getAnnotation(RestBody.class) != null) {
                        JSONObject json = new JSONObject();
                        json.put("name", parameter.getAnnotation(RestBody.class).value());
                        json.put("type", parameter.getType().getSimpleName());
                        bodyParams.put(json);
                    }
                });

        jsonMethod.put("bodyParams", bodyParams);

        jsonMethod.put("returns", getReturnType(method));
        jsonMethod.put("operatesOn", methodEntry.getOperatesOn().getSimpleName());
        jsonMethod.put("method", methodEntry.getRestMethod().name());

        jsonMethod.put("permission", PermissionUtils.getPermissionPath(method));

        return jsonMethod;
    }

    private String getReturnType(Method method) {
        if (Collection.class.isAssignableFrom(method.getReturnType())) {
            ParameterizedType type = (ParameterizedType) method.getGenericReturnType();
            Class<?> realType = (Class<?>) type.getActualTypeArguments()[0];
            return realType.getSimpleName() + "[]";
        }
        return method.getReturnType().getSimpleName();
    }

    @Override
    public boolean hasPermission(CommandSource commandSource) {
        return commandSource.hasPermission("universalminecraftapi.command.createapidoc");
    }

    @Override
    public String getDescription() {
        return "Create an API documentation file (JSON format)";
    }

    @Override
    public String getUsage() {
        return "<filename>";
    }
}
