package com.juulcrienen.githubsourcecodeidentifiers;

import com.juulcrienen.githubsourcecodeidentifiers.extractors.Extractor;
import com.juulcrienen.githubsourcecodeidentifiers.extractors.LanguageMapper;
import com.juulcrienen.githubsourcecodeidentifiers.models.SourceFile;
import com.juulcrienen.githubapiwrapper.GitHubAPIWrapper;
import com.juulcrienen.githubapiwrapper.helpers.FileHelper;
import org.kohsuke.github.GHRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GitHubExtractor {

    private GitHubAPIWrapper wrapper;

    public GitHubExtractor(String libraryPath) throws IOException {
        this(libraryPath, false);
    }

    public GitHubExtractor(String libraryPath, boolean verbose) throws IOException {
        this.wrapper = new GitHubAPIWrapper(verbose);
        System.load(Paths.get(libraryPath).toAbsolutePath().toString());
    }

    public GitHubExtractor(String libraryPath, String username, String token) throws IOException {
        this(libraryPath, username, token, false);
    }

    public GitHubExtractor(String libraryPath, String username, String token, boolean verbose) throws IOException {
        this.wrapper = new GitHubAPIWrapper(username, token, verbose);
        System.load(Paths.get(libraryPath).toAbsolutePath().toString());
    }

    public void extractIdentifiers(String inputFile, String outputFolder, String extension) throws Exception {
        long language = LanguageMapper.getLanguage(extension);

        GitHubAPIWrapper.info("Reading input file...");
        Set<String> inputRepositories;
        try (Stream<String> lines = Files.lines(Paths.get(inputFile))) {
            inputRepositories = lines.collect(Collectors.toSet());
        }
        GitHubAPIWrapper.info("Found " + inputRepositories.size() + " repositories");

        GitHubAPIWrapper.debug("Creating output folder");
        Files.createDirectories(Paths.get(outputFolder));

        List<GHRepository> repositories = wrapper.getGitHubRepositories(inputRepositories);

        Extractor extractor = new Extractor(language);

        for (GHRepository repository : repositories) {
            List<SourceFile> contents = FileHelper.getFiles(repository, extension).stream().map(x -> new SourceFile(x, extension)).collect(Collectors.toList());

            if (contents.isEmpty()) {
                System.out.println("No files found with extension " + extension + " in repository " + repository.getName());
                return;
            }

            GitHubAPIWrapper.debug("Creating output file");
            Path file = Paths.get(outputFolder + "/" + repository.getOwnerName() + ":" + repository.getName());
            Files.deleteIfExists(file);
            Files.createFile(file);

            GitHubAPIWrapper.info("Extracting identifiers from " + repository.getName());

            for (SourceFile content : contents) {
                extractor.extractAll(content);
                for (String identifier : content.getIdentifiers()) {
                    Files.writeString(file, identifier + System.lineSeparator(), StandardOpenOption.APPEND);
                }
            }
        }
        GitHubAPIWrapper.info("Done! Output files can be found in " + Paths.get(outputFolder).toAbsolutePath() + "\n");
    }

    public GitHubAPIWrapper getWrapper() {
        return wrapper;
    }
}
