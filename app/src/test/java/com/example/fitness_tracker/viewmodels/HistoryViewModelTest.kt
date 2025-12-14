package com.example.fitness_tracker.viewmodels

import com.example.fitness_tracker.MainDispatcherRule
import com.example.fitness_tracker.Repository.AuthRepo
import com.example.fitness_tracker.Repository.FoodRepo
import com.example.fitness_tracker.Repository.WalkRepo
import com.example.fitness_tracker.Repository.WaterRepo
import com.example.fitness_tracker.data.food.FoodEntity
import com.example.fitness_tracker.data.walk.WalkEntity
import com.example.fitness_tracker.data.water.WaterEntity
import com.example.fitness_tracker.viewmodels.history_viewmodel.HistoryViewModel
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalCoroutinesApi::class)
class HistoryViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock private lateinit var waterRepo: WaterRepo
    @Mock private lateinit var foodRepo: FoodRepo
    @Mock private lateinit var walkRepo: WalkRepo
    @Mock private lateinit var authRepo: AuthRepo

    private lateinit var viewModel: HistoryViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `historyList combines water, food and walk data correctly`() = runTest {
        val fakeUserId = "user123"
        `when`(authRepo.getCurrentUserId()).thenReturn(fakeUserId)
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val now = System.currentTimeMillis()
        val waterList = listOf(
            WaterEntity(userId = fakeUserId, amount = 1000, timestamp = now),
            WaterEntity(userId = fakeUserId, amount = 500, timestamp = now)
        )
        val foodList = listOf(
            FoodEntity(userId = fakeUserId, foodName = "Apple", totalCalories = 100, timestamp = now, weight = 100.0, protein = 0.0, carbs = 0.0, fats = 0.0)
        )
        val walkList = listOf(
            WalkEntity(userId = fakeUserId, date = today, steps = 5000, caloriesBurned = 200, distance = 3.5)
        )
        `when`(waterRepo.getRecordsForUser(fakeUserId)).thenReturn(flowOf(waterList))
        `when`(foodRepo.getAllFoods(fakeUserId)).thenReturn(flowOf(foodList))
        `when`(walkRepo.getAllWalks(fakeUserId)).thenReturn(flowOf(walkList))
        viewModel = HistoryViewModel(waterRepo, foodRepo, walkRepo, authRepo,SharingStarted.Eagerly)
        val resultList = viewModel.historyList.value
        assertEquals("Should have 1 history entry for today", 1, resultList.size)
        val historyItem = resultList[0]
        assertEquals(today, historyItem.date)
        assertEquals("Water should be summed", 1500, historyItem.totalWater)
        assertEquals("Food calories should match", 100, historyItem.totalCaloriesConsumed)
        assertEquals("Steps should match", 5000, historyItem.totalSteps)
    }

}