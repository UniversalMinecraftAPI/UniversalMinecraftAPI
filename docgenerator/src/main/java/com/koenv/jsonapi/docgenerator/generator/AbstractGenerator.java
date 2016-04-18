package com.koenv.jsonapi.docgenerator.generator;

import com.koenv.jsonapi.docgenerator.resolvers.ClassResolver;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

public abstract class AbstractGenerator {
    protected File rootDirectory;

    public AbstractGenerator(File rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    public abstract void generate(Configuration configuration, ClassResolver classResolver, Writer output) throws IOException, TemplateException;
}
