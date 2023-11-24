package com.juulcrienen.githubsourcecodeidentifiers;

import com.juulcrienen.githubsourcecodeidentifiers.extractors.Extractor;
import com.juulcrienen.githubsourcecodeidentifiers.extractors.LanguageMapper;
import com.juulcrienen.githubsourcecodeidentifiers.models.SourceFile;
import com.juulcrienen.githubapiwrapper.GitHubAPIWrapper;
import com.juulcrienen.githubapiwrapper.helpers.FileHelper;
import org.apache.commons.io.FilenameUtils;
import org.kohsuke.github.GHRepository;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GitHubExtractor {

    private GitHubAPIWrapper wrapper;
    private Properties properties;

    private String classNamesFileName;
    private String methodNamesFileName;
    private String parametersFileName;
    private String localVariablesFileName;
    private String globalVariablesFileName;

    private String splitSuffix;
    private boolean split;


    public GitHubExtractor(String propertiesFile) throws Exception {
        this(propertiesFile, false);
    }

    public GitHubExtractor(String propertiesFile, boolean verbose) throws Exception {
        this.properties = new Properties();
        this.properties.load(new FileInputStream(propertiesFile));

        globalVariablesFileName = properties.getProperty("files.global-variables-filename");
        localVariablesFileName = properties.getProperty("files.local-variables-filename");
        classNamesFileName = properties.getProperty("files.class-names-filename");
        methodNamesFileName = properties.getProperty("files.method-names-filename");
        parametersFileName = properties.getProperty("files.parameters-filename");

        split = Boolean.parseBoolean(properties.getProperty("split.split"));
        splitSuffix = properties.getProperty("split.suffix");

        String username = properties.getProperty("github.username");
        String token = properties.getProperty("github.token");

        if(username != null && token != null && !username.isEmpty() && !token.isEmpty()) this.wrapper = new GitHubAPIWrapper(username, token, verbose);
        else this.wrapper = new GitHubAPIWrapper(verbose);

        System.load(Paths.get(this.properties.getProperty("library-file")).toAbsolutePath().toString());
    }

    public void extractIdentifiers() throws Exception {
        extractIdentifiers(properties.getProperty("input-file"), properties.getProperty("output-folder"), properties.getProperty("languages").split(","));
    }

    private void extractIdentifiers(String inputFile, String outputFolder, String... languages) throws Exception {
        GitHubAPIWrapper.info("Reading input file...");
        Set<String> inputRepositories;
        try (Stream<String> lines = Files.lines(Paths.get(inputFile))) {
            inputRepositories = lines.collect(Collectors.toSet());
        }
        GitHubAPIWrapper.info("Found " + inputRepositories.size() + " repositories");

        GitHubAPIWrapper.debug("Creating output folder");
        Files.createDirectories(Paths.get(outputFolder));

        List<GHRepository> repositories = wrapper.getGitHubRepositories(inputRepositories);

        for (GHRepository repository : repositories) {
            Path directory = Paths.get(outputFolder + "/" + repository.getOwnerName() + ":" + repository.getName());
            Files.createDirectories(directory);

            List<String> extensionsList = new ArrayList<>();
            for (String language : languages) {
                extensionsList.addAll(LanguageMapper.getExtensions(language, properties));
            }
            String[] extensionsArray = new String[extensionsList.size()];
            extensionsList.toArray(extensionsArray);

            List<SourceFile> contents = FileHelper.getFiles(repository, extensionsArray).stream().map(x -> new SourceFile(x, LanguageMapper.getLanguageByExtension(FilenameUtils.getExtension(x.getName()), properties))).collect(Collectors.toList());

            if (contents.isEmpty()) {
                GitHubAPIWrapper.info("No files found for language " + Arrays.toString(languages) + " in repository " + repository.getName());
                continue;
            }

            GitHubAPIWrapper.info("Extracting identifiers from " + repository.getName());
            writeToFiles(contents, directory);

        }
        GitHubAPIWrapper.info("Done! Output files can be found in " + Paths.get(outputFolder).toAbsolutePath() + "\n");
    }

    private void writeToFiles(List<SourceFile> sourceFiles, Path directory) throws IOException {
        Map<String, Path> paths = createFiles(directory, classNamesFileName, parametersFileName, localVariablesFileName, globalVariablesFileName, methodNamesFileName);

        for (SourceFile content : sourceFiles) {
            Extractor extractor = new Extractor(content.getLanguage());
            extractor.extractAll(content);

            writeToFile(content.getClassNames(), paths, classNamesFileName);
            writeToFile(content.getLocalVariableNames(), paths, localVariablesFileName);
            writeToFile(content.getGlobalVariableNames(), paths, globalVariablesFileName);
            writeToFile(content.getMethodNames(), paths, methodNamesFileName);
            writeToFile(content.getParameterNames(), paths, parametersFileName);
        }
    }

    private void writeToFile(List<String> identifiers, Map<String, Path> paths, String fileName) throws IOException {
        for (String identifier : identifiers) {
            if(split) {
                String[] splitIdentifiers = identifier.split(properties.getProperty("split.regex"));
                for (String splitIdentifier : splitIdentifiers) {
                    Files.writeString(paths.get(fileName + "_" + splitSuffix), splitIdentifier + System.lineSeparator(), StandardOpenOption.APPEND);
                }
            }
            Files.writeString(paths.get(fileName), identifier + System.lineSeparator(), StandardOpenOption.APPEND);
        }
    }

    private Map<String, Path> createFiles(Path directory, String... fileNames) throws IOException {
        Map<String, Path> paths = new HashMap<>();
        for (String fileName : fileNames) {
            paths.put(fileName, createFile(directory, fileName));
            if(split) paths.put(fileName + "_" + splitSuffix, createFile(directory, fileName + "_" + splitSuffix));
        }
        return paths;
    }

    private Path createFile(Path directory, String fileName) throws IOException {
        Path file = Paths.get(directory.toAbsolutePath() + "/" + fileName + ".txt");
        Files.deleteIfExists(file);
        Files.createFile(file);
        return file;
    }

    public GitHubAPIWrapper getWrapper() {
        return wrapper;
    }
}
