package com.arjental.dealdone

import com.arjental.dealdone.models.TaskItem
import com.arjental.dealdone.repository.Actualizer
import com.arjental.dealdone.repository.Repository
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.concurrent.atomic.AtomicBoolean

class ActualizerUnitTests {

    private val repository: Repository = mock()
    private val translator: Translator = mock()

    private val actualizer = Actualizer()

    private val listForTests = ListsForTests()

    init {
        actualizer.translator = translator
        actualizer.repository = repository
    }

    private val flow = MutableSharedFlow<List<TaskItem>>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    @Test
    fun `actualizationWork server is empty and db too server available` () {
        runBlocking {
            whenever(translator.updateFlag).thenReturn(AtomicBoolean(true))
            whenever(repository.getTasks()).thenReturn(Pair(emptyList(), true))
            whenever(repository.getTasksFromDatabase()).thenReturn(emptyList())
            whenever(translator.actualTaskList).thenReturn(flow)
            val exp = emptyList<TaskItem>()
            actualizer.actualizationWork(this)
            val average = flow.first()
            assertEquals(exp, average)
        }
    }

    @Test
    fun `actualizationWork server is empty and db server not available` () {
        runBlocking {
            whenever(translator.updateFlag).thenReturn(AtomicBoolean(true))
            whenever(repository.getTasks()).thenReturn(Pair(emptyList(), false))
            whenever(repository.getTasksFromDatabase()).thenReturn(emptyList())
            whenever(translator.actualTaskList).thenReturn(flow)
            val exp = emptyList<TaskItem>()
            actualizer.actualizationWork(this)
            val average = flow.first()
            assertEquals(exp, average)
        }
    }

    @Test
    fun `actualizationWork server is empty db is not empty server not available` () {
        runBlocking {
            whenever(translator.updateFlag).thenReturn(AtomicBoolean(true))
            whenever(repository.getTasks()).thenReturn(Pair(emptyList(), false))
            whenever(repository.getTasksFromDatabase()).thenReturn(listForTests.actualizerListOfItems)
            whenever(actualizer.updateTasksOnServer(emptyList(),emptyList())).thenReturn(Unit)
            whenever(translator.actualTaskList).thenReturn(flow)
            val exp = listForTests.actualizerPasteList
            actualizer.actualizationWork(this)
            val average = flow.first()
            assertEquals(exp, average)
        }
    }

    @Test
    fun `actualizationWork server is empty db is not empty server available` () {
        runBlocking {
            whenever(translator.updateFlag).thenReturn(AtomicBoolean(true))
            whenever(repository.getTasks()).thenReturn(Pair(emptyList(), true))
            whenever(repository.getTasksFromDatabase()).thenReturn(listForTests.actualizerListOfItems)
            whenever(actualizer.updateTasksOnServer(emptyList(),emptyList())).thenReturn(Unit)
            whenever(translator.actualTaskList).thenReturn(flow)
            val exp = listForTests.actualizerPasteList
            actualizer.actualizationWork(this)
            val average = flow.first()
            assertEquals(exp, average)
        }
    }

    @Test
    fun `actualizationWork server is not empty db is empty server available` () {
        runBlocking {

            whenever(translator.updateFlag).thenReturn(AtomicBoolean(true))
            whenever(repository.getTasks()).thenReturn(Pair(listForTests.actualizerListOfItemsFromServ, true))
            whenever(repository.getTasksFromDatabase()).thenReturn(emptyList())
            whenever(repository.setTasksToDatabase(emptyList())).thenReturn(Unit)
            whenever(translator.actualTaskList).thenReturn(flow)
            val exp = listForTests.actualizerListOfItemsFromServ
            actualizer.actualizationWork(this)
            val average = flow.first()
            assertEquals(exp, average)
            verify(repository).setTasksToDatabase(listForTests.actualizerListOfItemsFromServ)
        }
    }

//    @Test
//    fun `actualizationWork server is not empty db is not empty server available` () {
//        runBlocking {
//
//            whenever(translator.updateFlag).thenReturn(AtomicBoolean(true))
//            whenever(repository.getTasks()).thenReturn(Pair(listForTests.actualizerListOfItemsFromServ, true))
//            whenever(repository.getTasksFromDatabase()).thenReturn(listForTests.actualizerListOfItems)
//            whenever(actualizer.comparatorDbListAndApiList(listForTests.actualizerListOfItems, listForTests.actualizerListOfItemsFromServ)).thenReturn(Unit)
//            whenever(repository.setTasksToDatabase(emptyList())).thenReturn(Unit)
//            whenever(translator.actualTaskList).thenReturn(flow)
//            val exp = listForTests.actualizerListOfItemsFromServ
//            actualizer.actualizationWork(this)
//            val average = flow.first()
//            assertEquals(exp, average)
//            verify(repository).setTasksToDatabase(listForTests.actualizerListOfItemsFromServ)
//        }
//    }

    @Test
    fun `updateOrAddTask update task` () {
        runBlocking {
            actualizer.actualizationScope = this
            whenever(repository.deleteTask(listForTests.taskToDelete)).thenReturn(listForTests.taskToDelete)
            whenever(repository.deleteTaskFromDatabase(listForTests.taskToDelete)).thenReturn(Unit)
            val average = actualizer.deleteTask(listForTests.taskToDelete)
            val expected = Unit
            assertEquals(expected, average)
            delay(30)
            verify(repository).deleteTask(listForTests.taskToDelete)
            verify(repository).deleteTaskFromDatabase(listForTests.taskToDelete)
        }
    }

    //delete task

    @Test
    fun `task delete server not responded` () {
        runBlocking {
            actualizer.actualizationScope = this
            whenever(repository.deleteTask(listForTests.taskToDelete)).thenReturn(null)
            whenever(repository.updateTaskInDatabase(listForTests.deletedTask)).thenReturn(Unit)
            val average = actualizer.deleteTask(listForTests.taskToDelete)
            val expected = Unit
            assertEquals(expected, average)
            delay(30)
            verify(repository).deleteTask(listForTests.taskToDelete)
            verify(repository).updateTaskInDatabase(listForTests.deletedTask)
        }
    }

    @Test
    fun `task delete server responded` () {
        runBlocking {
            actualizer.actualizationScope = this
            whenever(repository.deleteTask(listForTests.taskToDelete)).thenReturn(listForTests.taskToDelete)
            whenever(repository.deleteTaskFromDatabase(listForTests.taskToDelete)).thenReturn(Unit)
            val average = actualizer.deleteTask(listForTests.taskToDelete)
            val expected = Unit
            assertEquals(expected, average)
            delay(30)
            verify(repository).deleteTask(listForTests.taskToDelete)
            verify(repository).deleteTaskFromDatabase(listForTests.taskToDelete)
        }
    }


}