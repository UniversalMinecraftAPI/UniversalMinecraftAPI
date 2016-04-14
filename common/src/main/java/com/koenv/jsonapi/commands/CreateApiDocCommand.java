package com.koenv.jsonapi.commands;

import com.koenv.jsonapi.ChatColor;
import com.koenv.jsonapi.JSONAPIInterface;
import com.koenv.jsonapi.methods.ClassMethod;
import com.koenv.jsonapi.methods.MethodInvoker;
import com.koenv.jsonapi.methods.NamespacedMethod;
import com.koenv.jsonapi.util.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.StringJoiner;
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

        Format format = Format.MARKDOWN;

        if (args.length > 1) {
            try {
                format = Format.valueOf(args[1].toUpperCase(Locale.ENGLISH));
            } catch (IllegalArgumentException e) {
                StringJoiner validFormats = new StringJoiner(", ");
                for (Format f : Format.values()) {
                    validFormats.add(f.name());
                }
                commandSource.sendMessage(ChatColor.RED, "Invalid format. Valid formats: " + validFormats.toString());
            }
        }

        MethodInvoker methodInvoker = jsonapi.getMethodInvoker();

        StringBuilder builder = new StringBuilder();
        switch (format) {
            case MARKDOWN:
                builder.append("# Namespaces\n\n");

                methodInvoker.getNamespaces().entrySet().stream().forEach(entry -> {
                    builder.append("## ").append(findFirst(entry.getValue()).getNamespace());
                    builder.append("\n\n");

                    addMethodsToMarkdown(builder, entry.getValue());
                });

                builder.append("# Objects\n\n");

                methodInvoker.getClasses().entrySet().stream().forEach(entry -> {
                    builder.append("## ").append(findFirst(entry.getValue()).getOperatesOn().getSimpleName());
                    builder.append("\n\n");

                    addMethodsToMarkdown(builder, entry.getValue());
                });

                System.out.println(builder.toString());
                break;
            case JSON:
                JSONObject root = new JSONObject();

                JSONObject namespaces = new JSONObject();

                methodInvoker.getNamespaces().entrySet().stream().forEach(entry -> {
                    namespaces.put(findFirst(entry.getValue()).getNamespace(), getJsonMethods(entry.getValue()));
                });

                root.put("namespaces", namespaces);

                JSONObject objects = new JSONObject();

                methodInvoker.getClasses().entrySet().stream().forEach(entry -> {
                    objects.put(findFirst(entry.getValue()).getOperatesOn().getSimpleName(), getJsonMethods(entry.getValue()));
                });

                root.put("objects", objects);

                builder.append(root.toString(4));

                break;
        }

        try (PrintWriter printWriter = new PrintWriter(file)) {
            printWriter.write(builder.toString());
        } catch (IOException e) {
            e.printStackTrace();
            commandSource.sendMessage(ChatColor.RED, "Failed to write to file: " + e.toString());
            return false;
        }

        commandSource.sendMessage(ChatColor.GREEN, "API documentation saved to file " + file.getPath());

        return true;
    }

    private void addMethodsToMarkdown(StringBuilder builder, Map<String, ? extends com.koenv.jsonapi.methods.Method> map) {
        for (Map.Entry<String, ? extends com.koenv.jsonapi.methods.Method> methodEntry : map.entrySet()) {
            builder.append("### ").append(methodEntry.getValue().getName());
            builder.append("\n\n");
            Method method = methodEntry.getValue().getJavaMethod();

            int parameterCount = method.getParameters().length;
            Stream<Parameter> stream = Arrays.stream(method.getParameters());
            if (methodEntry.getValue() instanceof ClassMethod) {
                stream = stream.skip(1);
                parameterCount--;
            }

            if (parameterCount > 0) {
                builder.append("Arguments:\n\n");
                stream.forEach(parameter -> builder.append("* ").append(parameter.getName()).append(": `").append(parameter.getType().getSimpleName()).append("`\n"));
            }

            builder.append("\n\n");
            builder.append("Return type: `");
            builder.append(method.getReturnType().getSimpleName());
            builder.append("`\n\n");

            builder.append("Definition:\n\n\t");

            if (methodEntry.getValue() instanceof ClassMethod) {
                builder.append(((ClassMethod) methodEntry.getValue()).getOperatesOn().getSimpleName()).append(".");
            } else if (methodEntry.getValue() instanceof NamespacedMethod) {
                builder.append(((NamespacedMethod) methodEntry.getValue()).getNamespace()).append(".");
            }

            builder.append(methodEntry.getValue().getName()).append("(");

            StringJoiner joiner = new StringJoiner(", ");

            stream = Arrays.stream(method.getParameters());
            if (methodEntry.getValue() instanceof ClassMethod) {
                stream = stream.skip(1);
            }

            stream.forEach(parameter -> joiner.add(parameter.getName() + ": " + parameter.getType().getSimpleName()));

            builder.append(joiner.toString());
            builder.append(")\n\n");
        }
    }

    private JSONObject getJsonMethods(Map<String, ? extends com.koenv.jsonapi.methods.Method> map) {
        JSONObject root = new JSONObject();
        for (Map.Entry<String, ? extends com.koenv.jsonapi.methods.Method> methodEntry : map.entrySet()) {
            JSONObject jsonMethod = new JSONObject();

            Method method = methodEntry.getValue().getJavaMethod();

            int parameterCount = method.getParameters().length;
            Stream<Parameter> stream = Arrays.stream(method.getParameters());
            if (methodEntry.getValue() instanceof ClassMethod) {
                stream = stream.skip(1);
                parameterCount--;
            }

            if (parameterCount > 0) {
                JSONObject arguments = new JSONObject();
                stream.forEach(parameter -> {
                    arguments.put(parameter.getName(), parameter.getType().getSimpleName());
                });
                jsonMethod.put("arguments", arguments);
            }

            jsonMethod.put("returns", method.getReturnType().getSimpleName());

            if (methodEntry.getValue() instanceof ClassMethod) {
                jsonMethod.put("operatesOn", ((ClassMethod) methodEntry.getValue()).getOperatesOn().getSimpleName());
            } else if (methodEntry.getValue() instanceof NamespacedMethod) {
                jsonMethod.put("namespace", ((NamespacedMethod) methodEntry.getValue()).getNamespace());
            }

            root.put(methodEntry.getValue().getName(), jsonMethod);
        }

        return root;
    }

    private <T extends com.koenv.jsonapi.methods.Method> T findFirst(Map<String, T> map) {
        return map.values().stream().findFirst().orElseThrow(NullPointerException::new);
    }

    @Override
    public boolean hasPermission(CommandSource commandSource) {
        return commandSource.hasPermission("jsonapi.command.createapidoc");
    }

    enum Format {
        MARKDOWN,
        JSON;
    }
}
