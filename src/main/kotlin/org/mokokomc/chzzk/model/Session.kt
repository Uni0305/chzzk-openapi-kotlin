package org.mokokomc.chzzk.model


/**
 * Session authentication response
 */
data class SessionAuth(
    val url: String,
    val sessionKey: String
)

/**
 * Session information
 */
data class Session(
    val sessionId: String,
    val sessionKey: String,
    val status: SessionStatus,
    val createdDate: String,
    val connectedDate: String?,
    val disconnectedDate: String?
)

/**
 * Session status
 */
enum class SessionStatus {
    CREATED,
    CONNECTED,
    DISCONNECTED
}

/**
 * Response for session list
 */
data class SessionListResponse(
    val data: List<Session>?,
    val page: Page?
)

/**
 * Request to subscribe to events
 */
data class SubscribeEventRequest(
    val sessionKey: String
)

/**
 * Event subscription types
 */
enum class EventType {
    CHAT,
    DONATION,
    SUBSCRIPTION
}

