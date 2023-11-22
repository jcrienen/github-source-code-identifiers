package com.juulcrienen.GitHubSourceCodeIdentifiers.models;

import java.io.File;

public class JavaSourceFile extends SourceFile {

    public JavaSourceFile(File file) {
        super(file, ".java");
    }

}
