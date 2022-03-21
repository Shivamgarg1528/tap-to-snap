package com.lab49.assignment.taptosnap.features.main.ui.vm

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.lab49.assignment.taptosnap.BaseTest
import com.lab49.assignment.taptosnap.data.model.request.ItemPostRequest
import com.lab49.assignment.taptosnap.data.model.response.ItemPostResponse
import com.lab49.assignment.taptosnap.data.repo.SnapRepo
import com.lab49.assignment.taptosnap.util.Constants
import com.lab49.assignment.taptosnap.util.Resource
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest : BaseTest() {

    private lateinit var mainVM: MainViewModel

    @Mock
    private lateinit var snapRepo: SnapRepo

    @Mock
    private lateinit var savedStateHandle: SavedStateHandle

    @Test
    fun `should emit empty when items is empty`() {
        Mockito.`when`(savedStateHandle.get<String>(anyString())).thenReturn(null)
        mainVM = MainViewModel(savedStateHandle, snapRepo)
        runBlocking {
            mainVM.events.test {
                assertEquals(MainViewModel.Event.Empty, awaitItem())
                cancelAndConsumeRemainingEvents()
            }
        }
    }

    @Test
    fun `should emit items when passed items are not empty`() {
        Mockito.`when`(savedStateHandle.get<String>(anyString())).thenReturn("A")
        mainVM = MainViewModel(savedStateHandle, snapRepo)
        runBlocking {
            mainVM.events.test {
                val awaitListItem = awaitItem()
                assert(awaitListItem is MainViewModel.Event.Items)
                assertEquals(1, (awaitListItem as MainViewModel.Event.Items).items.size)

                val awaitTimer = awaitItem()
                assert(awaitTimer is MainViewModel.Event.Timer)
                assertEquals("00:02:00", (awaitTimer as MainViewModel.Event.Timer).time)

                delay(1000)
                val awaitTimer1 = awaitItem()
                assert(awaitTimer1 is MainViewModel.Event.Timer)
                assertEquals("00:01:59", (awaitTimer1 as MainViewModel.Event.Timer).time)

                cancelAndConsumeRemainingEvents()
            }
        }
    }

    @Test
    fun `check recreation event`() {
        Mockito.`when`(savedStateHandle.get<String>(anyString())).thenReturn("A")
        mainVM = MainViewModel(savedStateHandle, snapRepo)
        runBlocking {
            mainVM.events.test {
                awaitItem()
                awaitItem()
                mainVM.checkRecreation()
                val awaitListItem = awaitItem()
                assert(awaitListItem is MainViewModel.Event.Items)
                assertEquals(1, (awaitListItem as MainViewModel.Event.Items).items.size)
                cancelAndConsumeRemainingEvents()
            }
        }
    }

    @Test
    fun `check restart event`() {
        Mockito.`when`(savedStateHandle.get<String>(anyString())).thenReturn("A")
        mainVM = MainViewModel(savedStateHandle, snapRepo)
        runBlocking {
            mainVM.events.test {
                awaitItem()
                awaitItem()
                mainVM.restart()
                val event = awaitItem()
                assert(event is MainViewModel.Event.Items)
                assertEquals(1, (event as MainViewModel.Event.Items).items.size)
                cancelAndConsumeRemainingEvents()
            }
        }
    }

    @Test
    fun `check exit event`() {
        Mockito.`when`(savedStateHandle.get<String>(anyString())).thenReturn("A")
        mainVM = MainViewModel(savedStateHandle, snapRepo)
        runBlocking {
            mainVM.events.test {
                awaitItem()
                awaitItem()
                mainVM.exit()
                val exit = awaitItem()
                assert(exit is MainViewModel.Event.Exit)
                cancelAndConsumeRemainingEvents()
            }
        }
    }

    @Test
    fun `check captured event`() {
        Mockito.`when`(savedStateHandle.get<String>(anyString())).thenReturn("A")
        mainVM = MainViewModel(savedStateHandle, snapRepo)
        runBlocking {
            mainVM.events.test {
                awaitItem()
                awaitItem()
                mainVM.captured(null)
                val message = awaitItem()
                assert(message is MainViewModel.Event.Message)
                assertEquals((message as MainViewModel.Event.Message).message, Constants.FTG)
                cancelAndConsumeRemainingEvents()
            }
        }
    }

    @Test
    fun `post Image event should emit loading & success`() {
        Mockito.`when`(savedStateHandle.get<String>(anyString())).thenReturn("A")
        mainVM = MainViewModel(savedStateHandle, snapRepo)
        val loading = Resource.Loading<ItemPostResponse>()
        val success = Resource.Success(ItemPostResponse(true, "A"))
        val itemPostRequest = ItemPostRequest("A", "A")
        Mockito.`when`(snapRepo.uploadItem(itemPostRequest)).thenReturn(flowOf(loading, success))
        runBlocking {
            mainVM.events.test {
                awaitItem()
                awaitItem()
                mainVM.postImage(itemPostRequest)

                val loadingState = (awaitItem() as MainViewModel.Event.Items).items[0]
                assert(loadingState.state == Constants.STATE.RUNNING)

                val successState = (awaitItem() as MainViewModel.Event.Items).items[0]
                assert(successState.state == Constants.STATE.SUCCESS)

                cancelAndConsumeRemainingEvents()

            }
        }
    }

    @Test
    fun `post Image event should emit loading & failed`() {
        Mockito.`when`(savedStateHandle.get<String>(anyString())).thenReturn("A")
        mainVM = MainViewModel(savedStateHandle, snapRepo)
        val loading = Resource.Loading<ItemPostResponse>()
        val failed = Resource.Failure<ItemPostResponse>(IllegalStateException("A"))
        val itemPostRequest = ItemPostRequest("A", "A")
        Mockito.`when`(snapRepo.uploadItem(itemPostRequest)).thenReturn(flowOf(loading, failed))
        runBlocking {
            mainVM.events.test {
                awaitItem()
                awaitItem()
                mainVM.postImage(itemPostRequest)

                val loadingState = (awaitItem() as MainViewModel.Event.Items).items[0]
                assert(loadingState.state == Constants.STATE.RUNNING)

                val successState = (awaitItem() as MainViewModel.Event.Items).items[0]
                assert(successState.state == Constants.STATE.FAILED)

                cancelAndConsumeRemainingEvents()
            }
        }
    }
}