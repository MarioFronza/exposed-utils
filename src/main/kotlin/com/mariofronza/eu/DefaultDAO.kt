package com.mariofronza.eu

import com.mariofronza.eu.query.pagination.Pagination
import com.mariofronza.eu.query.pagination.PaginationResult
import com.mariofronza.eu.query.pagination.Order
import com.mariofronza.eu.query.limit
import com.mariofronza.eu.query.order
import com.mariofronza.eu.query.query
import com.mariofronza.eu.util.query
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.sql.Column


/**
 * Abstract base class for Data Access Objects (DAOs).
 *
 * @param Domain The domain model type.
 * @param ID The ID type.
 * @param E The database entity type.
 * @param dao The Exposed entity class.
 */
abstract class DefaultDAO<Domain : Any, ID : Comparable<ID>, E : Entity<ID>>(
    private val dao: EntityClass<ID, E>
) : Repository<Domain, ID> {

    /**
     * Convert a database entity to a domain model.
     *
     * @param E The database entity.
     * @return The domain model.
     */
    abstract fun E.toDomain(): Domain

    /**
     * The default sorting column for findAll when pagination is used.
     */
    protected open val defaultSortColumn: Column<*> = dao.table.id

    /**
     * The default sorting order for findAll when pagination is used.
     */
    protected open val defaultSortOrder: Order = Order.ASC

    override suspend fun findById(id: ID) = query {
        dao.findById(id)?.toDomain()
    }

    override suspend fun findAll() = query {
        dao.all().map { it.toDomain() }
    }

    override suspend fun findAll(pagination: Pagination) = query {
        val query = dao.query(pagination)
        PaginationResult(
            query.copy()
                .limit(pagination)
                .order(dao.table, pagination.sort, defaultSortColumn to defaultSortOrder)
                .map { it.toDomain() },
            query.copy().count()
        )
    }

    override suspend fun delete(id: ID) = query {
        dao[id].delete()
    }

    override suspend fun count(pagination: Pagination?) = query {
        dao.query(pagination).count()
    }


}
