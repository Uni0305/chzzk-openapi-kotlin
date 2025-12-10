package org.mokokomc.chzzk.api

import org.mokokomc.chzzk.model.ChatSettings
import org.mokokomc.chzzk.model.UpdateChatSettingsRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT

/**
 * Chat API service
 */
interface ChatApi {

    /**
     * Get chat settings
     * Requires Access Token authentication
     */
    @GET("/open/v1/chats/settings")
    suspend fun getChatSettings(): ChatSettings

    /**
     * Update chat settings
     * Requires Access Token authentication
     */
    @PUT("/open/v1/chats/settings")
    suspend fun updateChatSettings(
        @Body request: UpdateChatSettingsRequest
    )
}

