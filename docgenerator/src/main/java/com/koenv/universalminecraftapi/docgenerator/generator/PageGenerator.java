package com.koenv.universalminecraftapi.docgenerator.generator;

import com.koenv.universalminecraftapi.docgenerator.model.Page;
import com.koenv.universalminecraftapi.docgenerator.resolvers.ClassResolver;
import com.koenv.universalminecraftapi.docgenerator.resolvers.PlatformResolver;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TableBlock;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.html.HtmlRenderer;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;

import java.io.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PageGenerator extends AbstractGenerator {
    private Page page;

    public PageGenerator(File rootDirectory, Page page) {
        super(rootDirectory);
        this.page = page;
    }

    @Override
    public void generate(Configuration configuration, ClassResolver classResolver, PlatformResolver platformResolver, Writer output) throws IOException, TemplateException {
        Template template = configuration.getTemplate("page.ftl");

        Map<String, Object> dataModel = new HashMap<>();

        dataModel.put("page", page);

        dataModel.put("contents", generateContents(page));

        template.process(dataModel, output);
    }

    public String generateContents(Page page) throws IOException {
        List<Extension> extensions = Collections.singletonList(TablesExtension.create());

        Parser parser = Parser
                .builder()
                .extensions(extensions)
                .build();

        HtmlRenderer renderer = HtmlRenderer
                .builder()
                .attributeProvider((node, attributes) -> {
                    if (node instanceof TableBlock) {
                        attributes.put("class", "table");
                    }
                })
                .extensions(extensions)
                .build();

        Node document = parser.parseReader(new FileReader(page.getFile()));

        return renderer.render(document);
    }
}
