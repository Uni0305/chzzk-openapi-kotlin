package org.mokokomc.chzzk.model


/**
 * Common response wrapper for Chzzk API responses
 */
data class ChzzkResponse<T>(
    val code: Int,
    val message: String?,
    val content: T?
)

/**
 * Simple response for success/error messages
 */
data class SimpleResponse(
    val message: String
)

/**
 * Pagination information
 */
data class Page(
    val next: String?
)

/**
 * Cursor-based pagination
 */
data class CursorPage(
    val cursor: String?
)

