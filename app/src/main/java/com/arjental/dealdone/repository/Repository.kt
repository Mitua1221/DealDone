package com.arjental.dealdone.repository

import android.util.Log
import com.arjental.dealdone.databases.tasksdatabase.TasksDb
import com.arjental.dealdone.exceptions.ExceptionsHandler
import com.arjental.dealdone.models.TaskItem
import com.arjental.dealdone.models.newtworkmodels.SyncItemsFromApi
import com.arjental.dealdone.network.RetrofitInstance
import com.arjental.dealdone.repository.interfaces.RepositoryInterface
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "Repository"

@Singleton
class Repository @Inject constructor(
    val retrofit: RetrofitInstance,
    val tasksDatabase: TasksDb,
    val converterFromApi: ConverterFromApi,
    val exceptionsHandler: ExceptionsHandler,
) : RepositoryInterface {

    private enum class MethodsAPI {
        GETTASKS, PUSHTASK, UPDATETASK, DELETETASK, SYNCTASKS
    }

    //Database

    override suspend fun setTasksToDatabase(list: List<TaskItem>) = tasksDatabase.setTasks(list)

    override suspend fun updateTaskInDatabase(item: TaskItem) = tasksDatabase.updateTask(item)

    override suspend fun addTaskToDatabase(item: TaskItem) = tasksDatabase.addTask(item)

    override suspend fun getTasksFromDatabase() = tasksDatabase.getTasks()

    override suspend fun getTaskFromDatabase(uuid: UUID) = tasksDatabase.getTask(uuid)

    override suspend fun deleteTaskFromDatabase(item: TaskItem) = tasksDatabase.deleteTask(item)

    //API

    override suspend fun getTasks(): Pair<List<TaskItem>, Boolean> = try {
        val list = retrofit.api.getTasks()
        if (list.isSuccessful) {
            Pair(converterFromApi.convertFromApiTaskListToTaskItemList(list.body()), true)
        } else {
            exceptionsHandler.unsuccessfulResponse(TAG, list.code().toString(), MethodsAPI.GETTASKS.name)
            Pair(emptyList(), false)
        }
    } catch (e: Exception) {
        exceptionsHandler.exceptionInResponse(TAG, e, MethodsAPI.GETTASKS.name)
        Pair(emptyList(), false)
    }

//    suspend fun getTasksDemo(): Pair<List<TaskItem>, Boolean> = try {
//        val list: Call<List<ItemFromApi>> = retrofit.api.getTasksDemo()
//
//        list.enqueue(object : Callback<List<ItemFromApi>> {
//            override fun onResponse(
//                call: Call<List<ItemFromApi>>,
//                response: Response<List<ItemFromApi>>
//            ) {
//                TODO("Not yet implemented")
//            }
//
//            override fun onFailure(call: Call<List<ItemFromApi>>, t: Throwable) {
//                TODO("Not yet implemented")
//            }
//
//        })
//
////        if (list.) {
////            Pair(converterFromApi.convertFromApiTaskListToTaskItemList(list.body()), true)
////        } else {
////            Log.w(TAG, "getTasks = " + list.code().toString())
////            Pair(emptyList(), false)
////        }
//        TODO()
//        Pair(emptyList(), false)
//    } catch (e: Exception) {
//        Log.e(TAG, " getTasks EXCEPTION $e")
//        Pair(emptyList(), false)
//    }

    override suspend fun pushTask(setTask: TaskItem): TaskItem? {
        try {
            val changedItem =
                retrofit.api.pushTask(converterFromApi.convertFromTaskItemToApiItem(setTask))
            if (changedItem.isSuccessful) {
                return converterFromApi.convertFromApiItemToTaskItem(changedItem.body())
            } else {
                exceptionsHandler.unsuccessfulResponse(TAG, changedItem.code().toString(), MethodsAPI.PUSHTASK.name)
            }
        } catch (e: Exception) {
            exceptionsHandler.exceptionInResponse(TAG, e, MethodsAPI.PUSHTASK.name)
        }
        return null
    }

    override suspend fun updateTask(taskToUpdate: TaskItem): TaskItem? {
        try {
            val item = retrofit.api.updateTask(
                taskId = taskToUpdate.id.toString(),
                task = converterFromApi.convertFromTaskItemToApiItem(taskToUpdate)
            )
            if (item.isSuccessful) {
                return converterFromApi.convertFromApiItemToTaskItem(item.body())
            } else {
                exceptionsHandler.unsuccessfulResponse(TAG, item.code().toString(), MethodsAPI.UPDATETASK.name)
            }
        } catch (e: Exception) {
            exceptionsHandler.exceptionInResponse(TAG, e, MethodsAPI.UPDATETASK.name)
        }
        return null
    }

    override suspend fun deleteTask(item: TaskItem): TaskItem? {
        try {
            val list = retrofit.api.deleteTask(item.id.toString())
            if (list.isSuccessful) {
                return converterFromApi.convertFromApiItemToTaskItem(list.body())
            } else {
                exceptionsHandler.unsuccessfulResponse(TAG, list.code().toString(), MethodsAPI.DELETETASK.name)
            }
        } catch (e: Exception) {
            exceptionsHandler.exceptionInResponse(TAG, e, MethodsAPI.DELETETASK.name)
        }
        return null
    }

    override suspend fun syncTasks(
        toDelete: List<String>,
        toUpdate: List<TaskItem>
    ): List<TaskItem> {
        try {
            val list = retrofit.api.synchronizedTasks(
                SyncItemsFromApi(
                    deleted = toDelete,
                    other = converterFromApi.convertFromTaskItemListToApiTaskList(toUpdate)
                )
            )
            if (list.isSuccessful) {
                return converterFromApi.convertFromApiTaskListToTaskItemList(list.body())
            } else {
                exceptionsHandler.unsuccessfulResponse(TAG, list.code().toString(), MethodsAPI.SYNCTASKS.name)
            }
        } catch (e: Exception) {
            exceptionsHandler.exceptionInResponse(TAG, e, MethodsAPI.SYNCTASKS.name)
        }
        return emptyList()
    }


}