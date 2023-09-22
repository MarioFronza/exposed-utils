package com.mariofronza.eu.query

import io.mockk.every
import io.mockk.mockk
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.QueryBuilder
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.VarCharColumnType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals


class SearchTest {

    @Test
    fun `of should create searchable field for a single table`() {
        val table = mockk<Table>()
        val firstColumn = mockk<Column<String>>()
        val secondColumn = mockk<Column<String>>()

        every { table.columns } returns listOf(firstColumn, secondColumn)

        val searchableFields = SearchableFields.of(table)

        assertEquals(1, searchableFields.size)
        assertEquals(table, searchableFields.first().table)
        assertEquals(listOf(firstColumn, secondColumn), searchableFields.first().columns)
    }

    @Test
    fun `of should create searchable fields for a list of tables`() {
        val firstTable = mockk<Table>()
        val secondTable = mockk<Table>()
        val firstColumn = mockk<Column<String>>()
        val secondColumn = mockk<Column<String>>()

        every { firstTable.columns } returns listOf(firstColumn)
        every { secondTable.columns } returns listOf(secondColumn)

        val searchableFields = SearchableFields.of(listOf(firstTable, secondTable))

        assertEquals(2, searchableFields.size)
        assertEquals(firstTable, searchableFields.first().table)
        assertEquals(listOf(firstColumn), searchableFields.first().columns)
        assertEquals(secondTable, searchableFields.last().table)
        assertEquals(listOf(secondColumn), searchableFields.last().columns)
    }

    @Test
    fun `search should build a search query for non-empty search string`() {
        val table = mockk<Table>()
        val firstColumn = mockk<Column<String>>()
        val secondColumn = mockk<Column<String>>()
        val fields = SearchableFields(table, listOf(firstColumn, secondColumn))
        val search = "value"

        every { firstColumn.columnType } returns VarCharColumnType()
        every { secondColumn.columnType } returns VarCharColumnType()
        every { firstColumn.name } returns "firstColumn"
        every { secondColumn.name } returns "secondColumn"
        every { firstColumn.toQueryBuilder(any()) } answers {
            val builder = arg<QueryBuilder>(0)
            builder.append(firstColumn.name)
        }
        every { secondColumn.toQueryBuilder(any()) } answers {
            val builder = arg<QueryBuilder>(0)
            builder.append(secondColumn.name)
        }

        val query = SqlExpressionBuilder.search(listOf(fields), search)

        assertEquals("(firstColumn LIKE '%value%') OR (secondColumn LIKE '%value%')", query?.toString())
    }

    @Test
    fun `search should return null for an empty search string`() {
        val table = mockk<Table>()
        val firstColumn = mockk<Column<String>>()
        val secondColumn = mockk<Column<String>>()
        val fields = SearchableFields(table, listOf(firstColumn, secondColumn))
        val search = ""

        every { firstColumn.columnType } returns VarCharColumnType()
        every { secondColumn.columnType } returns VarCharColumnType()

        every { firstColumn.toQueryBuilder(any()) } returns mockk()
        every { secondColumn.toQueryBuilder(any()) } returns mockk()

        val query = SqlExpressionBuilder.search(listOf(fields), search)

        assertEquals(null, query)
    }


}