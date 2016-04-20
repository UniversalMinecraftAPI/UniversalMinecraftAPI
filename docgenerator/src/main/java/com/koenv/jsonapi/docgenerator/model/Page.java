package com.koenv.jsonapi.docgenerator.model;

import java.io.File;

public class Page {
    private String title;
    private File file;

    public Page(String title, File file) {
        this.title = title;
        this.file = file;
    }

    public String getTitle() {
        return title;
    }

    public File getFile() {
        return file;
    }
}
