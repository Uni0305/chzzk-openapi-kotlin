package org.mokokomc.chzzk.api

import org.mokokomc.chzzk.model.SessionAuth
import org.mokokomc.chzzk.model.SessionListResponse
import org.mokokomc.chzzk.model.SimpleResponse
import org.mokokomc.chzzk.model.SubscribeEventRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Session API service
 */
interface SessionApi {

    /**
     * Create session with user authentication (Access Token)
     * Requires Access Token authentication
     */
    @GET("/open/v1/sessions/auth")
    suspend fun createUserSession(): SessionAuth

    /**
     * Create session with client authentication
     * Requires Client authentication
     */
    @GET("/open/v1/sessions/auth/client")
    suspend fun createClientSession(): SessionAuth

    /**
     * Get list of sessions (client authentication)
     * Requires Client authentication
     */
    @GET("/open/v1/sessions")
    suspend fun getSessions(
        @Query("size") size: Int = 20,
        @Query("page") page: Int = 0
    ): SessionListResponse

    /**
     * Subscribe to chat events
     * Requires Access Token authentication
     */
    @POST("/open/v1/sessions/events/subscribe/chat")
    suspend fun subscribeToChatEvents(
        @Body request: SubscribeEventRequest
    ): SimpleResponse

    /**
     * Subscribe to donation events
     * Requires Access Token authentication
     */
    @POST("/open/v1/sessions/events/subscribe/donation")
    suspend fun subscribeToDonationEvents(
        @Body request: SubscribeEventRequest
    ): SimpleResponse

    /**
     * Subscribe to subscription events
     * Requires Access Token authentication
     */
    @POST("/open/v1/sessions/events/subscribe/subscription")
    suspend fun subscribeToSubscriptionEvents(
        @Body request: SubscribeEventRequest
    ): SimpleResponse
}

