package org.mokokomc.chzzk.test.integration

import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mokokomc.chzzk.ChzzkClient
import org.mokokomc.chzzk.model.ChatAvailableCondition
import org.mokokomc.chzzk.model.ChatAvailableGroup
import org.mokokomc.chzzk.model.UpdateChatSettingsRequest
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Integration tests for Chat API
 */
@DisplayName("Chat API Integration Tests")
class ChatApiIntegrationTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var client: ChzzkClient

    @BeforeEach
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        client = ChzzkClient.builder()
            .accessToken("test-access-token")
            .baseUrl(mockWebServer.url("/").toString())
            .build()
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.close()
    }

    @Test
    @DisplayName("Should retrieve current chat settings")
    fun shouldRetrieveCurrentChatSettings() = runTest {
        val responseBody = """
            {
                "chatAvailableCondition": "NONE",
                "chatAvailableGroup": "ALL",
                "minFollowerMinute": 0,
                "allowSubscriberInFollowerMode": false
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(responseBody)
                .addHeader("Content-Type", "application/json")
        )

        val response = client.chat.getChatSettings()

        assertNotNull(response)
        assertEquals(ChatAvailableCondition.NONE, response.chatAvailableCondition)
        assertEquals(ChatAvailableGroup.ALL, response.chatAvailableGroup)
        assertEquals(0, response.minFollowerMinute)
        assertEquals(false, response.allowSubscriberInFollowerMode)
    }

    @Test
    @DisplayName("Should successfully update chat settings")
    fun shouldUpdateChatSettings() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
        )

        client.chat.updateChatSettings(
            UpdateChatSettingsRequest(
                chatAvailableCondition = ChatAvailableCondition.REAL_NAME,
                chatAvailableGroup = ChatAvailableGroup.FOLLOWER,
                minFollowerMinute = 30,
                allowSubscriberInFollowerMode = true,
                chatSlowModeSec = 10,
                chatEmojiMode = true
            )
        )

        val request = mockWebServer.takeRequest()
        assertEquals("PUT", request.method)
        assertEquals("/open/v1/chats/settings", request.path)
    }

    @Test
    @DisplayName("Should handle chat settings with follower restrictions")
    fun shouldHandleChatSettingsWithFollowerRestrictions() = runTest {
        val responseBody = """
            {
                "chatAvailableCondition": "REAL_NAME",
                "chatAvailableGroup": "FOLLOWER",
                "minFollowerMinute": 30,
                "allowSubscriberInFollowerMode": true
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(responseBody)
                .addHeader("Content-Type", "application/json")
        )

        val response = client.chat.getChatSettings()

        assertNotNull(response)
        assertEquals(ChatAvailableCondition.REAL_NAME, response.chatAvailableCondition)
        assertEquals(ChatAvailableGroup.FOLLOWER, response.chatAvailableGroup)
        assertEquals(30, response.minFollowerMinute)
        assertEquals(true, response.allowSubscriberInFollowerMode)
    }
}

