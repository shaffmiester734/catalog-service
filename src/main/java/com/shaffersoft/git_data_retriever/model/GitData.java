package com.shaffersoft.git_data_retriever.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record GitData(
        String login,
        String name,
        String avatarUrl,
        String location,
        String url,
        String createdAt) {
}
