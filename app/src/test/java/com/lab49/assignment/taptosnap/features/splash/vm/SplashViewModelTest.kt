package com.lab49.assignment.taptosnap.features.splash.vm

import app.cash.turbine.test
import com.lab49.assignment.taptosnap.BaseTest
import com.lab49.assignment.taptosnap.data.model.response.ItemsListResponse
import com.lab49.assignment.taptosnap.data.repo.SnapRepo
import com.lab49.assignment.taptosnap.util.Resource
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SplashViewModelTest : BaseTest() {

    private lateinit var splashVM: SplashViewModel

    @Mock
    private lateinit var snapRepo: SnapRepo

    @Before
    override fun before() {
        super.before()
        splashVM = SplashViewModel(snapRepo)
    }

    @Test
    fun `should emit loading and success when get items called with item list`() {
        val loading = Resource.Loading<ItemsListResponse>()
        val success = Resource.Success(items)
        Mockito.`when`(snapRepo.getItems()).thenReturn(flowOf(loading, success))
        runBlocking {
            splashVM.events.test {
                splashVM.getItems()
                assertEquals(SplashViewModel.Event.NoOperation, awaitItem())
                assertEquals(SplashViewModel.Event.Loading, awaitItem())
                assertEquals(SplashViewModel.Event.Success(A), awaitItem())
                cancelAndConsumeRemainingEvents()
            }
        }
    }

    @Test
    fun `should emit loading and empty when get items called without items list`() {
        val loading = Resource.Loading<ItemsListResponse>()
        val empty = Resource.Success(ItemsListResponse())
        Mockito.`when`(snapRepo.getItems()).thenReturn(flowOf(loading, empty))
        runBlocking {
            splashVM.events.test {
                splashVM.getItems()
                assertEquals(SplashViewModel.Event.NoOperation, awaitItem())
                assertEquals(SplashViewModel.Event.Loading, awaitItem())
                assertEquals(SplashViewModel.Event.Empty, awaitItem())
                cancelAndConsumeRemainingEvents()
            }
        }
    }

    @Test
    fun `should emit loading and error when get items called`() {
        val throwable = IllegalStateException(A)
        val loading = Resource.Loading<ItemsListResponse>()
        val error = Resource.Failure<ItemsListResponse>(throwable)
        Mockito.`when`(snapRepo.getItems()).thenReturn(flowOf(loading, error))
        runBlocking {
            splashVM.events.test {
                splashVM.getItems()
                assertEquals(SplashViewModel.Event.NoOperation, awaitItem())
                assertEquals(SplashViewModel.Event.Loading, awaitItem())
                assertEquals(SplashViewModel.Event.Failed(throwable), awaitItem())
                cancelAndConsumeRemainingEvents()
            }
        }
    }

    companion object {
        private const val A = "A"
        val items = ItemsListResponse().apply { add(ItemsListResponse.Item(A)) }
    }
}