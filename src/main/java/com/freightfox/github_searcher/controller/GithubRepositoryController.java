
package com.freightfox.github_searcher.controller;

import com.freightfox.github_searcher.dto.GithubSearchRequest;
import com.freightfox.github_searcher.entity.GithubRepository;
import com.freightfox.github_searcher.service.GithubRepositoryService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/github")
public class GithubRepositoryController {

    private final GithubRepositoryService service;

    public GithubRepositoryController(
            GithubRepositoryService service) {
        this.service = service;
    }

    // Search GitHub and save repositories
    @PostMapping("/search")
    public Map<String, Object> searchRepositories(
            @Valid @RequestBody GithubSearchRequest request) {

        List<GithubRepository> repositories =
                service.searchAndSaveRepositories(
                        request.query(),
                        request.language(),
                        request.sort()
                );

        return Map.of(
                "message", "Repositories fetched and saved successfully",
                "repositories", repositories
        );
    }

    // Retrieve stored repositories using optional filters
    @GetMapping("/repositories")
    public Map<String, Object> getRepositories(
            @RequestParam(required = false) String language,
            @RequestParam(required = false) Integer minStars,
            @RequestParam(defaultValue = "stars") String sort
    ) {

        List<GithubRepository> repositories =
                service.getRepositories(language, minStars, sort);

        return Map.of(
                "repositories", repositories
        );
    }
}
