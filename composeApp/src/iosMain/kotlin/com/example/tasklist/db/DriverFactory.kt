package com.example.tasklist.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver

class DriverFactory {
    fun createDriver(): SqlDriver {
        return NativeSqliteDriver(TaskDatabase.Schema, "task.db")
    }
}
