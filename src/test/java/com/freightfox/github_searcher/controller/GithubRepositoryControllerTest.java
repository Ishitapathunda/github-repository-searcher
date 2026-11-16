
package com.freightfox.github_searcher.controller;

import com.freightfox.github_searcher.entity.GithubRepository;
import com.freightfox.github_searcher.service.GithubRepositoryService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GithubRepositoryController.class)
@Import(com.freightfox.github_searcher.exception.GlobalExceptionHandler.class)
class GithubRepositoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GithubRepositoryService service;

    @Test
    void searchRepositoriesShouldReturnSuccess() throws Exception {

        GithubRepository repository = new GithubRepository();
        repository.setGithubId(123L);
        repository.setName("spring-boot-example");
        repository.setOwner("test-user");
        repository.setLanguage("Java");
        repository.setStars(100);
        repository.setForks(20);

        when(service.searchAndSaveRepositories(
                "spring boot", "Java", "stars"
        )).thenReturn(List.of(repository));

        mockMvc.perform(post("/api/github/search")
                        .contentType("application/json")
                        .content("""
                                {
                                    "query": "spring boot",
                                    "language": "Java",
                                    "sort": "stars"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Repositories fetched and saved successfully"))
                .andExpect(jsonPath("$.repositories[0].name")
                        .value("spring-boot-example"));

        verify(service).searchAndSaveRepositories(
                "spring boot", "Java", "stars"
        );
    }

    @Test
    void searchRepositoriesShouldRejectEmptyQuery() throws Exception {

        mockMvc.perform(post("/api/github/search")
                        .contentType("application/json")
                        .content("""
                                {
                                    "query": "",
                                    "language": "Java",
                                    "sort": "stars"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("Query cannot be empty"));

        verifyNoInteractions(service);
    }

    @Test
    void getRepositoriesShouldReturnStoredRepositories() throws Exception {

        GithubRepository repository = new GithubRepository();
        repository.setGithubId(123L);
        repository.setName("spring-boot-example");
        repository.setOwner("test-user");
        repository.setLanguage("Java");
        repository.setStars(100);
        repository.setForks(20);

        when(service.getRepositories("Java", 100, "stars"))
                .thenReturn(List.of(repository));

        mockMvc.perform(get("/api/github/repositories")
                        .param("language", "Java")
                        .param("minStars", "100")
                        .param("sort", "stars"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.repositories[0].name")
                        .value("spring-boot-example"))
                .andExpect(jsonPath("$.repositories[0].stars")
                        .value(100));

        verify(service).getRepositories("Java", 100, "stars");
    }

    @Test
    void getRepositoriesShouldReturnEmptyListWhenNoResults() throws Exception {

        when(service.getRepositories(null, null, "stars"))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/github/repositories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.repositories").isEmpty());
    }
}
