package org.mokokomc.chzzk

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import org.mokokomc.chzzk.api.*
import org.mokokomc.chzzk.auth.AccessTokenInterceptor
import org.mokokomc.chzzk.auth.ClientAuthInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Main Chzzk OpenAPI client
 *
 * Example usage:
 * ```kotlin
 * // Client authentication
 * val client = ChzzkClient.builder()
 *     .clientAuth("your-client-id", "your-client-secret")
 *     .build()
 *
 * // Access token authentication
 * val client = ChzzkClient.builder()
 *     .accessToken("your-access-token")
 *     .build()
 * ```
 */
class ChzzkClient private constructor(
    private val retrofit: Retrofit
) {

    /**
     * Channel API - manage channels, followers, subscribers
     */
    val channel: ChannelApi by lazy { retrofit.create(ChannelApi::class.java) }

    /**
     * Live API - manage live streams and settings
     */
    val live: LiveApi by lazy { retrofit.create(LiveApi::class.java) }

    /**
     * Chat API - manage chat settings
     */
    val chat: ChatApi by lazy { retrofit.create(ChatApi::class.java) }

    /**
     * Drops API - manage drops rewards
     */
    val drops: DropsApi by lazy { retrofit.create(DropsApi::class.java) }

    /**
     * Session API - manage WebSocket sessions and event subscriptions
     */
    val session: SessionApi by lazy { retrofit.create(SessionApi::class.java) }

    companion object {
        private const val BASE_URL = "https://openapi.chzzk.naver.com"

        /**
         * Create a new builder for ChzzkClient
         */
        fun builder(): Builder = Builder()
    }

    /**
     * Builder for ChzzkClient
     */
    class Builder {
        private var clientId: String? = null
        private var clientSecret: String? = null
        private var accessToken: String? = null
        private var baseUrl: String = BASE_URL
        private var connectTimeout: Long = 30
        private var readTimeout: Long = 30
        private var writeTimeout: Long = 30
        private var okHttpClient: OkHttpClient? = null

        /**
         * Set Client ID and Client Secret for client authentication
         */
        fun clientAuth(clientId: String, clientSecret: String): Builder {
            this.clientId = clientId
            this.clientSecret = clientSecret
            return this
        }

        /**
         * Set Access Token for user authentication
         */
        fun accessToken(accessToken: String): Builder {
            this.accessToken = accessToken
            return this
        }

        /**
         * Set custom base URL (default: https://openapi.chzzk.naver.com)
         */
        fun baseUrl(baseUrl: String): Builder {
            this.baseUrl = baseUrl
            return this
        }

        /**
         * Set connection timeout in seconds (default: 30)
         */
        fun connectTimeout(seconds: Long): Builder {
            this.connectTimeout = seconds
            return this
        }

        /**
         * Set read timeout in seconds (default: 30)
         */
        fun readTimeout(seconds: Long): Builder {
            this.readTimeout = seconds
            return this
        }

        /**
         * Set write timeout in seconds (default: 30)
         */
        fun writeTimeout(seconds: Long): Builder {
            this.writeTimeout = seconds
            return this
        }

        /**
         * Set custom OkHttpClient
         */
        fun okHttpClient(client: OkHttpClient): Builder {
            this.okHttpClient = client
            return this
        }

        /**
         * Build the ChzzkClient instance
         */
        fun build(): ChzzkClient {
            require(clientId != null || accessToken != null) {
                "Either clientAuth or accessToken must be provided"
            }

            val httpClientBuilder = okHttpClient?.newBuilder() ?: OkHttpClient.Builder()

            // Add authentication interceptor
            when {
                accessToken != null -> {
                    httpClientBuilder.addInterceptor(AccessTokenInterceptor(accessToken!!))
                }
                clientId != null && clientSecret != null -> {
                    httpClientBuilder.addInterceptor(
                        ClientAuthInterceptor(clientId!!, clientSecret!!)
                    )
                }
            }

            // Set timeouts
            httpClientBuilder
                .connectTimeout(connectTimeout, TimeUnit.SECONDS)
                .readTimeout(readTimeout, TimeUnit.SECONDS)
                .writeTimeout(writeTimeout, TimeUnit.SECONDS)

            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(httpClientBuilder.build())
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()

            return ChzzkClient(retrofit)
        }
    }
}

