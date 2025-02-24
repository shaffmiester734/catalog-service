package com.shaffersoft.git.retriever;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.shaffersoft.git.retriever.service.GitFetcherService;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.wiremock.spring.EnableWireMock;
import org.wiremock.spring.InjectWireMock;

import java.io.IOException;
import java.nio.file.Files;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@AutoConfigureMockMvc
@EnableWireMock
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationTests {

    @InjectWireMock
    private WireMockServer wireMockServer;

    @MockitoSpyBean
    private GitFetcherService gitFetcherServiceSpy;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setupMockGithubResponses() throws IOException {
        // Stub the result that GitHub returns if you try a username that doesn't exist
        wireMockServer.stubFor(get(urlEqualTo("/users/i-dont-exist"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withBody("""
                                {
                                  "message": "Not Found",
                                  "documentation_url": "https://docs.github.com/rest",
                                  "status": "404"
                                }
                                """)
                        .withHeader("Content-Type", "application/json")));

        wireMockServer.stubFor(get(urlEqualTo("/users/octocat"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(Files.readString(new ClassPathResource("test-data/user.json").getFile().toPath()))
                        .withHeader("Content-Type", "application/json")));

        wireMockServer.stubFor(get(urlEqualTo("/users/octocat/repos"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(Files.readString(new ClassPathResource("test-data/repos.json").getFile().toPath()))
                        .withHeader("Content-Type", "application/json")));
    }

    @Test
    public void returnsA404IfUserCantBeLocated() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/i-dont-exist"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void easdasdf() throws Exception {

        when(gitFetcherServiceSpy.fetchGitData(eq("i-throw-an-error"))).thenThrow(new IllegalStateException("Ooopsie"));

        mockMvc.perform(MockMvcRequestBuilders.get("/i-throw-an-error"))
                .andExpect(MockMvcResultMatchers.status().is5xxServerError());
    }

    @Test
    public void returnsExpectedDataForValidUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/octocat"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.user_name").value("octocat"))
                .andExpect(jsonPath("$.display_name").value("The Octocat"))
                .andExpect(jsonPath("$.avatar").value("https://avatars.githubusercontent.com/u/583231?v=4"))
                .andExpect(jsonPath("$.geo_location").value("San Francisco"))
                .andExpect(jsonPath("$.email").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.url").value("https://github.com/octocat"))
                .andExpect(jsonPath("$.created_at").value("2011-01-25 18:44:36"))
                .andExpect(jsonPath("$.repos", hasSize(8)))
                .andExpect(jsonPath("$.repos[?(@.name == 'boysenberry-repo-1' && @.url == 'https://github.com/octocat/boysenberry-repo-1')]").exists())
                .andExpect(jsonPath("$.repos[?(@.name == 'linguist' && @.url == 'https://github.com/octocat/linguist')]").exists());
    }

    @Test
    public void cachesDataSoItWillOnlyMakeOneCallPerUserName() throws Exception {
        // Call the service multiple times with the same username
        mockMvc.perform(MockMvcRequestBuilders.get("/octocat"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/octocat"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/octocat"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Validate only one http call was made to wiremock server
        wireMockServer.verify(1, getRequestedFor(urlEqualTo("/users/octocat")));
    }

}
