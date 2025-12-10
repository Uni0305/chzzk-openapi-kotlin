# Chzzk OpenAPI Kotlin Client

[![GitHub Actions Workflow Status](https://img.shields.io/github/actions/workflow/status/Uni0305/chzzk-openapi-kotlin/build.yml?style=for-the-badge)](https://github.com/Uni0305/chzzk-openapi-kotlin/actions)
[![GitHub License](https://img.shields.io/github/license/Uni0305/chzzk-openapi-kotlin?style=for-the-badge)](LICENSE.md)
[![GitHub Release](https://img.shields.io/github/v/release/Uni0305/chzzk-openapi-kotlin?style=for-the-badge)](https://github.com/Uni0305/chzzk-openapi-kotlin/releases)
[![JitPack](https://img.shields.io/jitpack/version/com.github.Uni0305/chzzk-openapi-kotlin?style=for-the-badge)](https://jitpack.io/#Uni0305/chzzk-openapi-kotlin)

네이버 치지직(Chzzk) 스트리밍 플랫폼의 OpenAPI를 위한 Kotlin SDK입니다. Retrofit, OkHttp, Moshi를 기반으로 타입 안전하고 코루틴 기반의 HTTP 클라이언트를 제공합니다.

## 주요 기능

- 🔐 **이중 인증 모드**: Client 인증(공개 API)과 Access Token 인증(사용자 인증) 지원
- ⚡ **코루틴 기반**: 모든 API 호출이 suspend function으로 구현되어 비동기 처리 최적화
- 🎯 **타입 안전**: Kotlin 데이터 클래스로 모든 응답/요청 타입 정의
- 🏗️ **Fluent Builder 패턴**: 직관적인 클라이언트 설정
- 🔧 **커스터마이징 가능**: 타임아웃, 베이스 URL, OkHttpClient 설정 가능
- 📦 **5개 API 도메인**: Channel, Live, Chat, Drops, Session API 지원

## 요구사항

- Kotlin 2.2+
- Java 21
- Gradle 8.0+

## 설치

### JitPack을 통한 설치 (권장)

JitPack을 사용하면 GitHub 리포지토리에서 직접 라이브러리를 의존성으로 추가할 수 있습니다.

#### Gradle (Kotlin DSL)

`settings.gradle.kts` 또는 `build.gradle.kts`에 JitPack 저장소를 추가:

```kotlin
repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}
```

의존성 추가:

```kotlin
dependencies {
    implementation("com.github.Uni0305:chzzk-openapi-kotlin:VERSION")
}
```

#### Gradle (Groovy)

`build.gradle`에 JitPack 저장소를 추가:

```groovy
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}
```

의존성 추가:

```groovy
dependencies {
    implementation 'com.github.Uni0305:chzzk-openapi-kotlin:VERSION'
}
```

#### Maven

`pom.xml`에 JitPack 저장소를 추가:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

의존성 추가:

```xml
<dependency>
    <groupId>com.github.Uni0305</groupId>
    <artifactId>chzzk-openapi-kotlin</artifactId>
    <version>VERSION</version>
</dependency>
```

### 버전 지정 방법

JitPack에서는 다양한 방식으로 버전을 지정할 수 있습니다:

- **릴리즈 태그**: `v1.0.0` - 안정적인 버전 사용 (권장)
- **특정 커밋**: `a1b2c3d` - 특정 커밋 해시 사용
- **브랜치 최신**: `master-SNAPSHOT` - 최신 개발 버전 (개발용)

예시:

```kotlin
implementation("com.github.Uni0305:chzzk-openapi-kotlin:main-SNAPSHOT")
```

> **참고**: 최초 빌드 시 JitPack이 프로젝트를 빌드하므로 다운로드에 시간이 걸릴 수 있습니다. 안정적인 릴리즈 태그 사용을 권장합니다.

### 로컬 빌드 (개발자용)

프로젝트를 직접 수정하거나 기여하려는 경우 로컬에서 빌드할 수 있습니다:

```bash
git clone https://github.com/Uni0305/chzzk-openapi-kotlin.git
cd chzzk-openapi-kotlin
./gradlew build
```

#### Fat JAR 빌드

의존성이 포함된 단일 JAR 파일을 빌드하려면:

```bash
./gradlew shadowJar
```

빌드된 JAR는 `build/libs/chzzk-openapi-kotlin-1.0-SNAPSHOT-all.jar`에 생성됩니다.

## 빠른 시작

### Client 인증 (공개 API)

Client ID와 Client Secret을 사용하여 공개 API에 접근:

```kotlin
import org.mokokomc.chzzk.ChzzkClient
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    // 클라이언트 생성
    val client = ChzzkClient.builder()
        .clientAuth("your-client-id", "your-client-secret")
        .build()

    // 채널 정보 조회
    val response = client.channel.getChannels(listOf("channel-id-1", "channel-id-2"))
    response.data?.forEach { channel ->
        println("채널명: ${channel.channelName}, 팔로워: ${channel.followerCount}")
    }
}
```

### Access Token 인증 (사용자 인증)

Bearer 토큰을 사용하여 사용자 인증이 필요한 API에 접근:

```kotlin
val client = ChzzkClient.builder()
    .accessToken("your-access-token")
    .build()

// 스트림 키 조회
val streamKey = client.live.getStreamKey()
println("스트림 키: ${streamKey.streamKey}")

// 채팅 설정 조회
val chatSettings = client.chat.getChatSettings()
println("채팅 가능 조건: ${chatSettings.chatAvailableCondition}")
```

## API 가이드

### Channel API

채널 정보, 팔로워, 구독자, 제한 채널 관리 기능을 제공합니다.

#### 채널 정보 조회 (Client 인증 필요)

```kotlin
val response = client.channel.getChannels(listOf("channelId1", "channelId2"))
response.data?.forEach { channel ->
    println("""
        채널 ID: ${channel.channelId}
        채널명: ${channel.channelName}
        팔로워 수: ${channel.followerCount}
        인증 마크: ${channel.verifiedMark}
    """.trimIndent())
}
```

#### 팔로워 목록 조회 (Access Token 필요)

```kotlin
// 첫 번째 페이지 조회 (기본: 30개)
val followers = client.channel.getFollowers(page = 0, size = 30)
println("총 팔로워: ${followers.totalElements}")

followers.content?.forEach { follower ->
    println("${follower.followerChannelName} (${follower.followDate})")
}

// 다음 페이지 조회
if (!followers.last) {
    val nextPage = client.channel.getFollowers(page = 1, size = 30)
    // ...
}
```

#### 구독자 목록 조회 (Access Token 필요)

```kotlin
val subscribers = client.channel.getSubscribers(
    page = 0,
    size = 30,
    sort = "subscribeDate,desc" // 구독일 내림차순
)

subscribers.content?.forEach { subscriber ->
    println("""
        구독자: ${subscriber.subscriberChannelName}
        등급: ${subscriber.subscribeTierName}
        구독일: ${subscriber.subscribeDate}
    """.trimIndent())
}
```

#### 제한 채널 관리 (Access Token 필요)

```kotlin
// 제한 채널 목록 조회
val restricted = client.channel.getRestrictedChannels(size = 50)
restricted.data?.forEach { channel ->
    println("제한된 채널: ${channel.channelName}")
}

// 채널 제한 추가
client.channel.addRestrictedChannel(
    RestrictChannelRequest(channelId = "bad-channel-id")
)

// 채널 제한 해제
client.channel.removeRestrictedChannel(
    RestrictChannelRequest(channelId = "bad-channel-id")
)
```

### Live API

라이브 스트림 관리, 스트림 키 조회, 라이브 설정 기능을 제공합니다.

#### 라이브 스트림 목록 조회 (Client 인증 필요)

```kotlin
// 첫 번째 페이지 조회
val lives = client.live.getLives(size = 20)

lives.data?.forEach { live ->
    println("""
        채널: ${live.channelName}
        제목: ${live.liveTitle}
        시청자 수: ${live.concurrentUserCount}
        카테고리: ${live.categoryValue}
    """.trimIndent())
}

// 커서 기반 페이지네이션으로 다음 페이지 조회
lives.page?.next?.let { nextCursor ->
    val nextPage = client.live.getLives(size = 20, next = nextCursor)
    // ...
}
```

#### 스트림 키 조회 (Access Token 필요)

```kotlin
val streamKey = client.live.getStreamKey()
println("""
    스트림 키: ${streamKey.streamKey}
    스트림 URL: ${streamKey.streamUrl}
""".trimIndent())
```

#### 라이브 설정 관리 (Access Token 필요)

```kotlin
// 현재 설정 조회
val settings = client.live.getLiveSetting()
println("""
    제목: ${settings.liveTitle}
    카테고리: ${settings.categoryValue}
    성인 콘텐츠: ${settings.adult}
""".trimIndent())

// 설정 업데이트
client.live.updateLiveSetting(
    UpdateLiveSettingRequest(
        liveTitle = "새로운 라이브 제목",
        categoryType = CategoryType.GAME,
        categoryId = "category-id",
        adult = false
    )
)
```

### Chat API

채팅 설정 조회 및 관리 기능을 제공합니다.

#### 채팅 설정 조회 및 수정 (Access Token 필요)

```kotlin
// 현재 채팅 설정 조회
val chatSettings = client.chat.getChatSettings()
println("""
    채팅 가능 조건: ${chatSettings.chatAvailableCondition}
    채팅 가능 그룹: ${chatSettings.chatAvailableGroup}
    채팅 딜레이: ${chatSettings.chatDonationRankingExposure}
""".trimIndent())

// 채팅 설정 업데이트
client.chat.updateChatSettings(
    UpdateChatSettingsRequest(
        chatAvailableCondition = ChatAvailableCondition.FOLLOWER,
        chatAvailableGroup = ChatAvailableGroup.FOLLOWER
    )
)
```

### Drops API

드롭스(보상) 관리 및 이행 기능을 제공합니다.

#### 드롭스 클레임 목록 조회 (Access Token 필요)

```kotlin
val claims = client.drops.getDropsClaims(
    size = 50,
    next = null // 첫 페이지
)

claims.data?.forEach { claim ->
    println("""
        클레임 ID: ${claim.claimId}
        사용자: ${claim.userIdHash}
        상태: ${claim.fulfillmentState}
        생성일: ${claim.createdDate}
    """.trimIndent())
}
```

#### 드롭스 클레임 업데이트 (Access Token 필요)

```kotlin
client.drops.updateDropsClaim(
    UpdateDropsClaimRequest(
        claimIds = listOf("claim-id-1", "claim-id-2"),
        fulfillmentState = FulfillmentState.FULFILLED
    )
)
```

#### 드롭스 이행 (Access Token 필요)

```kotlin
val result = client.drops.fulfillDropsClaim(
    FulfillDropsClaimRequest(
        claimIds = listOf("claim-id-1", "claim-id-2")
    )
)

result.data?.forEach { fulfillment ->
    println("클레임 ${fulfillment.claimId}: ${fulfillment.status}")
}
```

### Session API

WebSocket 세션 인증 및 이벤트 구독 기능을 제공합니다.

#### 세션 생성

```kotlin
// 사용자 인증 세션 (Access Token 필요)
val userSession = client.session.createUserSession()
println("""
    액세스 토큰: ${userSession.accessToken}
    만료 시간: ${userSession.extrapolatedTokenExpiration}
""".trimIndent())

// 클라이언트 인증 세션 (Client 인증 필요)
val clientSession = client.session.createClientSession()
println("세션 토큰: ${clientSession.accessToken}")
```

#### 세션 목록 조회 (Client 인증 필요)

```kotlin
val sessions = client.session.getSessions(size = 20, page = 0)
sessions.content?.forEach { session ->
    println("""
        세션 ID: ${session.sessionId}
        상태: ${session.sessionStatus}
        생성일: ${session.createdDate}
    """.trimIndent())
}
```

#### 채팅 이벤트 구독 (Access Token 필요)

```kotlin
client.session.subscribeToChatEvents(
    SubscribeEventRequest(
        subscribeEventTypes = listOf(
            EventType.CHAT,
            EventType.DONATION,
            EventType.SYSTEM_MESSAGE
        )
    )
)
```

## 고급 설정

### 타임아웃 설정

```kotlin
val client = ChzzkClient.builder()
    .clientAuth("client-id", "client-secret")
    .connectTimeout(60) // 연결 타임아웃 60초
    .readTimeout(60)    // 읽기 타임아웃 60초
    .writeTimeout(60)   // 쓰기 타임아웃 60초
    .build()
```

### 커스텀 OkHttpClient 사용

```kotlin
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

val loggingInterceptor = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}

val customHttpClient = OkHttpClient.Builder()
    .addInterceptor(loggingInterceptor)
    .build()

val client = ChzzkClient.builder()
    .clientAuth("client-id", "client-secret")
    .okHttpClient(customHttpClient)
    .build()
```

### 베이스 URL 변경

```kotlin
val client = ChzzkClient.builder()
    .clientAuth("client-id", "client-secret")
    .baseUrl("https://custom-api.example.com")
    .build()
```

## 에러 처리

API 호출 시 발생할 수 있는 에러를 처리하는 방법:

```kotlin
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val client = ChzzkClient.builder()
        .clientAuth("client-id", "client-secret")
        .build()

    try {
        val channels = client.channel.getChannels(listOf("invalid-id"))
        // 정상 처리
    } catch (e: Exception) {
        when (e) {
            is retrofit2.HttpException -> {
                println("HTTP 에러: ${e.code()} - ${e.message()}")
            }
            is java.net.SocketTimeoutException -> {
                println("타임아웃 에러")
            }
            else -> {
                println("알 수 없는 에러: ${e.message}")
            }
        }
    }
}
```

## 빌드 및 테스트

### 프로젝트 빌드

```bash
# 전체 빌드
./gradlew build

# 클린 빌드
./gradlew clean build

# Fat JAR 생성 (의존성 포함)
./gradlew shadowJar
```

### 테스트 실행

```bash
# 모든 테스트 실행
./gradlew test

# 특정 테스트 클래스만 실행
./gradlew test --tests "org.mokokomc.chzzk.test.ChzzkClientTest"

# 특정 테스트 메서드만 실행
./gradlew test --tests "*ChannelApiIntegrationTest.shouldRetrieveChannelInformation"
```

테스트 리포트는 `build/reports/tests/test/index.html`에서 확인할 수 있습니다.

### 테스트 구조

- **Unit Tests** (`src/test/kotlin/org/mokokomc/chzzk/test/ChzzkClientTest.kt`)

  - 클라이언트 빌더 설정 검증
  - 인증 설정 유효성 검사

- **Integration Tests** (`src/test/kotlin/org/mokokomc/chzzk/test/integration/`)
  - MockWebServer를 사용한 API 계약 테스트
  - 각 API별 통합 테스트 (ChannelApiIntegrationTest, LiveApiIntegrationTest 등)
  - 요청 헤더, 응답 파싱, 에러 처리 검증

## 기여하기

### 새 API 엔드포인트 추가

1. `src/main/kotlin/org/mokokomc/chzzk/api/` 디렉토리에 해당 API 인터페이스 수정
2. `src/main/kotlin/org/mokokomc/chzzk/model/` 디렉토리에 데이터 모델 추가
3. `ChzzkClient.kt`에 새 API가 속한 도메인을 lazy 프로퍼티로 노출 (새 도메인인 경우)
4. `src/test/kotlin/org/mokokomc/chzzk/test/integration/` 디렉토리에 통합 테스트 작성
5. 모든 API 메서드는 suspend function으로 작성

### 코드 스타일

- Kotlin 공식 코딩 컨벤션 준수
- API 인터페이스는 `Api` 접미사 사용
- 응답 타입은 `Response` 접미사 사용
- 요청 타입은 `Request` 접미사 사용
- 패키지 구조는 API 구조 반영: `org.mokokomc.chzzk.{api,auth,model}`

## 기술 스택

- **Kotlin** 2.2.21 - 주 프로그래밍 언어
- **Retrofit** 3.0.0 - HTTP 클라이언트 추상화
- **OkHttp** 5.3.2 - HTTP 클라이언트 및 인터셉터
- **Moshi** 1.15.2 - JSON 직렬화 (Kotlin 지원)
- **Kotlin Coroutines** 1.10.2 - 비동기 처리
- **JUnit 5** - 테스트 프레임워크
- **MockWebServer** - API 통합 테스트

## 라이선스

이 프로젝트는 [MIT 라이선스](LICENSE.md)에 따라 배포됩니다.

## 관련 링크

- [Chzzk OpenAPI 공식 문서](https://chzzk.gitbook.io/chzzk/)
- [Retrofit 공식 문서](https://square.github.io/retrofit/)
- [Kotlin Coroutines 가이드](https://kotlinlang.org/docs/coroutines-guide.html)
