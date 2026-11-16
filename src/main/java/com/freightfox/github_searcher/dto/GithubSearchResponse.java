
package com.freightfox.github_searcher.dto;

import java.util.List;

public record GithubSearchResponse(
        Integer total_count,
        Boolean incomplete_results,
        List<GithubRepositoryResponse> items
) {
}
