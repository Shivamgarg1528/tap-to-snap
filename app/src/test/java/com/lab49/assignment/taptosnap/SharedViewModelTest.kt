package com.lab49.assignment.taptosnap

import app.cash.turbine.test
import com.lab49.assignment.taptosnap.util.Constants
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test

class SharedViewModelTest : BaseTest() {

    private var sharedViewModel: SharedViewModel? = null

    @Before
    override fun before() {
        super.before()
        sharedViewModel = SharedViewModel()
    }

    @After
    override fun after() {
        super.after()
        sharedViewModel = null
    }

    @Test
    fun `test the message queue when having 2 items`() = runBlocking {
        sharedViewModel!!.messageQueue.test {
            sharedViewModel!!.postMessage(Constants.FTG)
            assertEquals(Constants.FTG, awaitItem())
            sharedViewModel!!.postMessage(Constants.SWW)
            assertEquals(Constants.SWW, awaitItem())
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `test the message queue when having 0 items`() = runBlocking {
        sharedViewModel!!.messageQueue.test {
            cancelAndConsumeRemainingEvents()
        }
    }
}