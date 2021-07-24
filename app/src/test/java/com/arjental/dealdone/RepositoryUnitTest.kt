package com.arjental.dealdone

import android.util.Log
import com.arjental.dealdone.databases.tasksdatabase.TasksDb
import com.arjental.dealdone.models.TaskItem
import com.arjental.dealdone.models.newtworkmodels.ItemFromApi
import com.arjental.dealdone.network.RetrofitInstance
import com.arjental.dealdone.network.TasksApi
import com.arjental.dealdone.repository.ConverterFromApi
import com.arjental.dealdone.repository.Repository
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import retrofit2.Response
import java.lang.IllegalStateException

class RepositoryUnitTest {

    private val retrofitInstance: RetrofitInstance = mock()

    private val retrofitInstanceA: TasksApi = mock()
    private val log : Log = mock()
    private val tasksDatabase: TasksDb = mock()
    private val converterFromApi: ConverterFromApi = mock()

    val repository = Repository(retrofitInstance, tasksDatabase, converterFromApi)

    private val listsForTests = ListsForTests()

    @Test
    fun `check repository getTasks with success response` () {
        //null because converter not mocked
        runBlocking {
            whenever(retrofitInstance.api).thenReturn(retrofitInstanceA)
            whenever(retrofitInstanceA.getTasks()).thenReturn(Response.success(listsForTests.elementsFromApi))
            val exp = Pair(null, true)
            val aver = repository.getTasks()
            assertEquals(exp, aver)
        }
    }

    @Test
    fun `check repository getTasks with unsuccessful response` () {
        runBlocking {
            whenever(retrofitInstance.api).thenReturn(retrofitInstanceA)
            whenever(retrofitInstanceA.getTasks()).thenReturn(Response.error(404, "Undefined".toResponseBody()))
            val exp = Pair(emptyList<TaskItem>(), false)
            val aver = repository.getTasks()
            assertEquals(exp, aver)
        }
    }

    @Test
    fun `check repository getTasks with incorrect response` () {
        runBlocking {
            whenever(retrofitInstance.api).thenReturn(retrofitInstanceA)
            whenever(retrofitInstanceA.getTasks()).thenThrow(IllegalStateException("dead"))
            val exp = Pair(emptyList<TaskItem>(), false)
            val aver = repository.getTasks()
            assertEquals(exp, aver)
        }
    }

}