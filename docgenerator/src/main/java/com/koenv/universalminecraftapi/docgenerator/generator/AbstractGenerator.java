package com.koenv.universalminecraftapi.docgenerator.generator;

import com.koenv.universalminecraftapi.docgenerator.resolvers.ClassResolver;
import com.koenv.universalminecraftapi.docgenerator.resolvers.PlatformResolver;
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

    public abstract void generate(Configuration configuration, ClassResolver classResolver, PlatformResolver platformResolver, Writer output) throws IOException, TemplateException;
}
