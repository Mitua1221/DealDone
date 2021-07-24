package com.arjental.dealdone

import androidx.room.PrimaryKey
import com.arjental.dealdone.models.ItemState
import com.arjental.dealdone.models.TaskItem
import com.arjental.dealdone.models.TaskItemPriorities
import com.arjental.dealdone.models.newtworkmodels.ItemFromApi
import com.arjental.dealdone.repository.ConverterFromApi
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.util.*

class ConverterUnitTests {

    private val converter = ConverterFromApi()
    private val listsForTests = ListsForTests()

    private val elementsFromApi = listsForTests.elementsFromApi

    private val expectedFromConverter = listsForTests.expectedFromConverter

    @Test
    private fun `empty list to convertFromApiTaskListToTaskItemList method` () {
        runBlocking {
            val expected = emptyList<TaskItem>()
            val actual = converter.convertFromApiTaskListToTaskItemList(emptyList<ItemFromApi>())
            assertEquals(expected, actual)
        }
    }

    @Test
    fun `null to convertFromApiTaskListToTaskItemList method` () {
        runBlocking {
            val expected = emptyList<TaskItem>()
            val actual = converter.convertFromApiTaskListToTaskItemList(null)
            assertEquals(expected, actual)
        }
    }

    @Test
    fun `first element to convertFromApiTaskListToTaskItemList method` () {
        runBlocking {
            val expected = listOf(expectedFromConverter[0])
            val actual = converter.convertFromApiTaskListToTaskItemList(listOf(elementsFromApi[0]))
            assertEquals(expected, actual)
        }
    }

    @Test
    fun `second element to convertFromApiTaskListToTaskItemList method` () {
        runBlocking {
            val expected = listOf(expectedFromConverter[0])
            val actual = converter.convertFromApiTaskListToTaskItemList(listOf(elementsFromApi[0]))
            assertEquals(expected, actual)
        }
    }

    @Test
    fun `third element to convertFromApiTaskListToTaskItemList method` () {
        runBlocking {
            val expected = listOf(expectedFromConverter[0])
            val actual = converter.convertFromApiTaskListToTaskItemList(listOf(elementsFromApi[0]))
            assertEquals(expected, actual)
        }
    }

    @Test
    fun `list of element to convertFromApiTaskListToTaskItemList method` () {
        runBlocking {
            val expected = expectedFromConverter
            val actual = converter.convertFromApiTaskListToTaskItemList(elementsFromApi)
            assertEquals(expected, actual)
        }
    }

}