package com.github.eu.query

import com.github.eu.query.pagination.Pagination
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SizedIterable
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.and

/**
 * Query and retrieve entities with optional pagination.
 *
 * @param pagination The pagination settings (optional).
 * @return A [SizedIterable] of entities based on the provided pagination.
 */
fun <ID : Comparable<ID>, E : Entity<ID>> EntityClass<ID, E>.query(pagination: Pagination?) =
    (filters(pagination)?.let { find(it) } ?: all()).limit(pagination)

/**
 * Generate filters for pagination and search criteria.
 *
 * @param pagination The pagination settings (optional).
 * @param fields The searchable fields for filtering (optional).
 * @return An [Op<Boolean>] representing the generated filters, or null if no filters are generated.
 */
fun <ID : Comparable<ID>, E : Entity<ID>> EntityClass<ID, E>.filters(
    pagination: Pagination?,
    fields: List<SearchableFields> = SearchableFields.of(table)
): Op<Boolean>? {
    val filters = listOfNotNull(pagination?.search?.let { SqlExpressionBuilder.search(fields, it) }).toMutableList()
    return if (filters.isNotEmpty()) {
        var statement = filters.removeFirstOrNull()
        filters.forEach {
            statement = statement?.and(it)
        }
        return statement
    } else null
}

/**
 * Limit the number of entities based on pagination settings.
 *
 * @param pagination The pagination settings (optional).
 * @return A [SizedIterable] with the specified limit applied.
 */
fun <ID : Comparable<ID>, E : Entity<ID>> SizedIterable<E>.limit(pagination: Pagination?) =
    pagination?.let { limit(it.itemsPerPage, it.offset()) } ?: this
