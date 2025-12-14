package com.example.fitness_tracker.viewmodels

import com.example.fitness_tracker.MainDispatcherRule
import com.example.fitness_tracker.Repository.AuthRepo
import com.example.fitness_tracker.Repository.WalkRepo
import com.example.fitness_tracker.viewmodels.food_viewmodel.FoodViewModel
import com.example.fitness_tracker.viewmodels.walk_viewmodel.WalkViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify

@OptIn(ExperimentalCoroutinesApi::class)
class WalkViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var walkRepo: WalkRepo

    @Mock
    private lateinit var authRepo: AuthRepo

    private lateinit var viewModel: WalkViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        `when`(walkRepo.getTodayDistance(any(), any())).thenReturn(flowOf(0.0))
        `when`(walkRepo.getTodayCalories(any(), any())).thenReturn(flowOf(0))
    }
    @Test
    fun `initial state loads total steps from repository`() = runTest {
        val fakeUserId = "user123"
        val expectedSteps = 3000
        `when`(authRepo.getCurrentUserId()).thenReturn(fakeUserId)
        `when`(walkRepo.getTodaySteps(eq(fakeUserId), any())).thenReturn(flowOf(expectedSteps))
        viewModel = WalkViewModel(walkRepo, authRepo,SharingStarted.Eagerly)
        assertEquals(expectedSteps,viewModel.todaySteps.value)
        verify(walkRepo).getTodaySteps(eq(fakeUserId), any())
    }

    @Test
    fun `addSteps calculates distance and calories correctly`() = runTest {
        val fakeUserId = "user123"
        `when`(authRepo.getCurrentUserId()).thenReturn(fakeUserId)
        `when`(walkRepo.getTodaySteps(any(), any())).thenReturn(flowOf(0))
        viewModel = WalkViewModel(walkRepo, authRepo, SharingStarted.Eagerly)
        val inputSteps = 1000
        val expectedDistance = 1000 * 0.0008
        val expectedCalories = (1000 * 0.04).toInt()
        viewModel.addSteps(inputSteps)
        verify(walkRepo).insertWalk(org.mockito.kotlin.check { walk ->
            assertEquals(fakeUserId, walk.userId)
            assertEquals(inputSteps, walk.steps)
            assertEquals(expectedDistance, walk.distance, 0.0001)
            assertEquals(expectedCalories, walk.caloriesBurned)
        })
    }
}