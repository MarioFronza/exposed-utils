package com.mariofronza.eu.query.pagination

import java.util.Date

/**
 * Represents pagination settings for querying a list of items.
 *
 * @property itemsPerPage The number of items per page.
 * @property page The page number (0-based).
 * @property search Optional search query.
 * @property period Optional time period filter.
 * @property sort Optional sorting criteria.
 */
data class Pagination(
    val itemsPerPage: Int,
    val page: Int,
    val search: String? = null,
    val period: Period? = null,
    val sort: Sort? = null
) {
    /**
     * Calculates the offset based on the current page and items per page.
     */
    fun offset() = page * itemsPerPage.toLong()
}

/**
 * Represents a time period filter.
 *
 * @property from The start date of the period.
 * @property to The end date of the period.
 */
data class Period(
    val from: Date,
    val to: Date
)

/**
 * Represents the result of a paginated query.
 *
 * @property items The list of items for the current page.
 * @property total The total number of items in the query result.
 */
open class PaginationResult<T>(
    val items: List<T>,
    val total: Long
)
