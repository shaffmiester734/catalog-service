package com.shaffersoft.git.retriever.service;

import com.shaffersoft.git.retriever.exceptions.UsernameNotFoundException;
import com.shaffersoft.git.retriever.model.GitHubUserDTO;

public interface GitFetcherService {

    /**
     * Collects data about a GitHub users data from github
     * @param username GitHub username you want to gather data for
     * @return {@link GitHubUserDTO}
     */
    GitHubUserDTO fetchGitData(String username) throws UsernameNotFoundException;
}
