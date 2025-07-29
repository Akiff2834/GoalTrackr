package com.goaltrackr.viewmodel

import app.cash.turbine.test
import com.goaltrackr.data.FirestoreRepository
import com.goaltrackr.data.model.Goal
import io.mockk.*
import kotlinx.coroutines.Dispatchers // ✅ eksikti
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GoalViewModelTest {

    private val repository: FirestoreRepository = mockk(relaxed = true)
    private lateinit var viewModel: GoalViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        coEvery { repository.getGoalsFlow() } returns flowOf(listOf(
            Goal(id = "1", title = "Test Goal", isCompleted = false)
        ))

        viewModel = GoalViewModel(repository) // ✅ Artık doğru
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial goals loaded`() = runTest {
        viewModel.goals.test {
            val items = awaitItem()
            assertEquals(1, items.size)
            assertEquals("Test Goal", items.first().title)
        }
    }

    @Test
    fun `filter completed goals`() = runTest {
        viewModel.setFilter(GoalViewModel.GoalFilter.COMPLETED)

        viewModel.filteredGoals.test {
            val items = awaitItem()
            assertTrue(items.isEmpty()) // çünkü örnek tamamlanmamış
        }
    }

    @Test
    fun `addGoal should call repository`() = runTest {
        val goal = Goal(id = "2", title = "New Goal")
        coEvery { repository.addGoal(goal) } just Runs

        viewModel.addGoal(goal)

        coVerify { repository.addGoal(goal) }
    }

    @Test
    fun `deleteGoal should call repository`() = runTest {
        coEvery { repository.deleteGoal("1") } just Runs
        viewModel.deleteGoal("1")
        coVerify { repository.deleteGoal("1") }
    }
}
