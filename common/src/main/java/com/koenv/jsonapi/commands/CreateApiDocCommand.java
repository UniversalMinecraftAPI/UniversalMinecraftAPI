package com.koenv.jsonapi.commands;

import com.koenv.jsonapi.ChatColor;
import com.koenv.jsonapi.JSONAPIInterface;
import com.koenv.jsonapi.methods.*;
import com.koenv.jsonapi.util.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class CreateApiDocCommand extends Command {
    @Override
    public boolean onCommand(JSONAPIInterface jsonapi, CommandSource commandSource, String[] args) {
        if (args.length < 1) {
            commandSource.sendMessage(ChatColor.RED, "Missing argument: file name");
            return false;
        }

        File file = new File(args[0]);
        if (file.exists()) {
            commandSource.sendMessage(ChatColor.RED, "File already exists, aborting");
            return false;
        }

        MethodInvoker methodInvoker = jsonapi.getMethodInvoker();

        JSONObject root = new JSONObject();

        JSONObject namespaces = new JSONObject();

        methodInvoker.getNamespaces().entrySet().stream().forEach(entry -> namespaces.put(findFirst(entry.getValue()).getNamespace(), getJsonMethods(entry.getValue())));

        root.put("namespaces", namespaces);

        JSONObject objects = new JSONObject();

        methodInvoker.getClasses().entrySet().stream().forEach(entry -> objects.put(findFirst(entry.getValue()).getOperatesOn().getSimpleName(), getJsonMethods(entry.getValue())));

        root.put("objects", objects);

        try (PrintWriter printWriter = new PrintWriter(file)) {
            printWriter.write(root.toString(4));
        } catch (IOException e) {
            e.printStackTrace();
            commandSource.sendMessage(ChatColor.RED, "Failed to write to file: " + e.toString());
            return false;
        }

        commandSource.sendMessage(ChatColor.GREEN, "API documentation saved to file " + file.getPath());

        return true;
    }

    private JSONObject getJsonMethods(Map<String, ? extends AbstractMethod> map) {
        JSONObject root = new JSONObject();
        for (Map.Entry<String, ? extends AbstractMethod> methodEntry : map.entrySet()) {
            JSONObject jsonMethod = new JSONObject();

            Method method = methodEntry.getValue().getJavaMethod();

            APIMethod annotation = method.getAnnotation(APIMethod.class);

            if (annotation != null) {
                jsonMethod.put("description", annotation.description());
            }

            int parameterCount = method.getParameters().length;
            Stream<Parameter> stream = Arrays.stream(method.getParameters());
            if (methodEntry.getValue() instanceof ClassMethod) {
                stream = stream.skip(1);
                parameterCount--;
            }

            if (parameterCount > 0) {
                JSONObject arguments = new JSONObject();
                AtomicInteger index = new AtomicInteger();
                stream
                        .filter(parameter -> !MethodUtils.shouldExcludeFromDoc(parameter))
                        .forEach(parameter -> {
                            JSONObject json = new JSONObject();
                            json.put("type", parameter.getType().getSimpleName());
                            if (annotation != null) {
                                if (annotation.argumentDescriptions().length > index.get()) {
                                    json.put("description", annotation.argumentDescriptions()[index.getAndIncrement()]);
                                }
                            }
                            arguments.put(parameter.getName(), json);
                        });
                jsonMethod.put("arguments", arguments);
            }

            JSONObject returns = new JSONObject();
            returns.put("type", method.getReturnType().getSimpleName());
            if (annotation != null) {
                returns.put("description", annotation.returnDescription());
            }

            jsonMethod.put("returns", returns);

            if (methodEntry.getValue() instanceof ClassMethod) {
                jsonMethod.put("operatesOn", ((ClassMethod) methodEntry.getValue()).getOperatesOn().getSimpleName());
            } else if (methodEntry.getValue() instanceof NamespacedMethod) {
                jsonMethod.put("namespace", ((NamespacedMethod) methodEntry.getValue()).getNamespace());
            }

            root.put(methodEntry.getValue().getName(), jsonMethod);
        }

        return root;
    }

    private <T extends AbstractMethod> T findFirst(Map<String, T> map) {
        return map.values().stream().findFirst().orElseThrow(NullPointerException::new);
    }

    @Override
    public boolean hasPermission(CommandSource commandSource) {
        return commandSource.hasPermission("jsonapi.command.createapidoc");
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
