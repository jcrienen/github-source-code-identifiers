package com.juulcrienen.githubapiwrapper.helpers;

import com.juulcrienen.githubapiwrapper.GitHubAPIWrapper;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TemporaryFileHandler {

    private Path tempDir;

    public TemporaryFileHandler(String prefix) {
        try {
            tempDir = Files.createTempDirectory(prefix);
            GitHubAPIWrapper.debug("Creating temporary directory " + tempDir.toAbsolutePath() + "...");
        } catch (IOException e) {
            GitHubAPIWrapper.error(e.getMessage());
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            GitHubAPIWrapper.info("Removing temporary files and folders...");
            FileUtils.deleteQuietly(tempDir.toFile());
        }));
    }

    public Path getTempDir() {
        return tempDir;
    }

    public Path createTempDir(String name) {
        Path temp = null;
        try {
            temp = Files.createTempDirectory(tempDir, name);
            GitHubAPIWrapper.debug("Creating temporary directory " + temp.toAbsolutePath() + "...");
        } catch (IOException e) {
            GitHubAPIWrapper.error(e.getMessage());
        }

        return temp;
    }


}
