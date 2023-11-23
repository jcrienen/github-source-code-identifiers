package com.juulcrienen.githubsourcecodeidentifiers.extractors;

public enum ExtractionType {
    GLOBAL_VARIABLES("(.*)field_declaration.variable_declarator|(.*)field_declaration.variable_declaration.variable_declarator"),
    LOCAL_VARIABLES("(.*)local_variable_declaration.variable_declarator|(.*)local_declaration_statement.variable_declaration.variable_declarator"),
    PARAMETERS("(.*)formal_parameter|(.*)parameter"),
    METHOD_NAMES("(.*)method_declaration"),
    CLASS_NAMES("(.*)class_declaration|(.*)interface_declaration");

    private final String regex;

    ExtractionType(String regex) {
        this.regex = regex;
    }

    public String getRegex() {
        return regex;
    }
}