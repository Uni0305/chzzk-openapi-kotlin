package org.mokokomc.chzzk.test

import org.junit.jupiter.api.DisplayName
import org.mokokomc.chzzk.ChzzkClient
import kotlin.test.Test
import kotlin.test.assertNotNull

/**
 * Tests for ChzzkClient builder configurations
 */
@DisplayName("ChzzkClient Builder Configuration Tests")
class ChzzkClientTest {

    @Test
    @DisplayName("Should successfully build client with client authentication credentials")
    fun shouldBuildClientWithClientAuthCredentials() {
        val client = ChzzkClient.builder()
            .clientAuth("test-client-id", "test-client-secret")
            .build()

        assertNotNull(client)
        assertNotNull(client.channel)
        assertNotNull(client.live)
        assertNotNull(client.chat)
        assertNotNull(client.drops)
        assertNotNull(client.session)
    }

    @Test
    @DisplayName("Should successfully build client with access token authentication")
    fun shouldBuildClientWithAccessTokenAuthentication() {
        val client = ChzzkClient.builder()
            .accessToken("test-access-token")
            .build()

        assertNotNull(client)
        assertNotNull(client.channel)
        assertNotNull(client.live)
        assertNotNull(client.chat)
        assertNotNull(client.drops)
        assertNotNull(client.session)
    }

    @Test
    @DisplayName("Should build client with all custom configuration options")
    fun shouldBuildClientWithCustomConfiguration() {
        val client = ChzzkClient.builder()
            .clientAuth("test-client-id", "test-client-secret")
            .baseUrl("https://custom-url.com")
            .connectTimeout(60)
            .readTimeout(60)
            .writeTimeout(60)
            .build()

        assertNotNull(client)
    }

    @Test
    @DisplayName("Should throw exception when building without authentication credentials")
    fun shouldThrowExceptionWhenMissingAuthenticationCredentials() {
        try {
            ChzzkClient.builder().build()
            throw AssertionError("Should have thrown exception")
        } catch (e: IllegalArgumentException) {
            // Expected
        }
    }
}
