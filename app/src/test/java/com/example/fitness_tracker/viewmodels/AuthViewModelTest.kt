package com.example.fitness_tracker.viewmodels

import com.example.fitness_tracker.MainDispatcherRule
import com.example.fitness_tracker.Repository.AuthRepo
import com.example.fitness_tracker.data.UserPreferences
import com.example.fitness_tracker.database.User
import com.example.fitness_tracker.viewmodels.auth_viewmodel.AuthState
import com.example.fitness_tracker.viewmodels.auth_viewmodel.AuthViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.verify

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock private lateinit var authRepo: AuthRepo
    @Mock private lateinit var userPrefs: UserPreferences

    private lateinit var viewModel: AuthViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        viewModel = AuthViewModel(authRepo, userPrefs)
    }

    @Test
    fun `login success updates state to success and saves preferences`() = runTest {
        val email = "test@test.com"
        val pass = "password"
        val uid = "user123"
        val mockUser = User(uid = uid, weight = 70.0, height = 170.0, age = 25, gender = "Male")
        `when`(authRepo.login(email, pass)).thenReturn(Result.success("Sign in Successfully"))
        `when`(authRepo.getCurrentUserId()).thenReturn(uid)
        `when`(authRepo.getUser(uid)).thenReturn(Result.success(mockUser))
        viewModel.login(email, pass)
        assertTrue(viewModel.authState.value is AuthState.Success)
        assertEquals(mockUser, viewModel.currentUserProfile.value)
        verify(userPrefs).saveUserGoals(
            weight = 70.0,
            height = 170.0,
            age = 25,
            gender = "Male"
        )
    }

    @Test
    fun `login failure updates state to Error`() = runTest {
        val email = "a@gmail.com"
        val pass = "wrongpass"
        val errorMsg = "Wrong password"
        `when`(authRepo.login(email, pass)).thenReturn(Result.failure(Exception(errorMsg)))
        viewModel.login(email, pass)
        val state = viewModel.authState.value
        assertTrue(state is AuthState.Error)
        assertEquals(errorMsg, (state as AuthState.Error).message)
    }

    @Test
    fun `signUp fails if age or weight are not valid numbers`() = runTest {
        val name = "John"
        val email = "john@test.com"
        val pass = "123456"
        val invalidAge = "abc"
        val gender = "Male"
        val height = "180"
        val weight = "80"
        viewModel.signUp(name, email, pass, invalidAge, gender, height, weight)
        val state = viewModel.authState.value
        assertTrue("State should be Error when inputs are invalid", state is AuthState.Error)
        verify(authRepo, never()).signUp(any(), any(), any(), any(), any(), any(), any())
    }

    @Test
    fun `logout clears user profile and resets state`() = runTest {
        viewModel.logout()
        verify(authRepo).logout()
        assertEquals(null, viewModel.currentUserProfile.value)
        assertTrue(viewModel.authState.value is AuthState.Idle)
    }
}