package com.shaffersoft.git_data_retriever;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class Config {

    @Value("${api.client.base-url}")
    private String baseUrl;

    @Bean
    public RestClient restClient(RestClient.Builder restClientBuilder) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

}
