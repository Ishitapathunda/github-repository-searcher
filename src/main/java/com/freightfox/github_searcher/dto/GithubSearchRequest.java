
package com.freightfox.github_searcher.dto;

import jakarta.validation.constraints.NotBlank;

public record GithubSearchRequest(
        @NotBlank(message = "Query cannot be empty")
        String query,

        String language,
        String sort
) {}

