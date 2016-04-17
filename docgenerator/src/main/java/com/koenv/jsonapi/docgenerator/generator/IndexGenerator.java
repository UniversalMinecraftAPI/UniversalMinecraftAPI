package com.koenv.jsonapi.docgenerator.generator;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IndexGenerator extends AbstractGenerator {
    private List<String> namespaces;
    private List<String> classes;
    private List<String> streams;

    public IndexGenerator(File rootDirectory, List<String> namespaces, List<String> classes, List<String> streams) {
        super(rootDirectory);
        this.namespaces = namespaces;
        this.classes = classes;
        this.streams = streams;
    }

    @Override
    public void generate(Configuration configuration, Writer output) throws IOException, TemplateException {
        Template template = configuration.getTemplate("index.ftl");

        Map<String, Object> dataModel = new HashMap<>();

        namespaces.sort(String::compareTo);
        classes.sort(String::compareTo);
        streams.sort(String::compareTo);

        dataModel.put("namespaces", namespaces);
        dataModel.put("classes", classes);
        dataModel.put("streams", streams);

        template.process(dataModel, output);
    }
}
