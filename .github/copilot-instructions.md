# Chzzk OpenAPI Kotlin Client - AI Coding Agent Instructions

## Project Overview

This is a Kotlin SDK for the Chzzk (네이버 치지직) streaming platform OpenAPI. It provides a type-safe, coroutine-based HTTP client wrapping the Chzzk REST API using Retrofit, OkHttp, and Moshi.

## Architecture

### Core Client Pattern

- **Entry point**: `ChzzkClient` is built using a fluent builder pattern with two authentication modes:
  - Client credentials (Client-Id/Client-Secret headers) for public API calls
  - Access token (Bearer token) for user-authenticated endpoints
- Authentication is injected via OkHttp interceptors (`ClientAuthInterceptor`, `AccessTokenInterceptor`) in `src/main/kotlin/org/mokokomc/chzzk/auth/Interceptors.kt`
- All API interfaces are lazily initialized properties on `ChzzkClient` (e.g., `client.channel`, `client.live`)

### API Layer Organization

Five API domains in `src/main/kotlin/org/mokokomc/chzzk/api/`:

- `ChannelApi`: Channel info, followers, subscribers, restrictions
- `LiveApi`: Live stream management, settings, stream keys
- `ChatApi`: Chat configuration
- `DropsApi`: Rewards/drops claims and fulfillment
- `SessionApi`: WebSocket session auth and event subscriptions

Each API interface uses Retrofit annotations (`@GET`, `@POST`, `@PATCH`, `@PUT`, `@DELETE`) with suspend functions for coroutine support.

### Data Models

Located in `src/main/kotlin/org/mokokomc/chzzk/model/`:

- Response wrappers: `ChzzkResponse<T>`, `SimpleResponse`, `Page`, `CursorPage`
- Domain models organized by feature: `Channel.kt`, `Live.kt`, `Chat.kt`, `Drops.kt`, `Session.kt`
- Enums for API constants: `UserRole`, `SessionStatus`, `CategoryType`, `ChatAvailableCondition`, `FulfillmentStatus`
- All data classes use **nullable fields** where API might omit values - this is the error handling strategy
- Request/response naming: `*Request` suffix for request bodies, `*Response` suffix for responses (e.g., `UpdateLiveSettingRequest`, `LiveListResponse`)

### Testing Strategy

Two test categories in `src/test/kotlin/org/mokokomc/chzzk/test/`:

1. **Unit tests** (`ChzzkClientTest.kt`): Builder configuration and validation
2. **Integration tests** (`integration/` folder): MockWebServer-based API contract tests
   - Each API has its own integration test class
   - Tests verify request headers, response parsing, and error handling
   - Use `kotlinx-coroutines-test` with `runTest` for coroutine testing

## Development Workflows

### Building and Testing

```bash
# Run all tests
./gradlew test

# Build fat JAR with dependencies (shadow plugin)
./gradlew shadowJar

# Clean and rebuild
./gradlew clean build

# Run both build and test in one command (CI workflow)
./gradlew shadowJar test
```

Test reports are generated at `build/reports/tests/test/index.html`.

### Publishing and Release

- **JitPack integration**: Configured via `jitpack.yml` (requires Java 21)
- **GitHub Actions**: `.github/workflows/build.yml` runs on every push/PR
  - Builds shadow JAR and runs tests
  - Creates GitHub releases on tag push (`refs/tags/*`)
  - Uploads JAR artifacts automatically
- **Shadow JAR**: Regular JAR is disabled (`tasks.jar { enabled = false }`), only shadow JAR is published
- **Maven publishing**: Uses `maven-publish` plugin with shadow component

### Adding New API Endpoints

1. Define Retrofit interface in appropriate `*Api.kt` file
2. Add corresponding data models in `model/` folder with nullable fields where appropriate
3. Expose the API as a lazy property in `ChzzkClient` if it's a new domain
4. Create integration test in `test/integration/` using MockWebServer
5. Use suspend functions for all API calls (coroutine-native)

Example from `ChannelApi.kt`:

```kotlin
@GET("/open/v1/channels")
suspend fun getChannels(
    @Query("channelIds") channelIds: List<String>
): ChannelListResponse
```

### Common Patterns

- **Pagination**: Use `@Query` parameters for `page`/`size` or `next`/`cursor` tokens
  - Page-based: `getFollowers(page = 0, size = 30)`
  - Cursor-based: `getLives(size = 20, next = "cursor-token")`
- **Request bodies**: Define dedicated request data classes (e.g., `UpdateLiveSettingRequest`)
- **Authentication requirements**: Document in KDoc whether endpoint needs Client auth or Access Token
- **HTTP methods with body**: Use `@HTTP(method = "DELETE", hasBody = true)` for DELETE with request body
  - Example: `removeRestrictedChannel` in `ChannelApi.kt`
- **Nullable fields**: All optional/potentially-missing API fields use nullable types (`String?`, `Int?`)
- **Enums for constants**: Use sealed enums for API constants (e.g., `CategoryType.GAME`, `UserRole.STREAMING_CHANNEL_MANAGER`)

## Key Conventions

### Dependency Management

- All versions centralized in `gradle/libs.versions.toml`
- Use version catalogs: `libs.bundles.retrofit`, `libs.kotlinx.coroutines.core`
- Bundles group related dependencies (logging, moshi, okhttp, retrofit)

### Kotlin Style

- Kotlin DSL for Gradle (`build.gradle.kts`, `settings.gradle.kts`)
- JVM toolchain set to Java 21
- Data classes for immutable models
- Suspend functions for async operations (no callbacks/RxJava)

### Naming

- API interfaces end with `Api` suffix
- Response types end with `Response` suffix
- Request types end with `Request` suffix
- Package structure mirrors API organization: `org.mokokomc.chzzk.{api,auth,model}`

## Critical Implementation Details

### Authentication Flow

The builder enforces at least one auth method:

```kotlin
require(clientId != null || accessToken != null) {
    "Either clientAuth or accessToken must be provided"
}
```

### Retrofit Configuration

- Base URL: `https://openapi.chzzk.naver.com` (configurable)
- JSON serialization: Moshi with `KotlinJsonAdapterFactory` for data class support
- Default timeouts: 30 seconds (connect/read/write)
- Content-Type: `application/json` added automatically by interceptors

### Test Isolation

Integration tests use MockWebServer with setup/teardown lifecycle:

```kotlin
@BeforeEach fun setup() { mockWebServer.start() }
@AfterEach fun tearDown() { mockWebServer.close() }
```

Each test enqueues mock responses with `mockWebServer.enqueue(MockResponse())` and verifies requests/responses.

## External Dependencies

- **Retrofit 3.0.0**: HTTP client abstraction
- **OkHttp 5.3.2**: Underlying HTTP client and interceptors
- **Moshi 1.15.2**: JSON serialization with Kotlin support
- **Kotlin Coroutines 1.10.2**: Async/await primitives
- **JUnit 5 + kotlin-test**: Testing framework
