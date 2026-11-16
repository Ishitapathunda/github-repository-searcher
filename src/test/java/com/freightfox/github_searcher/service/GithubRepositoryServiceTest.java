
package com.freightfox.github_searcher.service;

import com.freightfox.github_searcher.client.GithubApiClient;
import com.freightfox.github_searcher.dto.GithubRepositoryResponse;
import com.freightfox.github_searcher.dto.GithubSearchResponse;
import com.freightfox.github_searcher.entity.GithubRepository;
import com.freightfox.github_searcher.repository.GithubRepositoryRepository;

import com.freightfox.github_searcher.service.GithubRepositoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GithubRepositoryServiceTest {

    @Mock
    private GithubRepositoryRepository repository;

    @Mock
    private GithubApiClient githubApiClient;

    @InjectMocks
    private GithubRepositoryService service;

    @Test
    void searchAndSaveRepositoriesShouldSaveResults() {

        GithubRepositoryResponse.Owner owner =
                new GithubRepositoryResponse.Owner("test-user");

        GithubRepositoryResponse githubRepo =
                new GithubRepositoryResponse(
                        123L,
                        "spring-boot-example",
                        "Test repository",
                        owner,
                        "Java",
                        100,
                        20,
                        OffsetDateTime.now()
                );

        GithubSearchResponse response =
                new GithubSearchResponse(1, false, List.of(githubRepo));

        when(githubApiClient.searchRepositories(
                "spring boot", "Java", "stars", "desc", 30
        )).thenReturn(response);

        when(repository.saveAll(anyList()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        List<GithubRepository> result =
                service.searchAndSaveRepositories(
                        "spring boot", "Java", "stars"
                );

        assertEquals(1, result.size());
        assertEquals("spring-boot-example", result.get(0).getName());
        assertEquals(100, result.get(0).getStars());
        assertEquals("test-user", result.get(0).getOwner());

        verify(githubApiClient).searchRepositories(
                "spring boot", "Java", "stars", "desc", 30
        );
        verify(repository).saveAll(anyList());
    }

    @Test
    void searchAndSaveRepositoriesShouldReturnEmptyListWhenResponseIsNull() {

        when(githubApiClient.searchRepositories(
                anyString(), any(), any(), anyString(), anyInt()
        )).thenReturn(null);

        List<GithubRepository> result =
                service.searchAndSaveRepositories("spring boot", "Java", "stars");

        assertTrue(result.isEmpty());
        verify(repository, never()).saveAll(anyList());
    }

    @Test
    void getRepositoriesShouldReturnAllWhenNoFiltersProvided() {

        List<GithubRepository> expected = List.of(new GithubRepository());

        when(repository.findAll(any(Sort.class))).thenReturn(expected);

        List<GithubRepository> result =
                service.getRepositories(null, null, "stars");

        assertEquals(expected, result);
        verify(repository).findAll(any(Sort.class));
    }

    @Test
    void getRepositoriesShouldFilterByLanguage() {

        List<GithubRepository> expected = List.of(new GithubRepository());

        when(repository.findByLanguageIgnoreCase(
                eq("Java"), any(Sort.class)
        )).thenReturn(expected);

        List<GithubRepository> result =
                service.getRepositories("Java", null, "stars");

        assertEquals(expected, result);
        verify(repository).findByLanguageIgnoreCase(
                eq("Java"), any(Sort.class)
        );
    }

    @Test
    void getRepositoriesShouldFilterByMinimumStars() {

        List<GithubRepository> expected = List.of(new GithubRepository());

        when(repository.findByStarsGreaterThanEqual(
                eq(100), any(Sort.class)
        )).thenReturn(expected);

        List<GithubRepository> result =
                service.getRepositories(null, 100, "stars");

        assertEquals(expected, result);
        verify(repository).findByStarsGreaterThanEqual(
                eq(100), any(Sort.class)
        );
    }

    @Test
    void getRepositoriesShouldThrowExceptionForInvalidSort() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.getRepositories(null, null, "invalid")
        );
    }
}

