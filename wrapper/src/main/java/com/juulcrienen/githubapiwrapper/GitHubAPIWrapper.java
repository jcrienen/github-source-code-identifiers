package com.juulcrienen.githubapiwrapper;

import com.juulcrienen.githubapiwrapper.helpers.TemporaryFileHandler;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GitHubAPIWrapper {

    private GitHub github;
    private static TemporaryFileHandler temporaryFileHandler = new TemporaryFileHandler("github-api-wrapper");

    private static boolean debug;

    public GitHubAPIWrapper() throws IOException {
        this(false);
    }

    public GitHubAPIWrapper(boolean debug) throws IOException {
        github = GitHub.connectAnonymously();
        this.debug = debug;
    }

    public GitHubAPIWrapper(String username, String token) throws IOException {
        this(username, token, false);
    }

    public GitHubAPIWrapper(String username, String token, boolean verbose) throws IOException {
        github = GitHub.connect(username, token);
        this.debug = debug;
    }

    public GitHub getGitHub() {
        return github;
    }

    public void setGithub(GitHub github) {
        this.github = github;
    }

    public static TemporaryFileHandler getTemporaryFileHandler() {
        return temporaryFileHandler;
    }

    public GHRepository getGitHubRepository(String repository) throws IOException {
        debug("Getting repository information for " + repository + "...");
        return github.getRepository(repository);
    }

    public List<GHRepository> getGitHubRepositories(List<String> repositories) throws IOException{
        List<GHRepository> result = new ArrayList<>();
        for(String repo : repositories) {
            result.add(getGitHubRepository(repo));
        }
        return result;
    }

    public List<GHRepository> getGitHubRepositories(Set<String> repositories) throws IOException{
        List<GHRepository> result = new ArrayList<>();
        for(String repo : repositories) {
            result.add(getGitHubRepository(repo));
        }
        return result;
    }

    public List<GHRepository> getGitHubRepositories(InputStream inputStream) throws IOException {
        List<String> repositories = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                repositories.add(line);
            }
        }
        return getGitHubRepositories(repositories);
    }

    public static void error(String msg) {
        System.out.println("\u001B[31m" + msg);
    }

    public static void info(String msg) {
        System.out.println("\u001B[0m" + msg);
    }

    public static void debug(String msg) {
        if(!debug) return;
        System.out.println("\u001B[36m" + msg);
    }
}
