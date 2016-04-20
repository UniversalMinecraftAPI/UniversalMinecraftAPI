package com.koenv.jsonapi.docgenerator.generator;

import com.koenv.jsonapi.docgenerator.model.Page;
import com.koenv.jsonapi.docgenerator.resolvers.ClassResolver;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.commonmark.html.HtmlRenderer;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class PageGenerator extends AbstractGenerator {
    private Page page;

    public PageGenerator(File rootDirectory, Page page) {
        super(rootDirectory);
        this.page = page;
    }

    @Override
    public void generate(Configuration configuration, ClassResolver classResolver, Writer output) throws IOException, TemplateException {
        Template template = configuration.getTemplate("page.ftl");

        Map<String, Object> dataModel = new HashMap<>();

        dataModel.put("page", page);

        Parser parser = Parser.builder().build();
        Node document = parser.parseReader(new FileReader(page.getFile()));
        HtmlRenderer renderer = HtmlRenderer.builder().build();

        dataModel.put("contents", renderer.render(document));

        template.process(dataModel, output);
    }
}
