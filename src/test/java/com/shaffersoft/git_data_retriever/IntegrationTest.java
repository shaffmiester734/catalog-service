package com.shaffersoft.git_data_retriever;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.shaffersoft.git_data_retriever.model.GitData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.wiremock.spring.EnableWireMock;
import org.wiremock.spring.InjectWireMock;

@SpringBootTest
@EnableWireMock
public class IntegrationTest {

    @InjectWireMock
    private WireMockServer wm;

    @Autowired
    private GitFetcherService gitFetcherService;

    @Test
    public void test() {
        GitData data = gitFetcherService.fetchGitData("octocat");
        System.out.println(data);
    }

}
