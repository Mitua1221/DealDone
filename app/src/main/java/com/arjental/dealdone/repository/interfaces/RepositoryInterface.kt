package com.arjental.dealdone.repository.interfaces

import com.arjental.dealdone.databases.tasksdatabase.TasksDb
import com.arjental.dealdone.models.TaskItem
import com.arjental.dealdone.network.RetrofitInstance
import com.arjental.dealdone.repository.ConverterFromApi
import java.util.*

interface RepositoryInterface {

    //Database

    suspend fun setTasksToDatabase(list: List<TaskItem>)
    suspend fun updateTaskInDatabase(item: TaskItem)
    suspend fun addTaskToDatabase(item: TaskItem)
    suspend fun getTasksFromDatabase(): List<TaskItem>?
    suspend fun getTaskFromDatabase(uuid: UUID): TaskItem?
    suspend fun deleteTaskFromDatabase(item: TaskItem)

    //Networking

    suspend fun getTasks(): Pair<List<TaskItem>, Boolean>
    suspend fun pushTask(setTask: TaskItem): TaskItem?
    suspend fun updateTask(taskToUpdate: TaskItem): TaskItem?
    suspend fun deleteTask(item: TaskItem): TaskItem?
    suspend fun syncTasks(toDelete: List<String>, toUpdate: List<TaskItem>): List<TaskItem>

}