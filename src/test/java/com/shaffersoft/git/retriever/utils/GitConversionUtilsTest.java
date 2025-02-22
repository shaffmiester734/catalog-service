package com.shaffersoft.git.retriever.utils;

import com.shaffersoft.git.retriever.model.GitHubRepoDTO;
import com.shaffersoft.git.retriever.model.GitHubRepoEntity;
import com.shaffersoft.git.retriever.model.GitHubUserDTO;
import com.shaffersoft.git.retriever.model.GitHubUserEntity;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GitConversionUtilsTest {

    @Test
    public void convertDateFormatRonvertsAValid8601String(){
        String convertedDate = GitConversionUtils.convertDateFormat("2011-01-25T18:44:36Z");
        assertThat(convertedDate).isEqualTo("2011-01-25 18:44:36");
    }

    @Test
    public void convertDateFormatReturnsNullWhenPassedNull(){
        String convertedDate = GitConversionUtils.convertDateFormat(null);
        assertThat(convertedDate).isNull();
    }

    @Test
    public void convertDateFormatReturnsTheInputStringWhenItCantBeConverted(){
        String convertedDate = GitConversionUtils.convertDateFormat("I Am Invalid");
        assertThat(convertedDate).isEqualTo("I Am Invalid");
    }

    @Test
    public void convertDateFormatTest(){
        String convertedDate = GitConversionUtils.convertDateFormat("2011-01-25T18:44:36Z");
        assertThat(convertedDate).isEqualTo("2011-01-25 18:44:36");
    }

    @Test
    public void convertToDtoCanConvertAValidObject() {
        // Create the test data
        GitHubUserEntity gitHubUserEntity = new GitHubUserEntity(
                "octocat",
                "The Octocat",
                "https://avatars.githubusercontent.com/u/583231?v=4",
                "San Francisco",
                "joel@shaffersoft.com",
                "https://github.com/octocat",
                "2011-01-25T18:44:36Z"
        );

        List<GitHubRepoEntity> gitHubRepoEntityList = List.of(
                new GitHubRepoEntity("boysenberry-repo-1", "https://github.com/octocat/boysenberry-repo-1"),
                new GitHubRepoEntity("git-consortium", "https://github.com/octocat/git-consortium"),
                new GitHubRepoEntity("hello-worId", "https://github.com/octocat/hello-worId")
        );

        // Run method under test
        GitHubUserDTO githubUserDTO = GitConversionUtils.convertToDto(gitHubUserEntity, gitHubRepoEntityList);

        // Validate results
        assertThat(githubUserDTO)
                .returns("octocat", GitHubUserDTO::userName)
                .returns("The Octocat", GitHubUserDTO::displayName)
                .returns("https://avatars.githubusercontent.com/u/583231?v=4", GitHubUserDTO::avatar)
                .returns("San Francisco", GitHubUserDTO::geoLocation)
                .returns("joel@shaffersoft.com", GitHubUserDTO::email)
                .returns("https://github.com/octocat", GitHubUserDTO::url)
                .returns("2011-01-25 18:44:36", GitHubUserDTO::createdAt);

        // Validate the repos
        assertThat(githubUserDTO.repos()).isNotNull();
        assertThat(githubUserDTO.repos()).hasSize(3);
        assertThat(githubUserDTO.repos()).containsExactly(
                new GitHubRepoDTO("boysenberry-repo-1", "https://github.com/octocat/boysenberry-repo-1"),
                new GitHubRepoDTO("git-consortium", "https://github.com/octocat/git-consortium"),
                new GitHubRepoDTO("hello-worId", "https://github.com/octocat/hello-worId")
        );

    }

}