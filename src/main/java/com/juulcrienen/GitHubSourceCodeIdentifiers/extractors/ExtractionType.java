package com.juulcrienen.GitHubSourceCodeIdentifiers.extractors;

public enum ExtractionType {
    GLOBAL_VARIABLES("(.*)field_declaration.variable_declarator"),
    LOCAL_VARIABLES("(.*)local_variable_declaration.variable_declarator"),
    PARAMETERS("(.*)formal_parameter"),
    METHOD_NAMES("(.*)method_declaration"),
    CLASS_NAMES("(.*)class_declaration");

    private final String regex;

    ExtractionType(String regex) {
        this.regex = regex;
    }

    public String getRegex() {
        return regex;
    }
}