package com.example.tasklist.di

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.example.tasklist.db.TaskDatabase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual class DatabaseDriverFactory private constructor(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(schema = TaskDatabase.Schema, context = context, name = "task.db")
    }
    
    actual companion object : KoinComponent {
        private val context: Context by inject()

        actual fun create(): DatabaseDriverFactory = DatabaseDriverFactory(context)
    }
}
