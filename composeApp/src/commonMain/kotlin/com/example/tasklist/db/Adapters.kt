package com.example.tasklist.db

import app.cash.sqldelight.ColumnAdapter

object Adapters {
    val booleanAdapter = object : ColumnAdapter<Boolean, Long> {
        override fun decode(databaseValue: Long): Boolean = databaseValue == 1L
        override fun encode(value: Boolean): Long = if (value) 1L else 0L
    }
}
