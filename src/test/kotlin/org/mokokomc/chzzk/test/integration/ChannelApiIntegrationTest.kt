package org.mokokomc.chzzk.test.integration

import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mokokomc.chzzk.ChzzkClient
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Integration tests for Channel API with Client Authentication
 */
@DisplayName("Channel API Integration Tests")
class ChannelApiIntegrationTest {

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
    @DisplayName("Should successfully retrieve channel information")
    fun shouldRetrieveChannelInformation() = runTest {
        val responseBody = """
            {
                "data": [
                    {
                        "channelId": "channel1",
                        "channelName": "Test Channel",
                        "channelImageUrl": "https://example.com/image.jpg",
                        "followerCount": 1000,
                        "verifiedMark": true
                    }
                ]
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(responseBody)
                .addHeader("Content-Type", "application/json")
        )

        val response = client.channel.getChannels(listOf("channel1"))

        assertNotNull(response.data)
        assertEquals(1, response.data.size)
        assertEquals("Test Channel", response.data[0].channelName)
        assertEquals(1000, response.data[0].followerCount)
        assertEquals(response.data[0].verifiedMark, true)
    }

    @Test
    @DisplayName("Should retrieve streaming roles for channel administrators")
    fun shouldRetrieveStreamingRoles() = runTest {
        val responseBody = """
            {
                "data": [
                    {
                        "managerChannelId": "manager1",
                        "managerChannelName": "Manager Name",
                        "userRole": "STREAMING_CHANNEL_MANAGER",
                        "createdDate": "2023-10-27T10:00:00Z"
                    }
                ]
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(responseBody)
                .addHeader("Content-Type", "application/json")
        )

        val response = client.channel.getStreamingRoles()

        assertNotNull(response.data)
        assertEquals(1, response.data.size)
        assertEquals("Manager Name", response.data[0].managerChannelName)
    }

    @Test
    @DisplayName("Should retrieve channel followers with pagination")
    fun shouldRetrieveChannelFollowers() = runTest {
        val responseBody = """
            {
                "data": [
                    {
                        "channelId": "follower1",
                        "channelName": "Follower Name",
                        "createdDate": "2023-10-27T10:00:00Z"
                    }
                ]
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(responseBody)
                .addHeader("Content-Type", "application/json")
        )

        val response = client.channel.getFollowers(page = 0, size = 30)

        assertNotNull(response.data)
        assertEquals(1, response.data.size)
        assertEquals("Follower Name", response.data[0].channelName)
    }

    @Test
    @DisplayName("Should retrieve channel subscribers with sorting")
    fun shouldRetrieveChannelSubscribers() = runTest {
        val responseBody = """
            {
                "data": [
                    {
                        "channelId": "subscriber1",
                        "channelName": "Subscriber Name",
                        "month": 6,
                        "tierNo": 1,
                        "createdDate": "2023-10-27T10:00:00Z"
                    }
                ]
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(responseBody)
                .addHeader("Content-Type", "application/json")
        )

        val response = client.channel.getSubscribers(page = 0, size = 30, sort = "RECENT")

        assertNotNull(response.data)
        assertEquals(1, response.data.size)
        assertEquals("Subscriber Name", response.data[0].channelName)
        assertEquals(6, response.data[0].month)
        assertEquals(1, response.data[0].tierNo)
    }

    @Test
    @DisplayName("Should retrieve restricted channels list")
    fun shouldRetrieveRestrictedChannels() = runTest {
        val responseBody = """
            {
                "data": [
                    {
                        "restrictedChannelId": "restricted1",
                        "restrictedChannelName": "Restricted Channel",
                        "createdDate": "2023-10-27T10:00:00Z",
                        "releaseDate": null
                    }
                ]
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(responseBody)
                .addHeader("Content-Type", "application/json")
        )

        val response = client.channel.getRestrictedChannels(size = 20)

        assertNotNull(response.data)
        assertEquals(1, response.data.size)
        assertEquals("Restricted Channel", response.data[0].restrictedChannelName)
    }

    @Test
    @DisplayName("Should successfully add channel to restriction list")
    fun shouldAddChannelToRestrictionList() = runTest {
        val responseBody = """
            {
                "message": "Successfully added restriction."
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(responseBody)
                .addHeader("Content-Type", "application/json")
        )

        val response = client.channel.addRestrictedChannel(
            org.mokokomc.chzzk.model.RestrictChannelRequest(targetChannelId = "channel-to-block")
        )

        assertNotNull(response.message)
        assertTrue(response.message.contains("Successfully"))
    }

    @Test
    @DisplayName("Should successfully remove channel from restriction list")
    fun shouldRemoveChannelFromRestrictionList() = runTest {
        val responseBody = """
            {
                "message": "Successfully removed restriction."
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(responseBody)
                .addHeader("Content-Type", "application/json")
        )

        val response = client.channel.removeRestrictedChannel(
            org.mokokomc.chzzk.model.RestrictChannelRequest(targetChannelId = "channel-to-unblock")
        )

        assertNotNull(response.message)
        assertTrue(response.message.contains("Successfully"))
    }
}

