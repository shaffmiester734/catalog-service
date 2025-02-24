package com.shaffersoft.git.retriever.controller;

import com.shaffersoft.git.retriever.exceptions.UsernameNotFoundException;
import com.shaffersoft.git.retriever.service.GitFetcherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class GithubDataControllerErrorTests {

    @MockitoBean
    private GitFetcherService gitFetcherService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void exceptionHandlerHandlesAnUnexpectedError() throws Exception {
        when(gitFetcherService.fetchGitData(eq("i-throw-error"))).thenThrow(new IllegalStateException("Oopsie"));

        mockMvc.perform(get("/i-throw-error"))
                .andExpect(status().is5xxServerError())
                .andExpect(content().string("Oopsie... Try again later and/or check the logs!"));
    }

    @Test
    public void getUserDataReturns404WhenGitFetcherServiceThrowsUsernameNotFoundException() throws Exception {
        when(gitFetcherService.fetchGitData(eq("shaffmiester734")))
                .thenThrow(new UsernameNotFoundException("shaffmiester734"));

        mockMvc.perform(get("/shaffmiester734"))
                .andExpect(status().isNotFound());
    }

}
