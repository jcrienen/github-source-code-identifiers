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
            Path directory = Paths.get(outputFolder + "/" + repository.getOwnerName() + ":" + repository.getName());
            Files.createDirectories(directory);

            List<SourceFile> contents = FileHelper.getFiles(repository, extension).stream().map(x -> new SourceFile(x, extension)).collect(Collectors.toList());

            if (contents.isEmpty()) {
                System.out.println("No files found with extension " + extension + " in repository " + repository.getName());
                return;
            }

            GitHubAPIWrapper.debug("Creating output folders and files...");
            Path methodNameFile = Paths.get(directory.toAbsolutePath() + "/method_names.txt");
            Path parameterFile = Paths.get(directory.toAbsolutePath() + "/parameters.txt");
            Path classNameFile = Paths.get(directory.toAbsolutePath() + "/class_names.txt");
            Path globalVariableFile = Paths.get(directory.toAbsolutePath() + "/global_variables.txt");
            Path localVariableFile = Paths.get(directory.toAbsolutePath() + "/local_variables.txt");

            Files.deleteIfExists(methodNameFile);
            Files.createFile(methodNameFile);
            Files.deleteIfExists(parameterFile);
            Files.createFile(parameterFile);
            Files.deleteIfExists(classNameFile);
            Files.createFile(classNameFile);
            Files.deleteIfExists(globalVariableFile);
            Files.createFile(globalVariableFile);
            Files.deleteIfExists(localVariableFile);
            Files.createFile(localVariableFile);

            GitHubAPIWrapper.info("Extracting identifiers from " + repository.getName());

            for (SourceFile content : contents) {
                extractor.extractAll(content);
                for (String identifier : content.getMethodNames()) {
                    Files.writeString(methodNameFile, identifier + System.lineSeparator(), StandardOpenOption.APPEND);
                }
                for (String identifier : content.getClassNames()) {
                    Files.writeString(classNameFile, identifier + System.lineSeparator(), StandardOpenOption.APPEND);
                }
                for (String identifier : content.getGlobalVariableNames()) {
                    Files.writeString(globalVariableFile, identifier + System.lineSeparator(), StandardOpenOption.APPEND);
                }
                for (String identifier : content.getLocalVariableNames()) {
                    Files.writeString(localVariableFile, identifier + System.lineSeparator(), StandardOpenOption.APPEND);
                }
                for (String identifier : content.getParameterNames()) {
                    Files.writeString(parameterFile, identifier + System.lineSeparator(), StandardOpenOption.APPEND);
                }
            }
        }
        GitHubAPIWrapper.info("Done! Output files can be found in " + Paths.get(outputFolder).toAbsolutePath() + "\n");
    }

    public GitHubAPIWrapper getWrapper() {
        return wrapper;
    }
}
