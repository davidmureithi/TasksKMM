package com.example.tasklist.android

import android.app.Application
import com.example.tasklist.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class TaskApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        startKoin {
            androidLogger()
            androidContext(this@TaskApp)
            modules(appModule)
        }
    }
}
