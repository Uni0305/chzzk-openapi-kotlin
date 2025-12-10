package org.mokokomc.chzzk.api

import org.mokokomc.chzzk.model.*
import retrofit2.http.*

/**
 * Channel API service
 */
interface ChannelApi {

    /**
     * Get channel information for multiple channels
     * Requires Client authentication
     */
    @GET("/open/v1/channels")
    suspend fun getChannels(
        @Query("channelIds") channelIds: List<String>
    ): ChannelListResponse

    /**
     * Get channel administrators/managers
     * Requires Access Token authentication
     */
    @GET("/open/v1/channels/streaming-roles")
    suspend fun getStreamingRoles(): StreamingRoleListResponse

    /**
     * Get channel followers
     * Requires Access Token authentication
     */
    @GET("/open/v1/channels/followers")
    suspend fun getFollowers(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 30
    ): FollowerListResponse

    /**
     * Get channel subscribers
     * Requires Access Token authentication
     */
    @GET("/open/v1/channels/subscribers")
    suspend fun getSubscribers(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 30,
        @Query("sort") sort: String? = null
    ): SubscriberListResponse

    /**
     * Get restricted channels list
     * Requires Access Token authentication
     */
    @GET("/open/v1/restrict-channels")
    suspend fun getRestrictedChannels(
        @Query("size") size: Int? = null,
        @Query("next") next: String? = null
    ): RestrictedChannelListResponse

    /**
     * Add channel to restriction list
     * Requires Access Token authentication
     */
    @POST("/open/v1/restrict-channels")
    suspend fun addRestrictedChannel(
        @Body request: RestrictChannelRequest
    ): SimpleResponse

    /**
     * Remove channel from restriction list
     * Requires Access Token authentication
     */
    @HTTP(method = "DELETE", path = "/open/v1/restrict-channels", hasBody = true)
    suspend fun removeRestrictedChannel(
        @Body request: RestrictChannelRequest
    ): SimpleResponse
}

