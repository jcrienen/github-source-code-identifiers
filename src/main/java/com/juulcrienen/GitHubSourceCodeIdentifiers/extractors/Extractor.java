package com.juulcrienen.GitHubSourceCodeIdentifiers.extractors;

import ai.serenade.treesitter.Parser;
import ai.serenade.treesitter.TreeCursor;
import ai.serenade.treesitter.TreeCursorNode;
import com.juulcrienen.GitHubSourceCodeIdentifiers.models.ClassFile;

import java.util.ArrayList;
import java.util.List;

public abstract class Extractor<T extends ClassFile> {

    public enum ExtractionType {
        GLOBAL_VARIABLES,
        LOCAL_VARIABLES,
        PARAMETERS,
        METHOD_NAMES,
        CLASS_NAMES
    }

    private final Parser fileParser;
    private final long language;
    public Extractor(long language) {
        this.fileParser = new Parser();
        this.language = language;

        this.fileParser.setLanguage(language);
    }

    public Parser getFileParser() {
        return fileParser;
    }

    public long getLanguage() {
        return language;
    }

    public void extractIdentifiers(T file) {

    }

    public void extractVariables(T file) {

    }

    public void extractMethodNames(T file) {

    }

    public void extractParameters(T file) {

    }
    public List<String> treeTraversal(TreeCursor cursor, String input, ExtractionType type) {
        List<String> result = new ArrayList<>();
        treeTraversalRecursive(cursor, input, "", type, result);
        return result;
    }

    private void treeTraversalRecursive(TreeCursor cursor, String input, String typeTrail, ExtractionType type, List<String> collector) {
        TreeCursorNode current = cursor.getCurrentTreeCursorNode();
        String currentType = current.getType();

        if(currentType.equals("identifier") && typeTrail != null) {
            int start = current.getStartByte();
            int end = current.getEndByte();

            switch (type) {
                case GLOBAL_VARIABLES:
                    if(typeTrail.matches("(.*)field_declaration.variable_declarator")) collector.add(input.substring(start, end));
                    break;
                case LOCAL_VARIABLES:
                    if(typeTrail.matches("(.*)local_variable_declaration.variable_declarator")) collector.add(input.substring(start, end));
                    break;
                case PARAMETERS:
                    if(typeTrail.matches("(.*)formal_parameter")) collector.add(input.substring(start, end));
                    break;
                case METHOD_NAMES:
                    if(typeTrail.matches("(.*)method_declaration")) collector.add(input.substring(start, end));
                    break;
                case CLASS_NAMES:
                    if(typeTrail.matches("(.*)class_declaration")) collector.add(input.substring(start, end));
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        }


        if(cursor.gotoFirstChild()) {
            String newTrail = typeTrail + "." + currentType;
            treeTraversalRecursive(cursor, input, newTrail, type, collector);
            cursor.gotoParent();
        }
        if(cursor.gotoNextSibling()) treeTraversalRecursive(cursor, input, typeTrail, type, collector);
    }

}
