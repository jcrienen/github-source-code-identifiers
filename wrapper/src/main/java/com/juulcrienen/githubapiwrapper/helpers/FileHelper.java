package com.juulcrienen.githubapiwrapper.helpers;

import com.juulcrienen.githubapiwrapper.GitHubAPIWrapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.kohsuke.github.GHRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileHelper {

    public static List<File> getFiles(GHRepository repository, String... extension) throws Exception {
        return getFiles(repository.getDefaultBranch(), repository, null, extension);
    }

    public static List<File> getFiles(GHRepository repository, FileCallback callback, String... extension) throws Exception {
        return getFiles(repository.getDefaultBranch(), repository, callback, extension);
    }

    public static List<File> getFiles(String branch, GHRepository repository, FileCallback callback, String... extension) throws Exception {
        Path tempRepository = GitHubAPIWrapper.getTemporaryFileHandler().createTempDir(repository.getName());

        String branchFull = "refs/heads/" + branch;

        GitHubAPIWrapper.info("Cloning repository " + repository.getName() + " into " + tempRepository.toAbsolutePath() + "...");
        Git git = Git.cloneRepository()
                .setURI(repository.getHttpTransportUrl())
                .setDirectory(tempRepository.toFile())
                .setBranchesToClone(Arrays.asList(branchFull))
                .setBranch(branchFull)
                .call();
        git.close();

        List<File> files = new ArrayList<>();

        try (Stream<Path> walk = Files.walk(tempRepository)) {
            GitHubAPIWrapper.debug("Searching file tree for files with extension(s) " + Arrays.toString(extension) + "...");
            files = walk
                    .filter(f -> Arrays.stream(extension).anyMatch(FilenameUtils.getExtension(f.toString())::equals)).map(x -> x.toFile()).collect(Collectors.toList());
        } catch (IOException e) {
            GitHubAPIWrapper.error(e.getMessage());
        }

        if (callback != null) {
            callback.doTrigger(files);
            GitHubAPIWrapper.info("Deleting temporary directory...");
            FileUtils.deleteQuietly(tempRepository.toFile());
        }

        return files;
    }

    public static List<File> getFilesFromUrl(String repositoryUrl, FileCallback callback, String... extension) throws IOException, GitAPIException {
        String[] splitRepository = repositoryUrl.split("/");
        String repositoryName = splitRepository[1];
        Path tempRepository = GitHubAPIWrapper.getTemporaryFileHandler().createTempDir(repositoryName);

        GitHubAPIWrapper.info("Cloning repository " + repositoryUrl + " into " + tempRepository.toAbsolutePath() + "...");
        Git git = Git.cloneRepository()
                .setURI(repositoryUrl)
                .setDirectory(tempRepository.toFile())
                .call();
        git.close();

        List<File> files = new ArrayList<>();

        try (Stream<Path> walk = Files.walk(tempRepository)) {
            GitHubAPIWrapper.debug("Searching file tree for files with extension(s) " + Arrays.toString(extension) + "...");
            files = walk
                    .filter(f -> Arrays.asList(extension).contains(FilenameUtils.getExtension(f.toString()))).map(Path::toFile).collect(Collectors.toList());
        } catch (IOException e) {
            GitHubAPIWrapper.error(e.getMessage());
        }

        if (callback != null) {
            callback.doTrigger(files);
            GitHubAPIWrapper.info("Deleting temporary directory...");
            FileUtils.deleteQuietly(tempRepository.toFile());
        }

        return files;
    }
}
