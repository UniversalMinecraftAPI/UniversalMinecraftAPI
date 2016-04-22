package com.koenv.universalminecraftapi.commands;

import com.koenv.universalminecraftapi.ChatColor;
import com.koenv.universalminecraftapi.UniversalMinecraftAPIInterface;
import com.koenv.universalminecraftapi.methods.*;
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
            commandSource.sendMessage(ChatColor.RED, "Missing argument: file name");
            return;
        }

        File file = new File(args[0]);
        if (file.exists()) {
            commandSource.sendMessage(ChatColor.RED, "File already exists, aborting");
            return;
        }

        MethodInvoker methodInvoker = uma.getMethodInvoker();

        JSONObject root = new JSONObject();

        JSONArray namespaces = new JSONArray();

        methodInvoker.getNamespaces().values().stream().flatMap(map -> map.values().stream()).forEach(method -> namespaces.put(getJsonMethod(method)));

        root.put("namespaces", namespaces);

        JSONArray classes = new JSONArray();

        methodInvoker.getClasses().values().stream().flatMap(map -> map.values().stream()).forEach(method -> classes.put(getJsonMethod(method)));

        root.put("classes", classes);

        JSONArray streams = new JSONArray();
        uma.getStreamManager().getStreams().stream().forEach(streams::put);

        root.put("streams", streams);

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

        return;
    }

    private JSONObject getJsonMethod(AbstractMethod methodEntry) {
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
                        json.put("optional", parameter.getAnnotation(Optional.class) != null);
                        arguments.put(json);
                    });
            jsonMethod.put("arguments", arguments);
        } else {
            jsonMethod.put("arguments", new JSONArray());
        }

        if (Collection.class.isAssignableFrom(method.getReturnType())) {
            ParameterizedType type = (ParameterizedType) method.getGenericReturnType();
            Class<?> realType = (Class<?>) type.getActualTypeArguments()[0];
            jsonMethod.put("returns", realType.getSimpleName() + "[]");
        } else {
            jsonMethod.put("returns", method.getReturnType().getSimpleName());
        }

        if (methodEntry instanceof ClassMethod) {
            jsonMethod.put("operatesOn", ((ClassMethod) methodEntry).getOperatesOn().getSimpleName());
        } else if (methodEntry instanceof NamespacedMethod) {
            jsonMethod.put("namespace", ((NamespacedMethod) methodEntry).getNamespace());
        }

        return jsonMethod;
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
