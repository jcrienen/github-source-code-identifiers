package com.juulcrienen.GitHubSourceCodeIdentifiers.extractors;

import ai.serenade.treesitter.Languages;
import com.juulcrienen.GitHubSourceCodeIdentifiers.models.JavaSourceFile;

public class JavaExtractor extends Extractor<JavaSourceFile> {
    public JavaExtractor() {
        super(Languages.java());
    }
}
