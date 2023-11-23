package com.juulcrienen.githubsourcecodeidentifiers.extractors;

import ai.serenade.treesitter.Parser;
import ai.serenade.treesitter.Tree;
import ai.serenade.treesitter.TreeCursor;
import ai.serenade.treesitter.TreeCursorNode;
import com.juulcrienen.githubsourcecodeidentifiers.models.SourceFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Extractor<T extends SourceFile> {
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

    public void extractAll(T file) throws IOException {
        extractVariables(file);
        extractClassNames(file);
        extractMethodNames(file);
        extractParameters(file);
    }

    public void extractVariables(T file) throws IOException {
        file.addVariableNames(treeTraversal(Files.readString(file.getFile().toPath()), ExtractionType.GLOBAL_VARIABLES, ExtractionType.LOCAL_VARIABLES));
    }

    public void extractMethodNames(T file) throws IOException {
        file.addMethodNames(treeTraversal(Files.readString(file.getFile().toPath()), ExtractionType.METHOD_NAMES));
    }

    public void extractParameters(T file) throws IOException {
        file.addParameterNames(treeTraversal(Files.readString(file.getFile().toPath()), ExtractionType.PARAMETERS));
    }

    public void extractClassNames(T file) throws IOException {
        file.addClassNames(treeTraversal(Files.readString(file.getFile().toPath()), ExtractionType.CLASS_NAMES));
    }

    public List<String> treeTraversal(String input, ExtractionType... type) throws UnsupportedEncodingException {
        try (Tree tree = fileParser.parseString(input)) {
            TreeCursor cursor = tree.getRootNode().walk();
            List<String> result = new ArrayList<>();
            treeTraversalRecursive(cursor, input, "", result, type);
            return result;
        }
    }

    private void treeTraversalRecursive(TreeCursor cursor, String input, String typeTrail, List<String> collector, ExtractionType... type) {
        TreeCursorNode current = cursor.getCurrentTreeCursorNode();
        String currentType = current.getType();

        if(currentType.equals("identifier") && current.getName() != null && current.getName().equals("name") && typeTrail != null) {
            int start = current.getStartByte();
            int end = current.getEndByte();
            for (ExtractionType extractionType : type) {
                if(typeTrail.matches(extractionType.getRegex())) collector.add(input.substring(start, end));
            }
        }

        if(cursor.gotoFirstChild()) {
            String newTrail = typeTrail + "." + currentType;
            treeTraversalRecursive(cursor, input, newTrail, collector, type);
            cursor.gotoParent();
        }
        if(cursor.gotoNextSibling()) treeTraversalRecursive(cursor, input, typeTrail, collector, type);
    }

}
