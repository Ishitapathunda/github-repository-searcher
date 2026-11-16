
package com.freightfox.github_searcher.client;

import com.freightfox.github_searcher.dto.GithubSearchResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class GithubApiClient {

    private final RestClient restClient;

    public GithubApiClient() {

        this.restClient = RestClient.builder()
                .baseUrl("https://api.github.com")
                .defaultHeader(
                        "Accept",
                        "application/vnd.github+json"
                )
                .defaultHeader(
                        "User-Agent",
                        "FreightFox-GitHub-Searcher"
                )
                .defaultHeader(
                        "X-GitHub-Api-Version",
                        "2022-11-28"
                )
                .build();
    }


    public GithubSearchResponse searchRepositories(
            String query,
            String language,
            String sort,
            String order,
            int perPage
    ) {

        String searchQuery = query;

        if (language != null && !language.isBlank()) {
            searchQuery += " language:" + language;
        }

        final String finalSearchQuery = searchQuery;

        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search/repositories")
                        .queryParam("q", finalSearchQuery)
                        .queryParam("sort", sort)
                        .queryParam("order", order)
                        .queryParam("per_page", perPage)
                        .build()
                )
                .retrieve()
                .body(GithubSearchResponse.class);
    }

}

