package org.mokokomc.chzzk.model


/**
 * Drops reward claim information
 */
data class DropsClaim(
    val claimId: String,
    val campaignId: String,
    val rewardId: String,
    val categoryId: String,
    val categoryName: String,
    val channelId: String,
    val fulfillmentState: FulfillmentState,
    val claimedDate: String,
    val updatedDate: String
)

/**
 * Fulfillment state
 */
enum class FulfillmentState {
    CLAIMED,
    FULFILLED
}

/**
 * Response for drops reward claims
 */
data class DropsClaimListResponse(
    val data: List<DropsClaim>?,
    val page: CursorPage?
)

/**
 * Request to update drops reward claim
 */
data class UpdateDropsClaimRequest(
    val claimId: String,
    val fulfillmentState: FulfillmentState
)

/**
 * Request to fulfill multiple drops claims
 */
data class FulfillDropsClaimRequest(
    val claimIds: List<String>,
    val fulfillmentState: FulfillmentState
)

/**
 * Fulfillment status result
 */
data class FulfillmentStatusResult(
    val status: FulfillmentStatus,
    val ids: List<String>
)

/**
 * Fulfillment status
 */
enum class FulfillmentStatus {
    INVALID_ID,
    NOT_FOUND,
    SUCCESS,
    UNAUTHORIZED,
    UPDATE_FAILED
}

/**
 * Response for drops fulfillment
 */
data class DropsFulfillmentResponse(
    val data: List<FulfillmentStatusResult>
)

