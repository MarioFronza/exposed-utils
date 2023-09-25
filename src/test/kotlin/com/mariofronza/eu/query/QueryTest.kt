package com.mariofronza.eu.query

import com.mariofronza.eu.query.pagination.Pagination
import com.mariofronza.eu.util.TestEntity
import com.mariofronza.eu.util.TestTable
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test


class QueryTest {

    private lateinit var database: Database

    @BeforeEach
    fun setUp() {
        database = Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;", driver = "org.h2.Driver")
        transaction(database) {
            SchemaUtils.create(TestTable)
        }
    }

    @AfterEach
    fun tearDown() {
        transaction(database) {
            SchemaUtils.drop(TestTable)
        }
    }

    @Test
    fun `query should return all entities when pagination is null`() {
        transaction(database) {
            TestTable.insert {
                it[name] = "Entity 1"
            }
            TestTable.insert {
                it[name] = "Entity 2"
            }
            TestTable.insert {
                it[name] = "Entity 3"
            }
            val result = TestEntity.all().toList()

            assertEquals(3, result.size)
        }
    }

    @Test
    fun `query should apply pagination when provided`() {
        transaction(database) {
            repeat(10) { index ->
                TestTable.insert {
                    it[name] = "Entity $index"
                }
            }

            val pagination = Pagination(itemsPerPage = 5, page = 1)
            val result = TestEntity.query(pagination).toList()

            assertEquals(5, result.size)
            assertEquals("Entity 5", result[0].name)
            assertEquals("Entity 9", result[4].name)
        }
    }

    @Test
    fun `query should apply search filter when search criteria provided`() {
        transaction(database) {
            repeat(10) { index ->
                TestTable.insert {
                    it[name] = "Entity $index"
                }
            }

            val pagination = Pagination(itemsPerPage = 10, page = 0, search = "7")
            val result = TestEntity.query(pagination).toList()

            assertEquals(1, result.size)
            assertEquals("Entity 7", result[0].name)
        }
    }

    @Test
    fun `query should return empty list when no entities match search criteria`() {
        transaction(database) {
            repeat(10) { index ->
                TestTable.insert {
                    it[name] = "Entity $index"
                }
            }
            val pagination = Pagination(itemsPerPage = 10, page = 0, search = "NonExistent")
            val result = TestEntity.query(pagination).toList()

            assertEquals(0, result.size)
        }
    }

    @Test
    fun `query should apply search and pagination together`() {
        transaction(database) {
            repeat(50) { index ->
                TestTable.insert {
                    it[name] = "Entity $index"
                }
            }

            val pagination = Pagination(itemsPerPage = 2, page = 2, search = "3")
            val result = TestEntity.query(pagination).toList()

            assertEquals(2, result.size)
            assertEquals("Entity 31", result[0].name)
            assertEquals("Entity 32", result[1].name)
        }
    }

}

