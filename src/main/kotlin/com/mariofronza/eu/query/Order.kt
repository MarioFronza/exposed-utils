package com.mariofronza.eu.query

import com.mariofronza.eu.query.pagination.Order
import com.mariofronza.eu.query.pagination.Sort
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.SizedIterable
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.Table

/**
 * Maps a nullable [Order] to a [SortOrder].
 *
 * @return [SortOrder.ASC] if [Order] is null or [Order.ASC], [SortOrder.DESC] if [Order] is [Order.DESC].
 */
fun Order?.order() = when (this ?: Order.ASC) {
    Order.ASC -> SortOrder.ASC
    Order.DESC -> SortOrder.DESC
}

/**
 * Order a [SizedIterable] based on a [Sort] object, using default sorting columns if necessary.
 *
 * @param table The database table.
 * @param sort The sort criteria.
 * @param default Default sorting columns and order.
 * @return A [SizedIterable] with the specified sorting applied.
 */
fun <T> SizedIterable<T>.order(table: Table, sort: Sort?, default: Pair<Column<*>, Order>) =
    order(table.columns, sort, default)

/**
 * Order a [SizedIterable] based on a [Sort] object and additional columns, using default sorting columns if necessary.
 *
 * @param table The database table.
 * @param columns Additional columns to consider for sorting.
 * @param sort The sort criteria.
 * @param default Default sorting columns and order.
 * @return A [SizedIterable] with the specified sorting applied.
 */
fun <T> SizedIterable<T>.order(table: Table, columns: List<Column<*>>, sort: Sort?, default: Pair<Column<*>, Order>) =
    order(table.columns + columns, sort, default)

/**
 * Order a [SizedIterable] based on a list of columns and a [Sort] object, using default sorting columns if necessary.
 *
 * @param columns The columns to consider for sorting.
 * @param sort The sort criteria.
 * @param default Default sorting columns and order.
 * @return A [SizedIterable] with the specified sorting applied.
 */
fun <T> SizedIterable<T>.order(
    columns: List<Column<*>>,
    sort: Sort?,
    default: Pair<Column<*>, Order>
): SizedIterable<T> {
    val column = sort?.by.let { by ->
        columns.find { it.name == by } ?: default.first
    }
    val order = (sort?.order ?: default.second).order()
    return orderBy(column to order)
}
