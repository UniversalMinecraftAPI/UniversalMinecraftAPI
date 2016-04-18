package com.koenv.jsonapi.docgenerator.model;

public class JSONAPIClass {
    private String name;
    private boolean hasOwnDocumentation;
    private String documentationName;

    public JSONAPIClass(String name, boolean hasOwnDocumentation) {
        this(name, hasOwnDocumentation, name);
    }

    public JSONAPIClass(String name, boolean hasOwnDocumentation, String documentationName) {
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
