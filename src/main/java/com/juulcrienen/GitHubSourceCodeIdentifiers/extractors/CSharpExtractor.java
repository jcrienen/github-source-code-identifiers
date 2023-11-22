package com.juulcrienen.GitHubSourceCodeIdentifiers.extractors;

import ai.serenade.treesitter.Languages;
import com.juulcrienen.GitHubSourceCodeIdentifiers.models.CSharpClassFile;

public class CSharpExtractor extends Extractor<CSharpClassFile> {
    public CSharpExtractor() {
        super(Languages.cSharp());
    }
}
