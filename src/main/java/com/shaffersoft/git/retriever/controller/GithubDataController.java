package com.shaffersoft.git.retriever.controller;

import com.shaffersoft.git.retriever.exceptions.UsernameNotFoundException;
import com.shaffersoft.git.retriever.model.GitHubUserDTO;
import com.shaffersoft.git.retriever.service.GitFetcherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GithubDataController {
    private static final Logger LOGGER = LoggerFactory.getLogger(GithubDataController.class);

    private final GitFetcherService gitFetcherService;

    public GithubDataController(GitFetcherService gitFetcherService) {
        this.gitFetcherService = gitFetcherService;
    }

    @GetMapping("/{username}")
    public ResponseEntity<GitHubUserDTO> getUserData(@PathVariable(value = "username") String username) {
        try {
            return new ResponseEntity<>(gitFetcherService.fetchGitData(username), HttpStatus.OK);
        } catch (UsernameNotFoundException e) {
            LOGGER.warn("The username {} does not exist in GitHub", username);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleNotFound(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Oopsie... Try again later and/or check the logs!");
    }

}
