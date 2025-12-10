package org.mokokomc.chzzk.auth

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor for Client authentication
 * Adds Client-Id and Client-Secret headers to requests
 */
class ClientAuthInterceptor(
    private val clientId: String,
    private val clientSecret: String
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Client-Id", clientId)
            .addHeader("Client-Secret", clientSecret)
            .addHeader("Content-Type", "application/json")
            .build()

        return chain.proceed(request)
    }
}

/**
 * Interceptor for Access Token authentication
 * Adds Authorization header with Bearer token to requests
 */
class AccessTokenInterceptor(
    private val accessToken: String
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeader("Content-Type", "application/json")
            .build()

        return chain.proceed(request)
    }
}

