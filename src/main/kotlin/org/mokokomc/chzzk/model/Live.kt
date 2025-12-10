package org.mokokomc.chzzk.model


/**
 * Live stream information
 */
data class Live(
    val liveId: Long,
    val liveTitle: String,
    val liveThumbnailImageUrl: String?,
    val concurrentUserCount: Int,
    val openDate: String,
    val adult: Boolean,
    val tags: List<String>?,
    val categoryType: CategoryType?,
    val liveCategory: String?,
    val liveCategoryValue: String?,
    val channelId: String,
    val channelName: String,
    val channelImageUrl: String?
)

/**
 * Response for live list
 */
data class LiveListResponse(
    val data: List<Live>?,
    val page: Page?
)

/**
 * Category types
 */
enum class CategoryType {
    GAME,
    SPORTS,
    ETC
}

/**
 * Live category information
 */
data class LiveCategory(
    val categoryType: CategoryType?,
    val categoryId: String?,
    val categoryValue: String?,
    val posterImageUrl: String?
)

/**
 * Live settings
 */
data class LiveSetting(
    val defaultLiveTitle: String?,
    val category: LiveCategory?,
    val tags: List<String>?
)

/**
 * Request to update live settings
 */
data class UpdateLiveSettingRequest(
    val defaultLiveTitle: String? = null,
    val categoryType: String? = null,
    val categoryId: String? = null,
    val tags: List<String>? = null
)

/**
 * Stream key information
 */
data class StreamKey(
    val streamKey: String
)

