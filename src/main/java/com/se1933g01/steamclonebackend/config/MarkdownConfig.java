package com.se1933g01.steamclonebackend.config;

import java.util.Arrays;
import com.vladsch.flexmark.html.HtmlRenderer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.util.data.MutableDataSet;

@Configuration
public class MarkdownConfig {

@Bean
    public HtmlRenderer markdownRenderer() {
        MutableDataSet options = new MutableDataSet();
        options.set(Parser.EXTENSIONS, Arrays.asList(TablesExtension.create()));
        return HtmlRenderer.builder(options).build();
    }

    @Bean
    public Parser markdownParser() {
        MutableDataSet options = new MutableDataSet();
        options.set(Parser.EXTENSIONS, Arrays.asList(TablesExtension.create()));
        return Parser.builder(options).build();
    }

}

