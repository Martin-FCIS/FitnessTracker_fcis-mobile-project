package com.example.fitness_tracker.viewmodels

import com.example.fitness_tracker.MainDispatcherRule
import com.example.fitness_tracker.Repository.AuthRepo
import com.example.fitness_tracker.Repository.WaterRepo
import com.example.fitness_tracker.data.UserPreferences
import com.example.fitness_tracker.viewmodels.water_viewmodel.WaterViewModel
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
class WaterViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var waterRepo: WaterRepo

    @Mock
    private lateinit var authRepo: AuthRepo

    @Mock
    private lateinit var userPrefs: UserPreferences

    private lateinit var viewModel: WaterViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        `when`(userPrefs.dailyWaterGoal).thenReturn(flowOf(2000))
    }

    @Test
    fun `initial state loads total intake from repository`() = runTest {
        val fakeUserId = "user123"
        val expectedIntake = 1500
        `when`(authRepo.getCurrentUserId()).thenReturn(fakeUserId)
        `when`(waterRepo.getTodayTotalForUser(eq(fakeUserId), any())).thenReturn(flowOf(expectedIntake))
        `when`(waterRepo.getRecordsForUser(fakeUserId)).thenReturn(flowOf(emptyList()))
        viewModel = WaterViewModel(
            waterRepo,
            authRepo,
            userPrefs,
            SharingStarted.Eagerly
        )
        assertEquals(expectedIntake, viewModel.totalIntake.value)
        verify(waterRepo).getTodayTotalForUser(eq(fakeUserId),any())
    }

    @Test
    fun `addWater inserts correct entity into repository`() = runTest {
        val fakeUserId = "user123"
        `when`(authRepo.getCurrentUserId()).thenReturn(fakeUserId)
        `when`(waterRepo.getTodayTotalForUser(eq(fakeUserId), any())).thenReturn(flowOf(0))
        `when`(waterRepo.getRecordsForUser(fakeUserId)).thenReturn(flowOf(emptyList()))
        viewModel = WaterViewModel(waterRepo, authRepo, userPrefs,SharingStarted.Eagerly)
        val waterAmount = 250
        viewModel.addWater(waterAmount)
        verify(waterRepo).insert(org.mockito.kotlin.check { entity ->
            assertEquals(fakeUserId, entity.userId)
            assertEquals(waterAmount, entity.amount)
        })
    }
}