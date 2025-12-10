package org.mokokomc.chzzk.test.integration

import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mokokomc.chzzk.ChzzkClient
import org.mokokomc.chzzk.model.FulfillDropsClaimRequest
import org.mokokomc.chzzk.model.FulfillmentState
import org.mokokomc.chzzk.model.UpdateDropsClaimRequest
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Integration tests for Drops API
 */
@DisplayName("Drops API Integration Tests")
class DropsApiIntegrationTest {

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
    @DisplayName("Should retrieve drops reward claims with pagination")
    fun shouldRetrieveDropsRewardClaims() = runTest {
        val responseBody = """
            {
                "data": [
                    {
                        "claimId": "claim-id-1",
                        "campaignId": "campaign-id-1",
                        "rewardId": "reward-id-1",
                        "categoryId": "CATEGORY_CHZZK",
                        "categoryName": "치지직",
                        "channelId": "channel-id-1",
                        "fulfillmentState": "CLAIMED",
                        "claimedDate": "2024-08-01T09:20:26Z",
                        "updatedDate": "2024-08-01T09:20:26Z"
                    }
                ],
                "page": {
                    "cursor": "next-cursor-123"
                }
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(responseBody)
                .addHeader("Content-Type", "application/json")
        )

        val response = client.drops.getRewardClaims(size = 20)

        assertNotNull(response.data)
        assertEquals(1, response.data.size)
        assertEquals("claim-id-1", response.data[0].claimId)
        assertEquals(FulfillmentState.CLAIMED, response.data[0].fulfillmentState)
        assertNotNull(response.page)
        assertEquals("next-cursor-123", response.page.cursor)
    }

    @Test
    @DisplayName("Should successfully update single reward claim status")
    fun shouldUpdateSingleRewardClaimStatus() = runTest {
        val responseBody = """
            {
                "message": "Reward status updated successfully."
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(responseBody)
                .addHeader("Content-Type", "application/json")
        )

        val response = client.drops.updateRewardClaim(
            UpdateDropsClaimRequest(
                claimId = "claim-id-1",
                fulfillmentState = FulfillmentState.FULFILLED
            )
        )

        assertNotNull(response.message)
        assertEquals("Reward status updated successfully.", response.message)
    }

    @Test
    @DisplayName("Should successfully fulfill multiple reward claims")
    fun shouldFulfillMultipleRewardClaims() = runTest {
        val responseBody = """
            {
                "data": [
                    {
                        "status": "SUCCESS",
                        "ids": ["claim-id-1", "claim-id-2", "claim-id-3"]
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

        val response = client.drops.fulfillRewardClaims(
            FulfillDropsClaimRequest(
                claimIds = listOf("claim-id-1", "claim-id-2", "claim-id-3"),
                fulfillmentState = FulfillmentState.FULFILLED
            )
        )

        assertNotNull(response.data)
        assertEquals(1, response.data.size)
        assertEquals("SUCCESS", response.data[0].status.name)
        assertEquals(3, response.data[0].ids.size)
    }

    @Test
    @DisplayName("Should handle mixed fulfillment results with different statuses")
    fun shouldHandleMixedFulfillmentResults() = runTest {
        val responseBody = """
            {
                "data": [
                    {
                        "status": "SUCCESS",
                        "ids": ["claim-id-1", "claim-id-2"]
                    },
                    {
                        "status": "UNAUTHORIZED",
                        "ids": ["claim-id-3"]
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

        val response = client.drops.fulfillRewardClaims(
            FulfillDropsClaimRequest(
                claimIds = listOf("claim-id-1", "claim-id-2", "claim-id-3"),
                fulfillmentState = FulfillmentState.FULFILLED
            )
        )

        assertNotNull(response.data)
        assertEquals(2, response.data.size)
        assertEquals("SUCCESS", response.data[0].status.name)
        assertEquals(2, response.data[0].ids.size)
        assertEquals("UNAUTHORIZED", response.data[1].status.name)
        assertEquals(1, response.data[1].ids.size)
    }
}

