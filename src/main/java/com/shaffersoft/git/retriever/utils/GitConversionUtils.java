package com.shaffersoft.git.retriever.utils;

import com.shaffersoft.git.retriever.model.GitHubRepoDTO;
import com.shaffersoft.git.retriever.model.GitHubRepoEntity;
import com.shaffersoft.git.retriever.model.GitHubUserDTO;
import com.shaffersoft.git.retriever.model.GitHubUserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class GitConversionUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(GitConversionUtils.class);
    private static final DateTimeFormatter ISO8601_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX");
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private GitConversionUtils() {
    }

    public static GitHubUserDTO convertToDto(GitHubUserEntity gitHubUserEntity, List<GitHubRepoEntity> gitHubRepoEntityList) {
        List<GitHubRepoDTO> gitHubRepoDTOS = gitHubRepoEntityList.stream()
                .map(gitHubRepoEntity -> new GitHubRepoDTO(gitHubRepoEntity.name(), gitHubRepoEntity.htmlUrl()))
                .toList();

        return new GitHubUserDTO(
                gitHubUserEntity.login(),
                gitHubUserEntity.name(),
                gitHubUserEntity.avatarUrl(),
                gitHubUserEntity.location(),
                gitHubUserEntity.email(),
                gitHubUserEntity.htmlUrl(),
                convertDateFormat(gitHubUserEntity.createdAt()),
                gitHubRepoDTOS);
    }

    static String convertDateFormat(String ISO8601String) {
        if (ISO8601String == null) {
            return null;
        }

        try {
            LocalDateTime localDateTime = LocalDateTime.parse(ISO8601String, ISO8601_FORMATTER);
            return localDateTime.format(TIMESTAMP_FORMATTER);
        } catch (DateTimeParseException e) {
            LOGGER.warn("Encountered a date string that is not in ISO 8601 format: {}", ISO8601String);
            return ISO8601String;
        }
    }
}
