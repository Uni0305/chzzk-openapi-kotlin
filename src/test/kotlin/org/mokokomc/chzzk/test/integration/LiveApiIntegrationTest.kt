package org.mokokomc.chzzk.test.integration

import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mokokomc.chzzk.ChzzkClient
import org.mokokomc.chzzk.model.UpdateLiveSettingRequest
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Integration tests for Live API
 */
@DisplayName("Live API Integration Tests")
class LiveApiIntegrationTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var client: ChzzkClient

    @BeforeEach
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        client = ChzzkClient.builder()
            .clientAuth("test-client-id", "test-client-secret")
            .baseUrl(mockWebServer.url("/").toString())
            .build()
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.close()
    }

    @Test
    @DisplayName("Should retrieve list of active live streams")
    fun shouldRetrieveActiveLiveStreams() = runTest {
        val responseBody = """
            {
                "data": [
                    {
                        "liveId": 12345,
                        "liveTitle": "Test Live Stream",
                        "liveThumbnailImageUrl": "https://example.com/thumbnail.jpg",
                        "concurrentUserCount": 5000,
                        "openDate": "2023-10-27T10:00:00Z",
                        "adult": false,
                        "tags": ["gaming", "esports"],
                        "categoryType": "GAME",
                        "liveCategory": "FPS",
                        "liveCategoryValue": "First-Person Shooter",
                        "channelId": "channel123",
                        "channelName": "Test Channel",
                        "channelImageUrl": "https://example.com/channel.jpg"
                    }
                ],
                "page": {
                    "next": "nextCursor123"
                }
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(responseBody)
                .addHeader("Content-Type", "application/json")
        )

        val response = client.live.getLives(size = 10)

        assertNotNull(response.data)
        assertEquals(1, response.data.size)
        assertEquals("Test Live Stream", response.data[0].liveTitle)
        assertEquals(5000, response.data[0].concurrentUserCount)
        assertNotNull(response.page)
        assertEquals("nextCursor123", response.page.next)
    }

    @Test
    @DisplayName("Should retrieve stream key for broadcasting")
    fun shouldRetrieveStreamKey() = runTest {
        val responseBody = """
            {
                "streamKey": "test-stream-key-12345"
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(responseBody)
                .addHeader("Content-Type", "application/json")
        )

        val response = client.live.getStreamKey()

        assertNotNull(response.streamKey)
        assertEquals("test-stream-key-12345", response.streamKey)
    }

    @Test
    @DisplayName("Should retrieve current live settings")
    fun shouldRetrieveCurrentLiveSettings() = runTest {
        val responseBody = """
            {
                "defaultLiveTitle": "My Stream",
                "category": {
                    "categoryType": "GAME",
                    "categoryId": "RPG",
                    "categoryValue": "Role-Playing Game",
                    "posterImageUrl": "https://example.com/poster.jpg"
                },
                "tags": ["rpg", "fantasy"]
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(responseBody)
                .addHeader("Content-Type", "application/json")
        )

        val response = client.live.getLiveSetting()

        assertNotNull(response.defaultLiveTitle)
        assertEquals("My Stream", response.defaultLiveTitle)
        assertNotNull(response.category)
        assertEquals("RPG", response.category.categoryId)
        assertNotNull(response.tags)
        assertEquals(2, response.tags.size)
    }

    @Test
    @DisplayName("Should successfully update live settings")
    fun shouldUpdateLiveSettings() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
        )

        client.live.updateLiveSetting(
            UpdateLiveSettingRequest(
                defaultLiveTitle = "Updated Stream Title",
                categoryType = "GAME",
                tags = listOf("gaming", "fun", "multiplayer")
            )
        )

        val request = mockWebServer.takeRequest()
        assertEquals("PATCH", request.method)
        assertEquals("/open/v1/lives/setting", request.path)
    }
}

