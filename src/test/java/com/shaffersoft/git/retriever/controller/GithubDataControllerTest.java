package com.shaffersoft.git.retriever.controller;

import com.shaffersoft.git.retriever.exceptions.UsernameNotFoundException;
import com.shaffersoft.git.retriever.model.GitHubUserDTO;
import com.shaffersoft.git.retriever.service.GitFetcherService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GithubDataControllerTest {

    @Mock
    GitFetcherService gitFetcherService;

    @InjectMocks
    GithubDataController githubDataController;

    @Test
    public void getUserDataReturns404WhenGitFetcherServiceThrowsUsernameNotFoundException() {
        when(gitFetcherService.fetchGitData(eq("shaffmiester734")))
                .thenThrow(new UsernameNotFoundException("shaffmiester734"));

        ResponseEntity<GitHubUserDTO> response = githubDataController.getUserData("shaffmiester734");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void getUserDataReturnsExpectedResponseEntity() {
        GitHubUserDTO gitHubUserDTO = mock(GitHubUserDTO.class);

        when(gitFetcherService.fetchGitData(eq("shaffmiester734")))
                .thenReturn(gitHubUserDTO);

        ResponseEntity<GitHubUserDTO> response = githubDataController.getUserData("shaffmiester734");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(gitHubUserDTO);
    }


}