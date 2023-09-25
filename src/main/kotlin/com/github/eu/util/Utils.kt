package com.github.eu.util

import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

/**
 * Execute a database query within a suspended transaction.
 *
 * @param block The block of code to execute as part of the transaction.
 * @return The result of the query.
 */
suspend fun <T> query(
    block: suspend () -> T
): T = newSuspendedTransaction { block() }

