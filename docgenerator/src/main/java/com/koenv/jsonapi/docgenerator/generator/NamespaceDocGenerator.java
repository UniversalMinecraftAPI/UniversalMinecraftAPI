package com.koenv.jsonapi.docgenerator.generator;

import com.koenv.jsonapi.docgenerator.model.JSONAPIClass;
import com.koenv.jsonapi.docgenerator.model.NamespacedMethod;
import com.koenv.jsonapi.docgenerator.resolvers.ClassResolver;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigValueType;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class NamespaceDocGenerator extends AbstractGenerator {
    private String namespace;
    private List<NamespacedMethod> methods;

    public NamespaceDocGenerator(File rootDirectory, String namespace, List<NamespacedMethod> methods) {
        super(rootDirectory);
        this.namespace = namespace;
        this.methods = methods;
    }

    @Override
    public void generate(Configuration configuration, ClassResolver classResolver, Writer output) throws IOException, TemplateException {
        Template template = configuration.getTemplate("namespace.ftl");

        Map<String, Object> dataModel = new HashMap<>();

        dataModel.put("namespace", namespace);

        File namespaceIndexFile = new File(rootDirectory, namespace + "/index.conf");

        if (namespaceIndexFile.exists()) {
            Config config = ConfigFactory.parseFile(namespaceIndexFile);
            if (config.hasPath("description")) {
                dataModel.put("description", config.getString("description"));
            }
        }

        dataModel.put("methods", methods.stream().sorted((o1, o2) -> o1.getName().compareTo(o2.getName())).map(method -> {
            String description = "";
            Map<String, String> argumentDescriptions = new HashMap<>();
            String returnDescription = "";
            String example = "";
            JSONAPIClass returnType = classResolver.resolve(method.getReturns());
            List<ArgumentWrapper> arguments = method.getArguments().stream()
                    .map(argument -> new ArgumentWrapper(argument.getName(), classResolver.resolve(argument.getType())))
                    .collect(Collectors.toList());

            File methodFile = new File(rootDirectory, namespace + "/" + method.getName() + ".conf");

            if (methodFile.exists()) {
                Config config = ConfigFactory.parseFile(methodFile);
                if (config.hasPath("description")) {
                    description = config.getString("description");
                }
                GeneratorUtils.getArgumentDescriptions(argumentDescriptions, config, method);
                if (config.hasPath("returns")) {
                    returnDescription = config.getString("returns");
                }
                if (config.hasPath("example")) {
                    switch (config.getValue("example").valueType()) {
                        case STRING:
                            example = config.getString("example");
                            break;
                        case LIST:
                            List<ConfigValue> values = config.getList("example");
                            if (values.size() != method.getArguments().size()) {
                                throw new IllegalArgumentException(
                                        "Invalid number of parameters for example for method " +
                                                method.getDeclaration() + ". Expected " +
                                                method.getArguments().size() + ", received " +
                                                values.size()
                                );
                            }
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append(method.getNamespace());
                            stringBuilder.append(".");
                            stringBuilder.append(method.getName());
                            stringBuilder.append("(");
                            StringJoiner joiner = new StringJoiner(", ");

                            AtomicInteger i = new AtomicInteger();
                            method.getArguments()
                                    .stream()
                                    .map(argument -> {
                                        ConfigValue a = values.get(i.getAndIncrement());
                                        if (a == null) {
                                            throw new IllegalArgumentException("Missing value for example argument " + argument.getName() + " for method " + method.getDeclaration());
                                        }
                                        if (a.valueType() == ConfigValueType.STRING) {
                                            return "'" + a.unwrapped() + "'";
                                        }
                                        return a.unwrapped().toString();
                                    })
                                    .forEach(joiner::add);

                            stringBuilder.append(joiner.toString());
                            stringBuilder.append(")");
                            example = stringBuilder.toString();
                            break;
                        case OBJECT:
                            Map<String, Object> exampleArguments = new HashMap<>();
                            config.getConfig("example").entrySet().forEach(entry -> exampleArguments.put(entry.getKey(), entry.getValue().unwrapped()));
                            if (exampleArguments.size() != method.getArguments().size()) {
                                throw new IllegalArgumentException(
                                        "Invalid number of parameters for example for method " +
                                                method.getDeclaration() + ". Expected " +
                                                method.getArguments().size() + ", received " +
                                                exampleArguments.size()
                                );
                            }
                            StringBuilder exampleBuilder = new StringBuilder();
                            exampleBuilder.append(method.getNamespace());
                            exampleBuilder.append(".");
                            exampleBuilder.append(method.getName());
                            exampleBuilder.append("(");
                            StringJoiner exampleJoiner = new StringJoiner(", ");

                            method.getArguments()
                                    .stream()
                                    .map(argument -> {
                                        Object a = exampleArguments.get(argument.getName());
                                        if (a == null) {
                                            throw new IllegalArgumentException("Missing value for example argument " + argument.getName() + " for method " + method.getDeclaration());
                                        }
                                        if (a instanceof String) {
                                            return "'" + a + "'";
                                        }
                                        return a.toString();
                                    })
                                    .forEach(exampleJoiner::add);

                            exampleBuilder.append(exampleJoiner.toString());
                            exampleBuilder.append(")");
                            example = exampleBuilder.toString();
                            break;
                        default:
                            throw new IllegalArgumentException("Invalid example format for " + method.getDeclaration()
                                    + ": must be a string, object or list, given " + config.getValue("example").valueType().name());
                    }
                }
            }

            if (example.isEmpty()) {
                if (method.getArguments().size() == 0) {
                    example = method.getDeclaration();
                }
            }

            return new MethodWrapper(method, arguments, returnType, description, argumentDescriptions, returnDescription, example);
        }).collect(Collectors.toList()));

        template.process(dataModel, output);
    }

    @SuppressWarnings("unused")
    public static class MethodWrapper {
        private NamespacedMethod method;
        private List<ArgumentWrapper> arguments;
        private JSONAPIClass returns;
        private String description;
        private Map<String, String> argumentDescriptions;
        private String returnDescription;
        private String example;

        public MethodWrapper(NamespacedMethod method, List<ArgumentWrapper> arguments, JSONAPIClass returns, String description, Map<String, String> argumentDescriptions, String returnDescription, String example) {
            this.method = method;
            this.arguments = arguments;
            this.returns = returns;
            this.description = description;
            this.argumentDescriptions = argumentDescriptions;
            this.returnDescription = returnDescription;
            this.example = example;
        }

        public List<ArgumentWrapper> getArguments() {
            return arguments;
        }

        public JSONAPIClass getReturns() {
            return returns;
        }

        public String getDescription() {
            return description;
        }

        public Map<String, String> getArgumentDescriptions() {
            return argumentDescriptions;
        }

        public String getReturnDescription() {
            return returnDescription;
        }

        public String getExample() {
            return example;
        }

        public String getName() {
            return method.getName();
        }

        public String getDeclaration() {
            return method.getDeclaration();
        }

        public String getDeclarationWithoutNamespace() {
            return method.getDeclarationWithoutNamespace();
        }
    }

    @SuppressWarnings("unused")
    public static class ArgumentWrapper {
        private String name;
        private JSONAPIClass type;

        public ArgumentWrapper(String name, JSONAPIClass type) {
            this.name = name;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public JSONAPIClass getType() {
            return type;
        }
    }
}
