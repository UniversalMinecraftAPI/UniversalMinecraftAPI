package com.koenv.universalminecraftapi.docgenerator.model;

import java.io.File;

public class PlatformDefinition {
    private String name;
    private File file;

    public PlatformDefinition(String name, File file) {
        this.name = name;
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public File getFile() {
        return file;
    }
}
