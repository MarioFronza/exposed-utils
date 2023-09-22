package com.mariofronza.eu.query

import org.jetbrains.exposed.sql.Alias
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.TextColumnType
import org.jetbrains.exposed.sql.VarCharColumnType
import org.jetbrains.exposed.sql.or

/**
 * Represents searchable fields within database tables.
 *
 * @property table The database table.
 * @property columns The columns within the table that can be searched.
 */
class SearchableFields(
    val table: Table,
    val columns: List<Column<*>>
) {
    companion object {
        /**
         * Create searchable fields for a single table.
         *
         * @param table The database table.
         */
        fun of(table: Table) = of(listOf(table))

        /**
         * Create searchable fields for a list of tables.
         *
         * @param tables The list of database tables.
         */
        fun of(tables: List<Table>) = tables.map { SearchableFields(it, it.columns) }
    }
}

/**
 * Build a search query for a list of searchable fields and a search string.
 *
 * @param fields The list of searchable fields.
 * @param search The search string.
 * @return An expression representing the search query, or null if the search string is empty.
 */
fun SqlExpressionBuilder.search(fields: List<SearchableFields>, search: String): Op<Boolean>? {
    var buffer: Op<Boolean>? = null
    if (search.isNotEmpty()) {
        fields.flatMap { doSearch(it, search) }.forEach {
            buffer = buffer?.or(it) ?: it
        }
    }
    return buffer
}

/**
 * Build a search expression for a specific set of searchable fields and a search string.
 *
 * @param fields The searchable fields.
 * @param search The search string.
 * @return A list of search expressions.
 */
private fun SqlExpressionBuilder.doSearch(fields: SearchableFields, search: String) =
    fields.columns.filter { it.columnType is VarCharColumnType || it.columnType is TextColumnType }
        .filterIsInstance<Column<String>>()
        .map { (if (fields.table is Alias<*>) fields.table[it] else it) like "%$search%" }

