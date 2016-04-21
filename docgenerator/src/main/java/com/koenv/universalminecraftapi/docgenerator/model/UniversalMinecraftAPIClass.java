package com.koenv.universalminecraftapi.docgenerator.model;

public class UniversalMinecraftAPIClass {
    private String name;
    private boolean hasOwnDocumentation;
    private String documentationName;

    public UniversalMinecraftAPIClass(String name, boolean hasOwnDocumentation) {
        this(name, hasOwnDocumentation, name);
    }

    public UniversalMinecraftAPIClass(String name, boolean hasOwnDocumentation, String documentationName) {
        this.name = name;
        this.hasOwnDocumentation = hasOwnDocumentation;
        this.documentationName = documentationName;
    }

    public String getName() {
        return name;
    }

    public boolean hasOwnDocumentation() {
        return hasOwnDocumentation;
    }

    public boolean isHasOwnDocumentation() {
        return hasOwnDocumentation;
    }

    public String getDocumentationName() {
        return documentationName;
    }
}
