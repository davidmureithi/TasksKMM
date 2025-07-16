package com.example.tasklist.di

import app.cash.sqldelight.db.SqlDriver
import com.example.tasklist.db.TaskDatabase
import com.example.tasklist.data.repository.TaskRepository
import com.example.tasklist.domain.usecase.*
import com.example.tasklist.ui.TaskViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    single<SqlDriver> {
        DatabaseDriverFactory.create().createDriver()
    }
    
    single {
        TaskDatabase(get())
    }
    
    single { TaskRepository(get()) }
    
    // Use Cases
    singleOf(::GetTasksUseCase)
    singleOf(::AddTaskUseCase)
    singleOf(::UpdateTaskUseCase)
    singleOf(::DeleteTaskUseCase)
    
    single {
        TaskUseCases(
            getTasks = get(),
            addTask = get(),
            updateTask = get(),
            deleteTask = get()
        )
    }
    
    single {
        TaskViewModel(
            taskUseCases = get(),
            coroutineScope = CoroutineScope(Dispatchers.Main)
        )
    }
}
