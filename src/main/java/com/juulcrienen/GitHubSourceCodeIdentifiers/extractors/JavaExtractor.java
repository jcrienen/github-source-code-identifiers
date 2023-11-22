package com.juulcrienen.GitHubSourceCodeIdentifiers.extractors;

import ai.serenade.treesitter.Languages;
import com.juulcrienen.GitHubSourceCodeIdentifiers.models.JavaClassFile;

public class JavaExtractor extends Extractor<JavaClassFile> {
    public JavaExtractor() {
        super(Languages.java());
    }
}
