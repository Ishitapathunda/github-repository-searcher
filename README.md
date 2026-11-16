# GitHub Repository Searcher

A Spring Boot REST API application that searches GitHub repositories using the GitHub REST API, stores repository information in PostgreSQL, and provides endpoints to retrieve stored repositories using filters and sorting.

## 1. Project Overview

The GitHub Repository Searcher allows users to:

- Search GitHub repositories by repository name or keyword.
- Filter search results by programming language.
- Sort GitHub search results by stars, forks, or updated date.
- Store repository details in a PostgreSQL database.
- Retrieve stored repositories using optional filters.
- Update existing repository records instead of creating duplicate records for the same GitHub repository ID.
- Handle validation errors and GitHub API failures.

## 2. Technologies Used

| Technology | Purpose |
|---|---|
| Java 21 | Programming language |
| Spring Boot 4.1.2 | Application framework |
| Spring WebMVC | REST API development |
| Spring Data JPA | Database operations |
| PostgreSQL | Relational database |
| GitHub REST API | Fetch repository information |
| Maven | Dependency management and build |
| JUnit 5 | Unit testing |
| Mockito | Mocking dependencies |
| MockMvc | Controller testing |
| Postman | API testing |

## 3. Project Structure

```text
github-searcher/
│
├── src/
│   ├── main/
│   │   ├── java/com/freightfox/github_searcher/
│   │   │   ├── client/
│   │   │   │   └── GithubApiClient.java
│   │   │   ├── controller/
│   │   │   │   └── GithubRepositoryController.java
│   │   │   ├── dto/
│   │   │   │   ├── GithubRepositoryResponse.java
│   │   │   │   ├── GithubSearchRequest.java
│   │   │   │   └── GithubSearchResponse.java
│   │   │   ├── entity/
│   │   │   │   └── GithubRepository.java
│   │   │   ├── exception/
│   │   │   │   └── GlobalExceptionHandler.java
│   │   │   ├── repository/
│   │   │   │   └── GithubRepositoryRepository.java
│   │   │   ├── service/
│   │   │   │   └── GithubRepositoryService.java
│   │   │   └── GithubSearcherApplication.java
│   │   └── resources/
│   │       └── application.properties
│   │
│   └── test/
│       └── java/com/freightfox/github_searcher/
│           ├── service/
│           │   └── GithubRepositoryServiceTest.java
│           ├── controller/
│           │   └── GithubRepositoryControllerTest.java
│           └── GithubSearcherApplicationTests.java
│
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md
```

## 4. Prerequisites

Install the following before running the project:

- Java Development Kit (JDK) 21
- PostgreSQL 17 or compatible version
- IntelliJ IDEA or another Java IDE
- Postman (for API testing)

## 5. Database Configuration

Create a PostgreSQL database:

```sql
CREATE DATABASE github_searcher;
```

Configure the database connection in `src/main/resources/application.properties`.

Example:

```properties
spring.application.name=github-searcher

spring.datasource.url=jdbc:postgresql://localhost:5433/github_searcher
spring.datasource.username=postgres
spring.datasource.password=${DB_PASSWORD}

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

Set the `DB_PASSWORD` environment variable to your PostgreSQL password before starting the application.

The application uses Spring Data JPA to manage database operations and Hibernate to create or update the required table structure.

## 6. How to Run the Application

Clone the repository:

```bash
git clone <YOUR_GITHUB_REPOSITORY_URL>
```

Navigate to the project directory:

```bash
cd github-searcher
```

Check the Java version:

```bash
java -version
```

Run the application using Maven:

**Windows:**

```powershell
.\mvnw.cmd spring-boot:run
```

**Linux/macOS:**

```bash
./mvnw spring-boot:run
```

The application runs at:

```text
http://localhost:8080
```

## 7. API Documentation

### API 1: Search GitHub Repositories

**Endpoint**

```http
POST /api/github/search
```

**Full URL**

```text
http://localhost:8080/api/github/search
```

**Request Body**

```json
{
  "query": "spring boot",
  "language": "Java",
  "sort": "stars"
}
```

**Request Parameters**

| Field | Description |
|---|---|
| query | Repository name or search keyword (required) |
| language | Programming language filter |
| sort | Sorting criteria: stars, forks, or updated |

**Successful Response**

```json
{
  "message": "Repositories fetched and saved successfully",
  "repositories": [
    {
      "githubId": 123456,
      "name": "spring-boot-example",
      "description": "An example repository for Spring Boot",
      "owner": "user123",
      "language": "Java",
      "stars": 450,
      "forks": 120,
      "lastUpdated": "2024-01-01T12:00:00Z"
    }
  ]
}
```

The response fields contain actual repository data returned by GitHub. The example values above are illustrative.

### API 2: Retrieve Stored Repositories

**Endpoint**

```http
GET /api/github/repositories
```

**Full URL**

```text
http://localhost:8080/api/github/repositories
```

**Optional Query Parameters**

| Parameter | Description |
|---|---|
| language | Filter by programming language |
| minStars | Minimum star count |
| sort | Sort by stars, forks, or updated; defaults to stars |

**Example Requests**

Retrieve all repositories:

```http
GET /api/github/repositories
```

Filter by language:

```http
GET /api/github/repositories?language=Java
```

Filter by minimum stars:

```http
GET /api/github/repositories?minStars=100
```

Sort by forks:

```http
GET /api/github/repositories?sort=forks
```

Filter by language and minimum stars:

```http
GET /api/github/repositories?language=Java&minStars=100&sort=stars
```

**Successful Response**

```json
{
  "repositories": [
    {
      "githubId": 123456,
      "name": "spring-boot-example",
      "description": "An example repository for Spring Boot",
      "owner": "user123",
      "language": "Java",
      "stars": 450,
      "forks": 120,
      "lastUpdated": "2024-01-01T12:00:00Z"
    }
  ]
}
```

## 8. Error Handling

The application includes global exception handling and request validation.

Examples:

| Scenario | HTTP Status |
|---|---|
| Successful request | 200 OK |
| Empty search query | 400 Bad Request |
| Invalid sorting field | 400 Bad Request |
| GitHub API rate-limit response | Corresponding GitHub error status |
| Unexpected server error | 500 Internal Server Error |

Example validation error:

```json
{
  "error": "Query cannot be empty"
}
```

## 9. Database Operations

Repository details are stored in the `repositories` table.

The GitHub repository ID is used as the primary key to identify each repository.

Stored fields include:

- GitHub repository ID
- Repository name
- Description
- Owner name
- Programming language
- Stars count
- Forks count
- Last updated date

When a repository is fetched again, its existing record is updated using the same GitHub ID rather than creating another record.

## 10. Running Tests

The project includes service-layer and controller-layer tests using JUnit 5, Mockito, and MockMvc.

Run all tests:

**Windows:**

```powershell
.\mvnw.cmd clean test
```

**Linux/macOS:**

```bash
./mvnw clean test
```

Current test coverage includes:

- GitHub repository search and saving.
- Handling null API responses.
- Retrieving repositories.
- Filtering by language.
- Filtering by minimum stars.
- Invalid sorting criteria.
- Controller POST and GET endpoints.
- Empty search validation.
- Empty repository results.

**Latest test execution:** 11 tests passed, 0 failures, 0 errors.

## 11. External API

GitHub REST API:

https://api.github.com/search/repositories

Official documentation:

https://docs.github.com/en/rest/search/search#search-repositories

GitHub API rate limits apply to requests.

## 12. Author

Developed as a backend technical assignment for FreightFox.

**Project:** GitHub Repository Searcher  
**Language:** Java 21  
**Framework:** Spring Boot  
**Database:** PostgreSQL
