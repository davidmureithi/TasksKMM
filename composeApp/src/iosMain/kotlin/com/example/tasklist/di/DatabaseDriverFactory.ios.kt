package com.example.tasklist.di

import app.cash.sqldelight.db.SqlDriver
import com.example.tasklist.db.DriverFactory
import com.example.tasklist.db.TaskDatabase

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return DriverFactory().createDriver()
    }

    actual companion object {
        actual fun create(): DatabaseDriverFactory = DatabaseDriverFactory()
    }
}
