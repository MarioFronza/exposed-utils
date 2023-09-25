package com.mariofronza.eu.util

import com.mariofronza.eu.DefaultDAO
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object TestTable : IntIdTable() {
    val name = varchar("name", 255)
}

data class TestDomain(val id: Int = -1, val name: String)

class TestEntity(id: EntityID<Int>) : Entity<Int>(id) {
    companion object : EntityClass<Int, TestEntity>(TestTable)

    var name by TestTable.name
}

class TestRepositoryImpl : DefaultDAO<TestDomain, Int, TestEntity>(TestEntity) {
    override fun TestEntity.toDomain() = TestDomain(id.value, name)
    override suspend fun create(entity: TestDomain): TestDomain {
        val newEntity = query {
            TestEntity.new {
                name = entity.name
            }
        }
        return newEntity.toDomain()
    }

    override suspend fun update(entity: TestDomain): TestDomain {
        val existingEntity = query { TestEntity.findById(entity.id) }

        if (existingEntity != null) {
            query {
                existingEntity.name = entity.name
            }
            return existingEntity.toDomain()
        } else {
            throw Exception("Failed to create entity of type ${entity.name}")
        }
    }
}