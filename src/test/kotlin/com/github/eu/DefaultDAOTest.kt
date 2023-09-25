package com.github.eu

import com.github.eu.query.pagination.Pagination
import com.github.eu.util.TestDomain
import com.github.eu.util.TestRepositoryImpl
import com.github.eu.util.TestTable
import com.github.eu.util.query
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test



class DefaultDAOTest {
    private lateinit var database: Database
    private lateinit var repository: TestRepositoryImpl

    @BeforeEach
    fun setUp() {
        database = Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;", driver = "org.h2.Driver")
        transaction(database) {
            SchemaUtils.create(TestTable)
        }
        repository = TestRepositoryImpl()
    }

    @AfterEach
    fun tearDown() {
        transaction(database) {
            SchemaUtils.drop(TestTable)
        }

    }

    @Test
    fun `findById should return null when entity does not exist`() = runBlocking {
        val result = repository.findById(1)
        assertNull(result)
    }

    @Test
    fun `findById should return entity when it exists`() = runBlocking {
        val entityId = query {
            TestTable.insertAndGetId {
                it[name] = "Entity 1"
            }
        }

        val result = repository.findById(entityId.value)
        assertNotNull(result)
        assertEquals("Entity 1", result?.name)
    }

    @Test
    fun `findAll should return empty list when no entities exist`() = runBlocking {
        val result = repository.findAll()
        assertTrue(result.isEmpty())
    }

    @Test
    fun `findAll should return list of entities when they exist`() = runBlocking {
        query {
            TestTable.insert {
                it[name] = "Entity 1"
            }
            TestTable.insert {
                it[name] = "Entity 2"
            }
        }

        val result = repository.findAll()
        assertEquals(2, result.size)
        assertEquals("Entity 1", result[0].name)
        assertEquals("Entity 2", result[1].name)
    }

    @Test
    fun `create should return created entity`() = runBlocking {
        val entity = TestDomain(name = "New Entity")
        val result = repository.create(entity)
        assertNotNull(result.id)
        assertEquals("New Entity", result.name)
    }

    @Test
    fun `update should return updated entity`() = runBlocking {
        val entityId = query {
            TestTable.insertAndGetId {
                it[name] = "Entity 1"
            }
        }

        val updatedEntity = TestDomain(entityId.value, "Updated Entity")
        val result = repository.update(updatedEntity)
        assertNotNull(result)
        assertEquals(entityId.value, result.id)
        assertEquals("Updated Entity", result.name)
    }

    @Test
    fun `delete should remove entity`() = runBlocking {
        val entityId = query {
            TestTable.insertAndGetId {
                it[name] = "Entity 1"
            }
        }

        repository.delete(entityId.value)

        val result = query {
            TestTable.selectAll().count()
        }

        assertEquals(0, result)
    }

    @Test
    fun `count should return zero when no entities exist`() = runBlocking {
        val result = repository.count()
        assertEquals(0, result)
    }

    @Test
    fun `count should return the number of entities when they exist`() = runBlocking {
        query {
            TestTable.insert {
                it[name] = "Entity 1"
            }
            TestTable.insert {
                it[name] = "Entity 2"
            }
        }

        val result = repository.count()
        assertEquals(2, result)
    }

    @Test
    fun `findAll with pagination should return entities for the first page`() = runBlocking {
        repository.create(TestDomain(1, "Entity 1"))
        repository.create(TestDomain(2, "Entity 2"))
        repository.create(TestDomain(3, "Entity 3"))
        repository.create(TestDomain(4, "Entity 4"))
        repository.create(TestDomain(5, "Entity 5"))

        val pagination = Pagination(page = 0, itemsPerPage = 2)
        val result = repository.findAll(pagination)

        assertEquals(2, result.items.size)
        assertEquals(2, result.total)
    }

    @Test
    fun `findAll with pagination should return entities for the second page`() = runBlocking {
        repository.create(TestDomain(1, "Entity 1"))
        repository.create(TestDomain(2, "Entity 2"))
        repository.create(TestDomain(3, "Entity 3"))
        repository.create(TestDomain(4, "Entity 4"))
        repository.create(TestDomain(5, "Entity 5"))

        val pagination = Pagination(page = 1, itemsPerPage = 2)
        val result = repository.findAll(pagination)

        assertEquals(2, result.items.size)
        assertEquals(2, result.total)
    }

    @Test
    fun `findAll with pagination should return an empty list for an empty page`() = runBlocking {
        val pagination = Pagination(page = 2, itemsPerPage = 2)
        val result = repository.findAll(pagination)

        assertEquals(0, result.items.size)
        assertEquals(0, result.total)
    }

    @Test
    fun `findById should return an entity with a valid ID`() = runBlocking {
        repository.create(TestDomain(1, "Entity 1"))

        val entity = repository.findById(1)

        assertEquals("Entity 1", entity?.name)
    }

}