package com.koenv.universalminecraftapi.docgenerator.generator;

import com.google.common.io.Files;
import com.koenv.universalminecraftapi.docgenerator.model.Page;
import com.koenv.universalminecraftapi.docgenerator.resolvers.ClassResolver;
import com.koenv.universalminecraftapi.docgenerator.resolvers.PlatformResolver;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class IndexGenerator extends AbstractGenerator {
    private List<Page> pages;
    private List<String> namespaces;
    private List<String> classes;
    private List<String> streams;

    public IndexGenerator(File rootDirectory, List<Page> pages, List<String> namespaces, List<String> classes, List<String> streams) {
        super(rootDirectory);
        this.pages = pages;
        this.namespaces = namespaces;
        this.classes = classes;
        this.streams = streams;
    }

    @Override
    public void generate(Configuration configuration, ClassResolver classResolver, PlatformResolver platformResolver, Writer output) throws IOException, TemplateException {
        Template template = configuration.getTemplate("index.ftl");

        Map<String, Object> dataModel = new HashMap<>();

        List<PageWrapper> pages = this.pages.stream()
                .map(page -> new PageWrapper(page.getTitle(), Files.getNameWithoutExtension(page.getFile().getPath()) + ".html"))
                .collect(Collectors.toList());

        List<String> classes = new ArrayList<>(this.classes.stream().map(s -> {
            if (classResolver.resolve(s) != null) {
                return classResolver.resolve(s).getName();
            }
            return s;
        }).collect(Collectors.toSet()));

        namespaces.sort(String::compareTo);
        classes.sort(String::compareTo);
        streams.sort(String::compareTo);

        dataModel.put("pages", pages);
        dataModel.put("namespaces", namespaces);
        dataModel.put("classes", classes);
        dataModel.put("streams", streams);

        template.process(dataModel, output);
    }

    public static class PageWrapper {
        private String title;
        private String link;

        public PageWrapper(String title, String link) {
            this.title = title;
            this.link = link;
        }

        public String getTitle() {
            return title;
        }

        public String getLink() {
            return link;
        }
    }
}
