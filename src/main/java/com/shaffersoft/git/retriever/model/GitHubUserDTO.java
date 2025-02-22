package com.shaffersoft.git.retriever.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record GitHubUserDTO(
        String userName,
        String displayName,
        String avatar,
        String geoLocation,
        String email,
        String url,
        String createdAt,
        List<GitHubRepoDTO> repos
) {
}
