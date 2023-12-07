package com.juulcrienen.githubsourcecodeidentifiers;

import com.juulcrienen.githubapiwrapper.helpers.FileCallback;
import com.juulcrienen.githubsourcecodeidentifiers.extractors.Extractor;
import com.juulcrienen.githubsourcecodeidentifiers.extractors.LanguageMapper;
import com.juulcrienen.githubsourcecodeidentifiers.models.SourceFile;
import com.juulcrienen.githubapiwrapper.GitHubAPIWrapper;
import com.juulcrienen.githubapiwrapper.helpers.FileHelper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.kohsuke.github.GHRepository;

import java.io.File;
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

    private final GitHubAPIWrapper wrapper;
    private final Properties properties;

    private final String classNamesFileName;
    private final String methodNamesFileName;
    private final String parametersFileName;
    private final String localVariablesFileName;
    private final String globalVariablesFileName;

    private final String splitSuffix;
    private final boolean split;


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
        Path dir = Paths.get(outputFolder);
        Files.createDirectories(dir);

        int count = 0;
        int size = inputRepositories.size();
        for (String repository : inputRepositories) {
            count++;
            String[] splitRepository = repository.split("/");
            String owner = splitRepository[0];
            String repositoryName = splitRepository[1];
            String repositoryTopic = splitRepository[2];

            Path directory = Paths.get(outputFolder + "/" + owner + " - " + repositoryName + " - " + repositoryTopic);
            if(Files.exists(directory)) {
                GitHubAPIWrapper.info(repository + " already exists, skipping!");
                continue;
            }
            Files.createDirectories(directory);

            List<String> extensionsList = new ArrayList<>();
            for (String language : languages) {
                extensionsList.addAll(LanguageMapper.getExtensions(language, properties));
            }
            String[] extensionsArray = new String[extensionsList.size()];
            extensionsList.toArray(extensionsArray);

            int finalCount = count;
            FileCallback callback = new FileCallback() {
                @Override
                public void doTrigger(List<File> contents) throws IOException {
                    List<SourceFile> sourceFiles = contents.stream().map(x -> new SourceFile(x, LanguageMapper.getLanguageByExtension(FilenameUtils.getExtension(x.getName()), properties))).collect(Collectors.toList());

                    if (sourceFiles.isEmpty()) {
                        GitHubAPIWrapper.info("No files found for language " + Arrays.toString(languages) + " in repository " + repository);
                        FileUtils.deleteQuietly(directory.toFile());
                        return;
                    }

                    GitHubAPIWrapper.info("(" + finalCount + "/" + size + ") Extracting identifiers from " + repository);
                    writeToFiles(sourceFiles, directory);
                }
            };

            try {
                FileHelper.getFilesFromUrl("https://github.com/" + owner + "/" + repositoryName, callback, extensionsArray);
            } catch (GitAPIException e) {
                GitHubAPIWrapper.error("Error cloning repository " + repository);
            } catch (IOException e) {
                GitHubAPIWrapper.error("Error writing files for " + repository);
            }
        }
        GitHubAPIWrapper.info("Done! Output files can be found in " + dir.toAbsolutePath() + "\n");
    }

    private void writeToFiles(List<SourceFile> sourceFiles, Path directory) throws IOException {
        Map<String, Path> paths = createFiles(directory, classNamesFileName, parametersFileName, localVariablesFileName, globalVariablesFileName, methodNamesFileName);

        for (SourceFile content : sourceFiles) {
            Extractor<SourceFile> extractor = new Extractor<SourceFile>(content.getLanguage());
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
