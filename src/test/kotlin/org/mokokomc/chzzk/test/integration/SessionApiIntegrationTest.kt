package org.mokokomc.chzzk.test.integration

import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mokokomc.chzzk.ChzzkClient
import org.mokokomc.chzzk.model.SubscribeEventRequest
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Integration tests for Session API
 */
@DisplayName("Session API Integration Tests")
class SessionApiIntegrationTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var clientWithToken: ChzzkClient
    private lateinit var clientWithAuth: ChzzkClient

    @BeforeEach
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        clientWithToken = ChzzkClient.builder()
            .accessToken("test-access-token")
            .baseUrl(mockWebServer.url("/").toString())
            .build()

        clientWithAuth = ChzzkClient.builder()
            .clientAuth("test-client-id", "test-client-secret")
            .baseUrl(mockWebServer.url("/").toString())
            .build()
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.close()
    }

    @Test
    @DisplayName("Should create user session with access token authentication")
    fun shouldCreateUserSessionWithAccessToken() = runTest {
        val responseBody = """
            {
                "url": "wss://example.com/socket",
                "sessionKey": "user-session-key-12345"
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(responseBody)
                .addHeader("Content-Type", "application/json")
        )

        val response = clientWithToken.session.createUserSession()

        assertNotNull(response.url)
        assertNotNull(response.sessionKey)
        assertTrue(response.url.startsWith("wss://"))
        assertEquals("user-session-key-12345", response.sessionKey)
    }

    @Test
    @DisplayName("Should create client session with client authentication")
    fun shouldCreateClientSessionWithClientAuth() = runTest {
        val responseBody = """
            {
                "url": "wss://example.com/socket",
                "sessionKey": "client-session-key-12345"
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(responseBody)
                .addHeader("Content-Type", "application/json")
        )

        val response = clientWithAuth.session.createClientSession()

        assertNotNull(response.url)
        assertNotNull(response.sessionKey)
        assertEquals("client-session-key-12345", response.sessionKey)
    }

    @Test
    @DisplayName("Should retrieve list of sessions with pagination")
    fun shouldRetrieveListOfSessions() = runTest {
        val responseBody = """
            {
                "data": [
                    {
                        "sessionId": "session-1",
                        "sessionKey": "session-key-1",
                        "status": "CONNECTED",
                        "createdDate": "2023-10-27T10:00:00Z",
                        "connectedDate": "2023-10-27T10:01:00Z",
                        "disconnectedDate": null
                    }
                ],
                "page": {
                    "next": "next-page-cursor"
                }
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(responseBody)
                .addHeader("Content-Type", "application/json")
        )

        val response = clientWithAuth.session.getSessions(size = 20, page = 0)

        assertNotNull(response.data)
        assertEquals(1, response.data.size)
        assertEquals("session-1", response.data[0].sessionId)
        assertEquals("CONNECTED", response.data[0].status.name)
    }

    @Test
    @DisplayName("Should successfully subscribe to chat events")
    fun shouldSubscribeToChatEvents() = runTest {
        val responseBody = """
            {
                "message": "Successfully subscribed to chat events."
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(responseBody)
                .addHeader("Content-Type", "application/json")
        )

        val response = clientWithToken.session.subscribeToChatEvents(
            SubscribeEventRequest(sessionKey = "test-session-key")
        )

        assertNotNull(response.message)
        assertTrue(response.message.contains("Successfully"))
        assertTrue(response.message.contains("chat"))
    }

    @Test
    @DisplayName("Should successfully subscribe to donation events")
    fun shouldSubscribeToDonationEvents() = runTest {
        val responseBody = """
            {
                "message": "Successfully subscribed to donation events."
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(responseBody)
                .addHeader("Content-Type", "application/json")
        )

        val response = clientWithToken.session.subscribeToDonationEvents(
            SubscribeEventRequest(sessionKey = "test-session-key")
        )

        assertNotNull(response.message)
        assertTrue(response.message.contains("Successfully"))
        assertTrue(response.message.contains("donation"))
    }

    @Test
    @DisplayName("Should successfully subscribe to subscription events")
    fun shouldSubscribeToSubscriptionEvents() = runTest {
        val responseBody = """
            {
                "message": "Successfully subscribed to subscription events."
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(responseBody)
                .addHeader("Content-Type", "application/json")
        )

        val response = clientWithToken.session.subscribeToSubscriptionEvents(
            SubscribeEventRequest(sessionKey = "test-session-key")
        )

        assertNotNull(response.message)
        assertTrue(response.message.contains("Successfully"))
        assertTrue(response.message.contains("subscription"))
    }

    @Test
    @DisplayName("Should handle complete session workflow with event subscriptions")
    fun shouldHandleCompleteSessionWorkflow() = runTest {
        // Create session
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("""{"url": "wss://example.com/socket", "sessionKey": "workflow-session-key"}""")
                .addHeader("Content-Type", "application/json")
        )

        val sessionAuth = clientWithToken.session.createUserSession()
        assertEquals("workflow-session-key", sessionAuth.sessionKey)

        // Subscribe to chat
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("""{"message": "Successfully subscribed to chat events."}""")
                .addHeader("Content-Type", "application/json")
        )

        val chatResponse = clientWithToken.session.subscribeToChatEvents(
            SubscribeEventRequest(sessionKey = sessionAuth.sessionKey)
        )
        assertTrue(chatResponse.message.contains("chat"))

        // Subscribe to donation
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("""{"message": "Successfully subscribed to donation events."}""")
                .addHeader("Content-Type", "application/json")
        )

        val donationResponse = clientWithToken.session.subscribeToDonationEvents(
            SubscribeEventRequest(sessionKey = sessionAuth.sessionKey)
        )
        assertTrue(donationResponse.message.contains("donation"))
    }
}

