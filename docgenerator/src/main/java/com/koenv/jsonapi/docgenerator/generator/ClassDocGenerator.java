package com.koenv.jsonapi.docgenerator.generator;

import com.koenv.jsonapi.docgenerator.model.JSONAPIClass;
import com.koenv.jsonapi.docgenerator.resolvers.ClassResolver;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class ClassDocGenerator extends AbstractGenerator {
    protected String className;

    public ClassDocGenerator(File rootDirectory, String className) {
        super(rootDirectory);
        this.className = className;
    }

    @Override
    public void generate(Configuration configuration, ClassResolver classResolver, Writer output) throws IOException, TemplateException {
        Template template = configuration.getTemplate("class.ftl");

        Map<String, Object> dataModel = new HashMap<>();

        dataModel.put("class", className);

        File classIndexFile = new File(rootDirectory, className + ".conf");

        if (!classIndexFile.exists()) {
            classIndexFile = new File(rootDirectory, className + "/index.conf");
        }

        if (classIndexFile.exists()) {
            Config config = ConfigFactory.parseFile(classIndexFile);
            if (config.hasPath("description")) {
                dataModel.put("description", config.getString("description"));
            }

            if (config.hasPath("model")) {
                Map<String, ModelField> model = new HashMap<>();

                config.getObject("model").forEach((s, configValue) -> {
                    switch (configValue.valueType()) {
                        case STRING:
                            model.put(s, new ModelField(classResolver.resolve((String) configValue.unwrapped()), ""));
                            break;
                        case OBJECT:
                            Config object = config.getConfig("model").getConfig(s);
                            model.put(s, new ModelField(classResolver.resolve(object.getString("type")), object.getString("description")));
                            break;
                        default:
                            throw new IllegalArgumentException("The type of a model must be STRING, got " + configValue.valueType().name() + " at " + s);
                    }
                });
                dataModel.put("model", model);
            }
        }

        addToDataModel(classResolver, dataModel);

        template.process(dataModel, output);
    }

    protected void addToDataModel(ClassResolver classResolver, Map<String, Object> dataModel) {

    }

    public static class ModelField {
        private JSONAPIClass type;
        private String description;

        public ModelField(JSONAPIClass type, String description) {
            this.type = type;
            this.description = description;
        }

        public JSONAPIClass getType() {
            return type;
        }

        public String getDescription() {
            return description;
        }
    }
}
