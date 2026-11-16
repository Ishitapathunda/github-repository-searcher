
package com.freightfox.github_searcher.dto;

import java.time.OffsetDateTime;

public record GithubRepositoryResponse(
        Long id,
        String name,
        String description,
        Owner owner,
        String language,
        Integer stargazers_count,
        Integer forks_count,
        OffsetDateTime updated_at
) {
    public record Owner(String login) {
    }
}

