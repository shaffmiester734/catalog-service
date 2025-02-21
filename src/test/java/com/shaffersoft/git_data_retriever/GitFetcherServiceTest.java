package com.shaffersoft.git_data_retriever;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(components = {GitFetcherService.class, Config.class})
class GitFetcherServiceTest {

    @Autowired
    GitFetcherService gitFetcherService;

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() throws Exception {
        String detailsString =
                objectMapper.writeValueAsString(new String("SDF"));

        this.server.expect(requestTo("/john/details"))
                .andRespond(withSuccess(detailsString, MediaType.APPLICATION_JSON));
    }

    @Test
    public void tester() {
       // GitFetcherService gitFetcherService = new GitFetcherService(null);

        gitFetcherService.fetchGitData("octocat");

        this.server.expect(requestTo("/users/octocats"));
    }

}