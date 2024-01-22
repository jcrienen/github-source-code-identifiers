package com.juulcrienen.githubapiwrapper;

import org.junit.Test;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GitHubAPIWrapperTests {

    private GHRepository testRepository;

    private void setup(GitHubAPIWrapper wrapper) throws IOException {
        GitHub github = mock(GitHub.class);
        wrapper.setGithub(github);

        testRepository = mock(GHRepository.class);
        when(testRepository.getOwnerName()).thenReturn("Test Owner");


        when(github.getRepository(any(String.class))).thenReturn(testRepository);
    }

    @Test
    public void getGitHubRepositoryTest() throws IOException{
        GitHubAPIWrapper wrapper = new GitHubAPIWrapper();
        setup(wrapper);

        assertEquals(testRepository, wrapper.getGitHubRepository("Test"));
    }

    @Test
    public void getGitHubRepositoriesTest() throws IOException{
        GitHubAPIWrapper wrapper = new GitHubAPIWrapper();
        setup(wrapper);

        List<String> testRepositories = new ArrayList<>();
        for(int i = 0; i < 100; i++) testRepositories.add("Test repository " + i);
        assertEquals(100, wrapper.getGitHubRepositories(testRepositories).size());
    }

    @Test
    public void getGitHubRepositoriesInputStreamTest() throws IOException{
        GitHubAPIWrapper wrapper = new GitHubAPIWrapper();
        setup(wrapper);

        InputStream inputStream = new ByteArrayInputStream("Test repository 1".getBytes());
        assertEquals(1, wrapper.getGitHubRepositories(inputStream).size());
    }

}
