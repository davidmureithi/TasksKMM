package com.example.tasklist.ui

import com.example.tasklist.domain.model.Task
import com.example.tasklist.domain.usecase.TaskUseCases
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.test.*

class TaskViewModelTest {
    private lateinit var viewModel: TaskViewModel
    private lateinit var fakeTaskUseCases: FakeTaskUseCases
    private lateinit var testScope: TestScope

    @BeforeTest
    fun setup() {
        testScope = TestScope()
        fakeTaskUseCases = FakeTaskUseCases()
        viewModel = TaskViewModel(
            taskUseCases = fakeTaskUseCases,
            coroutineScope = testScope
        )
    }

    @Test
    fun initialState_isCorrect() = runTest {
        assertEquals(TaskState(), viewModel.state.value)
    }

    @Test
    fun loadTasks_updatesStateWithTasks() = runTest {

        val testTasks = listOf(
            createTestTask("Task 1"),
            createTestTask("Task 2")
        )
        fakeTaskUseCases.tasksToEmit = testTasks

        
        viewModel = TaskViewModel(fakeTaskUseCases, testScope)

        
        assertEquals(testTasks, viewModel.state.value.tasks)
        assertFalse(viewModel.state.value.isLoading)
        assertNull(viewModel.state.value.error)
    }

    @Test
    fun loadTasks_whenError_updatesErrorState() = runTest {

        fakeTaskUseCases.shouldThrowError = true

        
        viewModel = TaskViewModel(fakeTaskUseCases, testScope)

        
        assertNull(viewModel.state.value.tasks)
        assertFalse(viewModel.state.value.isLoading)
        assertEquals("Test error", viewModel.state.value.error)
    }

    @Test
    fun filterActive_showsOnlyActiveTasks() = runTest {

        val completedTask = createTestTask("Completed", isCompleted = true)
        val activeTask = createTestTask("Active", isCompleted = false)
        fakeTaskUseCases.tasksToEmit = listOf(completedTask, activeTask)
        viewModel = TaskViewModel(fakeTaskUseCases, testScope)

        
        viewModel.onEvent(TaskEvent.FilterTasks(TaskFilter.Active))

        
        assertEquals(listOf(activeTask), viewModel.state.value.tasks)
    }

    @Test
    fun sortByDueDate_sortsTasksCorrectly() = runTest {

        val now = Clock.System.now()
        val task1 = createTestTask("Task 1", dueDate = now)
        val task2 = createTestTask("Task 2", dueDate = now.plus(DatePeriod(days = 1)))
        fakeTaskUseCases.tasksToEmit = listOf(task2, task1)
        viewModel = TaskViewModel(fakeTaskUseCases, testScope)

        
        viewModel.onEvent(TaskEvent.SortTasks(TaskSortOrder.DueDate))

        
        assertEquals(listOf(task1, task2), viewModel.state.value.tasks)
    }

    @Test
    fun filterByCategory_showsOnlyCategoryTasks() = runTest {

        val task1 = createTestTask("Task 1", category = "Work")
        val task2 = createTestTask("Task 2", category = "Personal")
        fakeTaskUseCases.tasksToEmit = listOf(task1, task2)
        viewModel = TaskViewModel(fakeTaskUseCases, testScope)

        
        viewModel.onEvent(TaskEvent.SelectCategory("Work"))

        
        assertEquals(listOf(task1), viewModel.state.value.tasks)
    }

    @Test
    fun filterToday_showsOnlyTodayTasks() = runTest {

        val now = Clock.System.now()
        val todayTask = createTestTask("Today Task", dueDate = now)
        val tomorrowTask = createTestTask("Tomorrow Task", dueDate = now.plus(DatePeriod(days = 1)))
        fakeTaskUseCases.tasksToEmit = listOf(todayTask, tomorrowTask)
        viewModel = TaskViewModel(fakeTaskUseCases, testScope)

        
        viewModel.onEvent(TaskEvent.FilterTasks(TaskFilter.Today))

        
        assertEquals(listOf(todayTask), viewModel.state.value.tasks)
    }

    @Test
    fun addTask_updatesTaskList() = runTest {
        
        viewModel.onEvent(TaskEvent.AddTask(
            title = "New Task",
            description = "Description",
            category = "Work"
        ))

        
        assertEquals(1, fakeTaskUseCases.tasksToEmit.size)
        assertEquals("New Task", fakeTaskUseCases.tasksToEmit.first().title)
        assertEquals("Work", fakeTaskUseCases.tasksToEmit.first().category)
    }

    @Test
    fun deleteTask_removesTaskFromList() = runTest {

        val task = createTestTask("Task to delete")
        fakeTaskUseCases.tasksToEmit = listOf(task)
        viewModel = TaskViewModel(fakeTaskUseCases, testScope)

        
        viewModel.onEvent(TaskEvent.DeleteTask(task.id))

        
        assertTrue(fakeTaskUseCases.tasksToEmit.isEmpty())
    }

    @Test
    fun toggleTaskCompletion_updatesTaskStatus() = runTest {

        val task = createTestTask("Task", isCompleted = false)
        fakeTaskUseCases.tasksToEmit = listOf(task)
        viewModel = TaskViewModel(fakeTaskUseCases, testScope)

        
        viewModel.onEvent(TaskEvent.ToggleTaskCompletion(task.id))

        
        assertTrue(fakeTaskUseCases.tasksToEmit.first().isCompleted)
    }

    private fun createTestTask(
        title: String,
        isCompleted: Boolean = false,
        dueDate: Instant? = null
    ): Task {
        return Task(
            id = title.hashCode().toLong(),
            title = title,
            description = null,
            isCompleted = isCompleted,
            dueDate = dueDate,
            category = null,
            tags = emptyList(),
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now()
        )
    }
}

class FakeTaskUseCases : TaskUseCases {
    var tasksToEmit: List<Task> = emptyList()
    var shouldThrowError = false

    override fun getTasks(): Flow<List<Task>> {
        if (shouldThrowError) {
            throw Exception("Test error")
        }
        return flowOf(tasksToEmit)
    }

    override suspend fun addTask(task: Task) {
        tasksToEmit = tasksToEmit + task
    }

    override suspend fun updateTask(task: Task) {
        tasksToEmit = tasksToEmit.map { if (it.id == task.id) task else it }
    }

    override suspend fun deleteTask(taskId: Long) {
        tasksToEmit = tasksToEmit.filter { it.id != taskId }
    }
}