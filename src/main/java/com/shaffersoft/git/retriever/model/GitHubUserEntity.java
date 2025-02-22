package com.shaffersoft.git.retriever.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record GitHubUserEntity(
        String login,
        String name,
        String avatarUrl,
        String location,
        String email,
        String htmlUrl,
        String createdAt
) { }
