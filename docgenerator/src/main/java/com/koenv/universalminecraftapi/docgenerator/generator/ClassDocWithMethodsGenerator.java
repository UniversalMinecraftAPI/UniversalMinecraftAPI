package com.koenv.universalminecraftapi.docgenerator.generator;

import com.koenv.universalminecraftapi.docgenerator.model.ClassMethod;
import com.koenv.universalminecraftapi.docgenerator.model.UniversalMinecraftAPIClass;
import com.koenv.universalminecraftapi.docgenerator.resolvers.ClassResolver;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ClassDocWithMethodsGenerator extends ClassDocGenerator {
    private List<ClassMethod> methods;

    public ClassDocWithMethodsGenerator(File rootDirectory, String className, List<ClassMethod> methods) {
        super(rootDirectory, className);
        this.methods = methods;
    }

    @Override
    protected void addToDataModel(ClassResolver classResolver, Map<String, Object> dataModel) {
        dataModel.put("methods", methods.stream().sorted((o1, o2) -> o1.getName().compareTo(o2.getName())).map(method -> {
            String description = "";
            Map<String, String> argumentDescriptions = new HashMap<>();
            String returnDescription = "";
            UniversalMinecraftAPIClass returnType = classResolver.resolve(method.getReturns());
            List<ArgumentWrapper> arguments = method.getArguments().stream()
                    .map(argument -> new ArgumentWrapper(argument.getName(), classResolver.resolve(argument.getType())))
                    .collect(Collectors.toList());

            File methodFile = new File(rootDirectory, className + "/" + method.getName() + ".conf");

            if (methodFile.exists()) {
                Config config = ConfigFactory.parseFile(methodFile);
                if (config.hasPath("description")) {
                    description = config.getString("description");
                }
                GeneratorUtils.getArgumentDescriptions(argumentDescriptions, config, method);
                if (config.hasPath("returns")) {
                    returnDescription = config.getString("returns");
                }
            } else {
                logger.warn("No configuration file found for " + method.getDeclaration() + " at " + methodFile.getPath());
            }

            return new MethodWrapper(method, arguments, returnType, description, argumentDescriptions, returnDescription);
        }).collect(Collectors.toList()));
    }

    @SuppressWarnings("unused")
    public static class MethodWrapper {
        private ClassMethod method;
        private List<ArgumentWrapper> arguments;
        private UniversalMinecraftAPIClass returns;
        private String description;
        private Map<String, String> argumentDescriptions;
        private String returnDescription;

        public MethodWrapper(ClassMethod method, List<ArgumentWrapper> arguments, UniversalMinecraftAPIClass returns, String description, Map<String, String> argumentDescriptions, String returnDescription) {
            this.method = method;
            this.arguments = arguments;
            this.returns = returns;
            this.description = description;
            this.argumentDescriptions = argumentDescriptions;
            this.returnDescription = returnDescription;
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

        public String getName() {
            return method.getName();
        }

        public String getDeclaration() {
            return method.getDeclaration();
        }

        public String getDeclarationWithoutOperatesOn() {
            return method.getDeclarationWithoutOperatesOn();
        }
    }

    @SuppressWarnings("unused")
    public static class ArgumentWrapper {
        private String name;
        private UniversalMinecraftAPIClass type;

        public ArgumentWrapper(String name, UniversalMinecraftAPIClass type) {
            this.name = name;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public UniversalMinecraftAPIClass getType() {
            return type;
        }
    }
}
