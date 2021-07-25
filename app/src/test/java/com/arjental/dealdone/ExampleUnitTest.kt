package com.arjental.dealdone

import com.arjental.dealdone.databases.tasksdatabase.TasksDb
import com.arjental.dealdone.models.TaskItem
import com.arjental.dealdone.models.newtworkmodels.ItemFromApi
import com.arjental.dealdone.network.RetrofitInstance
import com.arjental.dealdone.repository.ConverterFromApi
import com.arjental.dealdone.repository.Repository
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Test

import org.junit.Assert.*
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import retrofit2.Response


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    private val testScope =
        CoroutineScope(Dispatchers.IO + CoroutineName("testScope"))

//    private val retrofitInstance: RetrofitInstance = mock()
//    private val tasksDatabase: TasksDb = mock()
//    private val converterFromApi: ConverterFromApi = mock()
//    val repository = Repository(retrofitInstance, tasksDatabase, converterFromApi)
//
//    val translator = Translator()
//
//    @Test
//    fun addition_isCorrect() {
//        assertEquals(4, 2 + 2)
//    }
//
//    @Test
//    fun translator_isCorrect() {
//        whenever(translator.retOne()).thenReturn(2)
//        assertEquals(2, translator.retOne())
//    }

//    @Test
//    fun `testing repository getTasks with empty list from server`() {
//        runBlocking {
//
//            whenever(retrofitInstance.api.getTasks()).thenReturn(Response.error(404, "error".toResponseBody()))
//            val expected = Response.error(404, ResponseBody.Companion.)
//            val actual = retrofitInstance.api.getTasks()
//
////
////            val expected = Response.success(200, emptyList<ItemFromApi>())
////            val actual = retrofitInstance.api.getTasks()
////            assertEquals(expected, actual)
//
////            val expected = Pair(emptyList<TaskItem>(), true)
////            println(expected)
////            val actual = repository.getTasks()
////            println(actual)
////            assertEquals(expected, actual)
//        }
//    }



}