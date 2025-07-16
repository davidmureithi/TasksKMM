package com.example.tasklist.di

import app.cash.sqldelight.db.SqlDriver

expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver

    companion object {
        fun create(): DatabaseDriverFactory
    }
}
