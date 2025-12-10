package org.mokokomc.chzzk.api

import org.mokokomc.chzzk.model.LiveListResponse
import org.mokokomc.chzzk.model.LiveSetting
import org.mokokomc.chzzk.model.StreamKey
import org.mokokomc.chzzk.model.UpdateLiveSettingRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Query

/**
 * Live API service
 */
interface LiveApi {

    /**
     * Get list of currently active live streams
     * Requires Client authentication
     */
    @GET("/open/v1/lives")
    suspend fun getLives(
        @Query("size") size: Int = 20,
        @Query("next") next: String? = null
    ): LiveListResponse

    /**
     * Get stream key
     * Requires Access Token authentication
     */
    @GET("/open/v1/streams/key")
    suspend fun getStreamKey(): StreamKey

    /**
     * Get live settings
     * Requires Access Token authentication
     */
    @GET("/open/v1/lives/setting")
    suspend fun getLiveSetting(): LiveSetting

    /**
     * Update live settings
     * Requires Access Token authentication
     */
    @PATCH("/open/v1/lives/setting")
    suspend fun updateLiveSetting(
        @Body request: UpdateLiveSettingRequest
    )
}

