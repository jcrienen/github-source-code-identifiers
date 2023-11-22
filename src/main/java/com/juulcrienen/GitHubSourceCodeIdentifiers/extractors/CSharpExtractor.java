package com.juulcrienen.GitHubSourceCodeIdentifiers.extractors;

import ai.serenade.treesitter.Languages;
import com.juulcrienen.GitHubSourceCodeIdentifiers.models.CSharpSourceFile;

public class CSharpExtractor extends Extractor<CSharpSourceFile> {
    public CSharpExtractor() {
        super(Languages.cSharp());
    }
}
