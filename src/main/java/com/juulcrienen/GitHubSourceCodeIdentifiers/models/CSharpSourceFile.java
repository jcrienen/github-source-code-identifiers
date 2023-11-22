package com.juulcrienen.GitHubSourceCodeIdentifiers.models;

import java.io.File;

public class CSharpSourceFile extends SourceFile {

    public CSharpSourceFile(File file) {
        super(file, ".cs");
    }

}
