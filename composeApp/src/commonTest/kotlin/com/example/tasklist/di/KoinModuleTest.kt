package com.example.tasklist.di

import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.check.checkModules
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class KoinModuleTest : KoinTest {

    @BeforeTest
    fun setup() {
        startKoin {
            modules(appModule, testModule)
        }
    }

    @AfterTest
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `verify koin module`() {
        checkModules {
            modules(appModule, testModule)
        }
    }
}
