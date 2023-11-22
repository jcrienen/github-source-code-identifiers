package com.juulcrienen.GitHubSourceCodeIdentifiers;

import com.juulcrienen.GitHubSourceCodeIdentifiers.extractors.CSharpExtractor;
import com.juulcrienen.GitHubSourceCodeIdentifiers.models.CSharpSourceFile;
import com.juulcrienen.githubapiwrapper.GitHubAPIWrapper;
import com.juulcrienen.githubapiwrapper.helpers.FileHelper;
import org.kohsuke.github.GHRepository;

import java.util.List;
import java.util.stream.Collectors;

public class Main {

    static {
        System.load(GitHubExtractor.class.getClassLoader().getResource("libjava-tree-sitter.so").getFile());
    }

    public static void main(String[] args) throws Exception {
        GitHubAPIWrapper wrapper = new GitHubAPIWrapper();
        GHRepository repo = wrapper.getGitHubRepository("shadowsocks/shadowsocks-windows");
        List<CSharpSourceFile> contents = FileHelper.getFiles(repo, ".cs").stream().map(CSharpSourceFile::new).collect(Collectors.toList());

        CSharpExtractor extractor = new CSharpExtractor();
        extractor.extractAll(contents.get(0));
        contents.get(0).getClassNames().forEach(System.out::println);

    }

}
