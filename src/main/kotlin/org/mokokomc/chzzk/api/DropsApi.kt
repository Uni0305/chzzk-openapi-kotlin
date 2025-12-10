package org.mokokomc.chzzk.api

import org.mokokomc.chzzk.model.*
import retrofit2.http.*

/**
 * Drops API service
 */
interface DropsApi {

    /**
     * Get drops reward claims
     * Requires Client authentication and Drops API Scope
     */
    @GET("/open/v1/drops/reward-claims")
    suspend fun getRewardClaims(
        @Query("cursor") cursor: String? = null,
        @Query("size") size: Int? = null
    ): DropsClaimListResponse

    /**
     * Update drops reward claim fulfillment state
     * Requires Client authentication and Drops API Scope
     */
    @PUT("/open/v1/drops/reward-claims")
    suspend fun updateRewardClaim(
        @Body request: UpdateDropsClaimRequest
    ): SimpleResponse

    /**
     * Request fulfillment for multiple drops campaigns
     * Requires Client authentication and Drops API Scope
     */
    @POST("/campaigns/drops/fulfillment")
    suspend fun fulfillRewardClaims(
        @Body request: FulfillDropsClaimRequest
    ): DropsFulfillmentResponse
}

