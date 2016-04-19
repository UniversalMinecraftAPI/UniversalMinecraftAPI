package com.koenv.jsonapi.docgenerator.generator;

import com.koenv.jsonapi.docgenerator.resolvers.ClassResolver;
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

public class StreamDocGenerator extends AbstractGenerator {
    private String streamName;
    private Logger logger;

    public StreamDocGenerator(File rootDirectory, String streamName) {
        super(rootDirectory);
        this.streamName = streamName;
        this.logger = LoggerFactory.getLogger(streamName);
    }

    @Override
    public void generate(Configuration configuration, ClassResolver classResolver, Writer output) throws IOException, TemplateException {
        Template template = configuration.getTemplate("stream.ftl");

        Map<String, Object> dataModel = new HashMap<>();

        dataModel.put("stream", streamName);

        File streamFile = new File(rootDirectory, streamName + ".conf");

        if (streamFile.exists()) {
            Config config = ConfigFactory.parseFile(streamFile);
            if (config.hasPath("description")) {
                dataModel.put("description", config.getString("description"));
            }
            if (config.hasPath("returns")) {
                dataModel.put("returns", classResolver.resolve(config.getString("returns")));
            }
        } else {
            logger.warn("No configuration file found for stream " + streamName + " at " + streamFile.getPath());
        }

        template.process(dataModel, output);
    }
}
