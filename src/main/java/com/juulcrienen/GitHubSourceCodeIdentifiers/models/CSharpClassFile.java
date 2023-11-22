package com.juulcrienen.GitHubSourceCodeIdentifiers.models;

import java.io.File;

public class CSharpClassFile extends ClassFile{

    public CSharpClassFile(File file) {
        super(file, ".cs");
    }

}
