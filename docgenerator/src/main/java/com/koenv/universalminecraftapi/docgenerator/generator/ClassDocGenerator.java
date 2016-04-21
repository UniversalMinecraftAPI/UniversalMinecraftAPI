package com.koenv.universalminecraftapi.docgenerator.generator;

import com.koenv.universalminecraftapi.docgenerator.model.UniversalMinecraftAPIClass;
import com.koenv.universalminecraftapi.docgenerator.resolvers.ClassResolver;
import com.koenv.universalminecraftapi.docgenerator.resolvers.PlatformResolver;
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
import java.util.HashMap;
import java.util.Map;

public class ClassDocGenerator extends AbstractGenerator {
    protected String className;
    protected Logger logger;

    public ClassDocGenerator(File rootDirectory, String className) {
        super(rootDirectory);
        this.className = className;
        this.logger = LoggerFactory.getLogger(className);
    }

    @Override
    public void generate(Configuration configuration, ClassResolver classResolver, PlatformResolver platformResolver, Writer output) throws IOException, TemplateException {
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
        } else {
            logger.warn("No configuration file found at " + classIndexFile.getPath());
        }

        addToDataModel(classResolver, platformResolver, dataModel);

        template.process(dataModel, output);
    }

    protected void addToDataModel(ClassResolver classResolver, PlatformResolver platformResolver, Map<String, Object> dataModel) {

    }

    public static class ModelField {
        private UniversalMinecraftAPIClass type;
        private String description;

        public ModelField(UniversalMinecraftAPIClass type, String description) {
            this.type = type;
            this.description = description;
        }

        public UniversalMinecraftAPIClass getType() {
            return type;
        }

        public String getDescription() {
            return description;
        }
    }
}
