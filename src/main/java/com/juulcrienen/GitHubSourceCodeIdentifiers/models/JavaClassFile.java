package com.juulcrienen.GitHubSourceCodeIdentifiers.models;

import java.io.File;

public class JavaClassFile extends ClassFile {

    public JavaClassFile(File file) {
        super(file, ".java");
    }

}
