package com.example.fitness_tracker.viewmodels

import com.example.fitness_tracker.MainDispatcherRule
import com.example.fitness_tracker.Repository.AuthRepo
import com.example.fitness_tracker.Repository.FoodRepo
import com.example.fitness_tracker.data.UserPreferences
import com.example.fitness_tracker.viewmodels.food_viewmodel.FoodViewModel
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
class FoodViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var foodRepo: FoodRepo

    @Mock
    private lateinit var authRepo: AuthRepo

    @Mock
    private lateinit var userPrefs: UserPreferences

    private lateinit var viewModel: FoodViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        `when`(userPrefs.dailyCalorieGoal).thenReturn(flowOf(2500))
        `when`(foodRepo.getTodayFoods(any(), any())).thenReturn(flowOf(emptyList()))
        `when`(foodRepo.getTodayProtein(any(), any())).thenReturn(flowOf(0.0))
        `when`(foodRepo.getTodayCarbs(any(), any())).thenReturn(flowOf(0.0))
        `when`(foodRepo.getTodayFats(any(), any())).thenReturn(flowOf(0.0))
    }
    @Test
    fun `initial state loads total calories from repository`() = runTest {
        val fakeUserId = "user123"
        val expectedCalories = 1000
        `when`(authRepo.getCurrentUserId()).thenReturn(fakeUserId)
        `when`(foodRepo.getTodayCalories(eq(fakeUserId), any())).thenReturn(flowOf(expectedCalories))
        viewModel = FoodViewModel(foodRepo, authRepo, userPrefs,SharingStarted.Eagerly)
        assertEquals(expectedCalories,viewModel.todayCalories.value)
        verify(foodRepo).getTodayCalories(eq(fakeUserId), any())

    }
    @Test
    fun `addFood correctly calculates total calories based on macros`() = runTest {
        val fakeUserId = "user123"
        `when`(authRepo.getCurrentUserId()).thenReturn(fakeUserId)
        `when`(foodRepo.getTodayCalories(any(), any())).thenReturn(flowOf(0))
        viewModel = FoodViewModel(foodRepo, authRepo, userPrefs,SharingStarted.Eagerly)
        val name = "Chicken Breast"
        val weight = 100.0
        val protein = 30.0
        val carbs = 0.0
        val fats = 5.0
        val expectedCalories = 165
        viewModel.addFood(name, weight, protein, carbs, fats)
        verify(foodRepo).insertFood(org.mockito.kotlin.check { food ->
            assertEquals(expectedCalories, food.totalCalories)
            assertEquals(name, food.foodName)
            assertEquals(fakeUserId, food.userId)
        })
    }
}