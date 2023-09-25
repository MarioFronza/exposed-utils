package com.github.eu.query

import com.github.eu.query.pagination.Order
import com.github.eu.query.pagination.Sort
import io.mockk.every
import io.mockk.mockk
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.SizedIterable
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.Table
import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test

class OrderTest {

    @Test
    fun `Order extension should map Order to SortOrder correctly`() {
        assertEquals(SortOrder.ASC, Order.ASC.order())
        assertEquals(SortOrder.DESC, Order.DESC.order())
        assertEquals(SortOrder.ASC, null.order())
    }

    @Test
    fun `order function should correctly order SizedIterable based on Sort`() {
        val table = mockk<Table>()
        val firstColumn = mockk<Column<*>>()
        val secondColumn = mockk<Column<*>>()
        val columns = listOf(firstColumn, secondColumn)

        every { table.columns } returns columns
        every { firstColumn.name } returns "firstColumn"
        every { secondColumn.name } returns "secondColumn"

        val sort = Sort("firstColumn", Order.DESC)
        val default = secondColumn to Order.ASC

        val result = mockk<SizedIterable<String>>()
        every { result.orderBy(any()) } returns result

        val iterable = mockk<SizedIterable<String>>()
        every { iterable.orderBy(any()) } returns result

        val ordered = iterable.order(table, sort, default)

        assertEquals(result, ordered)
    }

}