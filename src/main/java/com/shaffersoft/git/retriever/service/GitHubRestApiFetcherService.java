package com.shaffersoft.git.retriever.service;

import com.shaffersoft.git.retriever.exceptions.UsernameNotFoundException;
import com.shaffersoft.git.retriever.model.GitHubRepoEntity;
import com.shaffersoft.git.retriever.model.GitHubUserDTO;
import com.shaffersoft.git.retriever.model.GitHubUserEntity;
import com.shaffersoft.git.retriever.utils.GitConversionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class GitHubRestApiFetcherService implements GitFetcherService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GitHubRestApiFetcherService.class);

    private final RestClient restClient;

    public GitHubRestApiFetcherService(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    @Cacheable("users")
    public GitHubUserDTO fetchGitData(String username) throws UsernameNotFoundException {
        LOGGER.info("Fetching github data for username: {}", username);
        GitHubUserEntity userData = restClient.get()
                .uri("/users/{username}", username)
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, (request, response) -> {
                    throw new UsernameNotFoundException(username);
                })
                .body(GitHubUserEntity.class);
        LOGGER.debug("Github user data successfully retrieved for username: {}", username);

        LOGGER.debug("Fetching git repositories data for username: {}", username);
        List<GitHubRepoEntity> reposData = restClient.get()
                .uri("/users/{username}/repos", username)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
        LOGGER.debug("Repositories data successfully fetched for username: {}", username);

        LOGGER.info("Data successfully fetched from github for username: {}", username);
        return GitConversionUtils.convertToDto(userData, reposData);
    }


}
