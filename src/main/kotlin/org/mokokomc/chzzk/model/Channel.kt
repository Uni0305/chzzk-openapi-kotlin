package org.mokokomc.chzzk.model


/**
 * Channel information
 */
data class Channel(
    val channelId: String,
    val channelName: String,
    val channelImageUrl: String?,
    val followerCount: Int?,
    val verifiedMark: Boolean?
)

/**
 * Response for channel list
 */
data class ChannelListResponse(
    val data: List<Channel>?
)

/**
 * Channel administrator/manager information
 */
data class StreamingRole(
    val managerChannelId: String,
    val managerChannelName: String,
    val userRole: UserRole,
    val createdDate: String
)

/**
 * User roles in streaming channel
 */
enum class UserRole {
    STREAMING_CHANNEL_OWNER,
    STREAMING_CHANNEL_MANAGER,
    STREAMING_CHAT_MANAGER,
    STREAMING_SETTLEMENT_MANAGER
}

/**
 * Response for streaming roles list
 */
data class StreamingRoleListResponse(
    val data: List<StreamingRole>
)

/**
 * Follower information
 */
data class Follower(
    val channelId: String,
    val channelName: String,
    val createdDate: String
)

/**
 * Response for follower list
 */
data class FollowerListResponse(
    val data: List<Follower>
)

/**
 * Subscriber information
 */
data class Subscriber(
    val channelId: String,
    val channelName: String,
    val month: Int,
    val tierNo: Int,
    val createdDate: String
)

/**
 * Response for subscriber list
 */
data class SubscriberListResponse(
    val data: List<Subscriber>
)

/**
 * Restricted channel information
 */
data class RestrictedChannel(
    val restrictedChannelId: String,
    val restrictedChannelName: String,
    val createdDate: String,
    val releaseDate: String?
)

/**
 * Response for restricted channel list
 */
data class RestrictedChannelListResponse(
    val data: List<RestrictedChannel>?,
    val page: Page?
)

/**
 * Request to add/remove channel restriction
 */
data class RestrictChannelRequest(
    val targetChannelId: String
)

