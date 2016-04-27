package com.koenv.universalminecraftapi.docgenerator.generator;

import com.koenv.universalminecraftapi.docgenerator.ArgumentsUtil;
import com.koenv.universalminecraftapi.docgenerator.model.v1.NamespacedMethod;
import com.koenv.universalminecraftapi.docgenerator.model.Platform;
import com.koenv.universalminecraftapi.docgenerator.model.UniversalMinecraftAPIClass;
import com.koenv.universalminecraftapi.docgenerator.resolvers.ClassResolver;
import com.koenv.universalminecraftapi.docgenerator.resolvers.PlatformResolver;
import com.koenv.universalminecraftapi.util.json.JSONObject;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NamespaceDocGenerator extends AbstractGenerator {
    private String namespace;
    private List<NamespacedMethod> methods;
    private Logger logger;

    public NamespaceDocGenerator(File rootDirectory, String namespace, List<NamespacedMethod> methods) {
        super(rootDirectory);
        this.namespace = namespace;
        this.methods = methods;
        this.logger = LoggerFactory.getLogger(namespace);
    }

    @Override
    public void generate(Configuration configuration, ClassResolver classResolver, PlatformResolver platformResolver, Writer output) throws IOException, TemplateException {
        Template template = configuration.getTemplate("namespace.ftl");

        Map<String, Object> dataModel = new HashMap<>();

        dataModel.put("namespace", namespace);

        File namespaceIndexFile = new File(rootDirectory, namespace + "/index.conf");

        if (namespaceIndexFile.exists()) {
            Config config = ConfigFactory.parseFile(namespaceIndexFile);
            if (config.hasPath("description")) {
                dataModel.put("description", config.getString("description"));
            }
        } else {
            logger.warn("No configuration file found at " + namespaceIndexFile.getPath());
        }

        dataModel.put("methods", methods.stream().sorted((o1, o2) -> o1.getName().compareTo(o2.getName())).map(method -> {
            String description = "";
            Map<String, String> argumentDescriptions = new HashMap<>();
            String returnDescription = "";
            String example = "";
            UniversalMinecraftAPIClass returnType = classResolver.resolve(method.getReturns());
            List<ArgumentWrapper> arguments = method.getArguments().stream()
                    .map(argument -> new ArgumentWrapper(argument.getName(), classResolver.resolve(argument.getType()), argument.isOptional()))
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
                        case OBJECT:
                            Map<String, Object> exampleArguments = new HashMap<>();
                            config.getConfig("example").root().entrySet().forEach(entry -> exampleArguments.put(entry.getKey(), entry.getValue().unwrapped()));
                            if (exampleArguments.size() != method.getArguments().size()) {
                                throw new IllegalArgumentException(
                                        "Invalid number of parameters for example for method " +
                                                method.getDeclaration() + ". Expected " +
                                                method.getArguments().size() + ", received " +
                                                exampleArguments.size()
                                );
                            }

                            example = ArgumentsUtil.getExample(method, exampleArguments);
                            break;
                        default:
                            throw new IllegalArgumentException("Invalid example format for " + method.getDeclaration()
                                    + ": must be a string, object or list, given " + config.getValue("example").valueType().name());
                    }
                }
            } else {
                logger.warn("No configuration file found for " + method.getDeclaration() + " at " + methodFile.getPath());
            }

            if (example.isEmpty()) {
                if (method.getArguments().size() == 0) {
                    example = ArgumentsUtil.getExample(method, new HashMap<>());
                }
            }

            List<Platform> platforms = new ArrayList<>();

            if (!platformResolver.availableOnAllPlatforms(method)) {
                platforms.addAll(platformResolver.getPlatforms(method));
            }

            return new MethodWrapper(
                    method, arguments, returnType, description, argumentDescriptions, returnDescription, example,
                    JSONObject.quote(example), platforms.size() == 0, platforms
            );
        }).collect(Collectors.toList()));

        template.process(dataModel, output);
    }

    @SuppressWarnings("unused")
    public static class MethodWrapper {
        private NamespacedMethod method;
        private List<ArgumentWrapper> arguments;
        private UniversalMinecraftAPIClass returns;
        private String description;
        private Map<String, String> argumentDescriptions;
        private String returnDescription;
        private String example;
        private String jsonExample;
        private boolean availableOnAllPlatforms;
        private List<Platform> platforms;

        public MethodWrapper(
                NamespacedMethod method, List<ArgumentWrapper> arguments, UniversalMinecraftAPIClass returns,
                String description, Map<String, String> argumentDescriptions, String returnDescription, String example,
                String jsonExample, boolean availableOnAllPlatforms, List<Platform> platforms
        ) {
            this.method = method;
            this.arguments = arguments;
            this.returns = returns;
            this.description = description;
            this.argumentDescriptions = argumentDescriptions;
            this.returnDescription = returnDescription;
            this.example = example;
            this.jsonExample = jsonExample;
            this.availableOnAllPlatforms = availableOnAllPlatforms;
            this.platforms = platforms;
        }

        public List<ArgumentWrapper> getArguments() {
            return arguments;
        }

        public UniversalMinecraftAPIClass getReturns() {
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

        public String getJsonExample() {
            return jsonExample;
        }

        public boolean isAvailableOnAllPlatforms() {
            return availableOnAllPlatforms;
        }

        public List<Platform> getPlatforms() {
            return platforms;
        }

        public String getName() {
            return method.getName();
        }

        public String getPermission() {
            return method.getPermission();
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
        private UniversalMinecraftAPIClass type;
        private boolean optional;

        public ArgumentWrapper(String name, UniversalMinecraftAPIClass type, boolean optional) {
            this.name = name;
            this.type = type;
            this.optional = optional;
        }

        public String getName() {
            return name;
        }

        public UniversalMinecraftAPIClass getType() {
            return type;
        }

        public boolean isOptional() {
            return optional;
        }
    }
}
