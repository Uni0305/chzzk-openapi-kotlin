package org.mokokomc.chzzk.model


/**
 * Chat settings
 */
data class ChatSettings(
    val chatAvailableCondition: ChatAvailableCondition,
    val chatAvailableGroup: ChatAvailableGroup,
    val minFollowerMinute: Int?,
    val allowSubscriberInFollowerMode: Boolean?
)

/**
 * Chat available condition
 */
enum class ChatAvailableCondition {
    NONE,
    REAL_NAME
}

/**
 * Chat available group
 */
enum class ChatAvailableGroup {
    ALL,
    FOLLOWER,
    MANAGER,
    SUBSCRIBER
}

/**
 * Request to update chat settings
 */
data class UpdateChatSettingsRequest(
    val chatAvailableCondition: ChatAvailableCondition? = null,
    val chatAvailableGroup: ChatAvailableGroup? = null,
    val minFollowerMinute: Int? = null,
    val allowSubscriberInFollowerMode: Boolean? = null,
    val chatSlowModeSec: Int? = null,
    val chatEmojiMode: Boolean? = null
)

