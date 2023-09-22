package com.mariofronza.eu.query.pagination

/**
 * Represents sorting criteria.
 *
 * @property by The field to sort by.
 * @property order The sorting order (ASC or DESC).
 */
data class Sort(
    val by: String,
    val order: Order
)

/**
 * Enumeration for sorting order.
 */
enum class Order {
    ASC, DESC
}