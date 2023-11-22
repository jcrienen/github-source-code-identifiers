package com.juulcrienen.GitHubSourceCodeIdentifiers;

public class GitHubExtractor {

    static {
        System.load(GitHubExtractor.class.getClassLoader().getResource("libjava-tree-sitter.so").getFile());
    }

    public GitHubExtractor() {}

}
