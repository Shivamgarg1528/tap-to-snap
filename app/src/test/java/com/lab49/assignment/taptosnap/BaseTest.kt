package com.lab49.assignment.taptosnap

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before

open class BaseTest {
    protected val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @Before
    open fun before() {
        Dispatchers.setMain(mainThreadSurrogate)
    }

    @After
    open fun after() {
        Dispatchers.resetMain() // reset the main dispatcher to the original Main dispatcher
        mainThreadSurrogate.close()
    }
}
