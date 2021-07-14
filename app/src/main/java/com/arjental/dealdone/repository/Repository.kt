package com.arjental.dealdone.repository

import android.content.Context
import android.util.Log
import com.arjental.dealdone.databases.tasksdatabase.TasksDb
import com.arjental.dealdone.models.TaskItem
import com.arjental.dealdone.models.newtworkmodels.ItemFromApi
import com.arjental.dealdone.models.newtworkmodels.SyncItemsFromApi
import com.arjental.dealdone.network.RetrofitInstance
import com.arjental.dealdone.repository.interfaces.RepositoryInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton


private const val TAG = "Repository"

@Singleton
class Repository @Inject constructor(context: Context, tasksConstDatabase: TasksDb, converterConstFromApi: ConverterFromApi): RepositoryInterface {

    private val tasksDatabase = tasksConstDatabase
    private val converterFromApi = converterConstFromApi

//    private lateinit var tasksDatabase: TasksDb
//    private lateinit var converterFromApi: ConverterFromApi
//
//    init {
//        TasksDb.initialize(context)
//        tasksDatabase = TasksDb.get()
//        ConverterFromApi.initialize()
//        converterFromApi = ConverterFromApi.get()
//    }

    //Database

    suspend fun setTasksToDatabase(list: List<TaskItem>) = tasksDatabase.setTasks(list)

    suspend fun updateTaskInDatabase(item: TaskItem) = tasksDatabase.updateTask(item)

    suspend fun addTaskToDatabase(item: TaskItem) = tasksDatabase.addTask(item)

    suspend fun getTasksFromDatabase() = tasksDatabase.getTasks()

    suspend fun getTaskFromDatabase(uuid: UUID) = tasksDatabase.getTask(uuid)

    suspend fun deleteTaskFromDatabase(item: TaskItem) = tasksDatabase.deleteTask(item)

    //API

    suspend fun getTasks(): Pair<List<TaskItem>, Boolean> = try {
        val list = RetrofitInstance.api.getTasks()
        if (list.isSuccessful) {
            Pair(converterFromApi.convertFromApiTaskListToTaskItemList(list.body()), true)
        } else {
            Log.w(TAG, "getTasks = " + list.code().toString())
            Pair(emptyList(), false)
        }
    } catch (e: Exception) {
        Log.e(TAG, " getTasks EXCEPTION $e")
        Pair(emptyList(), false)
    }

    suspend fun getTasksDemo(): Pair<List<TaskItem>, Boolean> = try {
        val list: Call<List<ItemFromApi>> = RetrofitInstance.api.getTasksDemo()

        list.enqueue(object : Callback<List<ItemFromApi>> {
            override fun onResponse(
                call: Call<List<ItemFromApi>>,
                response: Response<List<ItemFromApi>>
            ) {
                TODO("Not yet implemented")
            }

            override fun onFailure(call: Call<List<ItemFromApi>>, t: Throwable) {
                TODO("Not yet implemented")
            }

        } )

//        if (list.) {
//            Pair(converterFromApi.convertFromApiTaskListToTaskItemList(list.body()), true)
//        } else {
//            Log.w(TAG, "getTasks = " + list.code().toString())
//            Pair(emptyList(), false)
//        }
        TODO()
        Pair(emptyList(), false)
    } catch (e: Exception) {
        Log.e(TAG, " getTasks EXCEPTION $e")
        Pair(emptyList(), false)
    }


    suspend fun pushTask(setTask: TaskItem): TaskItem? {
        try {
            val changedItem = RetrofitInstance.api.pushTask(converterFromApi.convertFromTaskItemToApiItem(setTask))
            if (changedItem.isSuccessful) {
                return converterFromApi.convertFromApiItemToTaskItem(changedItem.body())
            } else {
                Log.w(TAG, "pushTask = "  + changedItem.code().toString())
            }
        } catch (e: Exception) {
            Log.e(TAG, "pushTask EXCEPTION $e")
        }
        return null
    }

    suspend fun updateTask(taskToUpdate: TaskItem): TaskItem? {
        try {
            val item = RetrofitInstance.api.updateTask(
                taskId = taskToUpdate.id.toString(),
                task = converterFromApi.convertFromTaskItemToApiItem(taskToUpdate)
            )
            if (item.isSuccessful) {
                return converterFromApi.convertFromApiItemToTaskItem(item.body())
            } else {
                Log.w(TAG, "updateTask = "  + item.code().toString())
            }
        } catch (e: Exception) {
            Log.e(TAG, "updateTask EXCEPTION $e")
        }
        return null
    }

    suspend fun deleteTask(item: TaskItem): TaskItem? {
        try {
            val list = RetrofitInstance.api.deleteTask(item.id.toString())
            if (list.isSuccessful) {
                return converterFromApi.convertFromApiItemToTaskItem(list.body())
            } else {
                Log.w(TAG, "deleteTask = "  + list.code().toString())
            }
        } catch (e: Exception) {
            Log.e(TAG, "deleteTask EXCEPTION $e")
        }
        return null
    }

    suspend fun syncTasks(toDelete: List<String>, toUpdate: List<TaskItem>): List<TaskItem> {
        try {
            val list = RetrofitInstance.api.synchronizedTasks(
                SyncItemsFromApi(
                    deleted = toDelete,
                    other = converterFromApi.convertFromTaskItemListToApiTaskList(toUpdate)
                )
            )
            if (list.isSuccessful) {
                return converterFromApi.convertFromApiTaskListToTaskItemList(list.body())
            } else {
                Log.w(TAG, "syncTasks = "  + list.code().toString())
            }
        } catch (e: Exception) {
            Log.e(TAG, "syncTasks EXCEPTION $e")
        }
        return emptyList()
    }

    companion object {
        private var INSTANCE: Repository? = null
        fun initialize(context: Context) {
            if (INSTANCE == null) {
                TasksDb.initialize(context)
                ConverterFromApi.initialize()
                INSTANCE = Repository(context, TasksDb.get(), ConverterFromApi.get())
            }
        }

        fun get(): Repository {
            return INSTANCE ?: throw IllegalStateException("Repository first must be initialized")
        }
    }

}