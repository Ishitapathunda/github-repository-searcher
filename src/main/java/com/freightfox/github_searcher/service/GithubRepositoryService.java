
package com.freightfox.github_searcher.service;

import com.freightfox.github_searcher.client.GithubApiClient;
import com.freightfox.github_searcher.dto.GithubRepositoryResponse;
import com.freightfox.github_searcher.dto.GithubSearchResponse;
import com.freightfox.github_searcher.entity.GithubRepository;
import com.freightfox.github_searcher.repository.GithubRepositoryRepository;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GithubRepositoryService {

    private final GithubRepositoryRepository repository;
    private final GithubApiClient githubApiClient;

    public GithubRepositoryService(
            GithubRepositoryRepository repository,
            GithubApiClient githubApiClient) {
        this.repository = repository;
        this.githubApiClient = githubApiClient;
    }

    // Search GitHub and save repositories
    public List<GithubRepository> searchAndSaveRepositories(
            String query,
            String language,
            String sort
    ) {

        GithubSearchResponse response =
                githubApiClient.searchRepositories(
                        query,
                        language,
                        sort,
                        "desc",
                        30
                );

        if (response == null || response.items() == null) {
            return List.of();
        }

        List<GithubRepository> repositories =
                response.items()
                        .stream()
                        .map(this::convertToEntity)
                        .toList();

        return repository.saveAll(repositories);
    }

    // Convert GitHub DTO into database Entity
    private GithubRepository convertToEntity(
            GithubRepositoryResponse response) {

        GithubRepository entity = new GithubRepository();

        entity.setGithubId(response.id());
        entity.setName(response.name());
        entity.setDescription(response.description());

        entity.setOwner(
                response.owner() != null
                        ? response.owner().login()
                        : "unknown"
        );

        entity.setLanguage(response.language());
        entity.setStars(response.stargazers_count());
        entity.setForks(response.forks_count());
        entity.setLastUpdated(response.updated_at());

        return entity;
    }

    // Retrieve repositories with optional filters
    public List<GithubRepository> getRepositories(
            String language,
            Integer minStars,
            String sort
    ) {

        Sort sorting = switch (sort == null ? "stars" : sort.toLowerCase()) {
            case "stars" -> Sort.by(Sort.Direction.DESC, "stars");
            case "forks" -> Sort.by(Sort.Direction.DESC, "forks");
            case "updated" -> Sort.by(Sort.Direction.DESC, "lastUpdated");
            default -> throw new IllegalArgumentException(
                    "Invalid sort field. Use stars, forks or updated."
            );
        };

        boolean hasLanguage =
                language != null && !language.isBlank();

        boolean hasMinStars = minStars != null;

        if (hasLanguage && hasMinStars) {
            return repository
                    .findByLanguageIgnoreCaseAndStarsGreaterThanEqual(
                            language, minStars, sorting);
        }

        if (hasLanguage) {
            return repository.findByLanguageIgnoreCase(
                    language, sorting);
        }

        if (hasMinStars) {
            return repository.findByStarsGreaterThanEqual(
                    minStars, sorting);
        }

        return repository.findAll(sorting);
    }
}
