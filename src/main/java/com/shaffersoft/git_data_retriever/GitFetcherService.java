package com.shaffersoft.git_data_retriever;

import com.shaffersoft.git_data_retriever.model.GitData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class GitFetcherService {

    private final RestClient restClient;

    public GitFetcherService(RestClient restClient) {
        this.restClient = restClient;
    }

    public GitData fetchGitData(String username) {
        return restClient.get()
                .uri("/users/{username}", username)
                .retrieve()
                .body(GitData.class);
    }


}
