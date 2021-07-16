package com.arjental.dealdone.databases.tasksdatabase.interfaces

import com.arjental.dealdone.models.TaskItem
import java.util.*

interface TasksDbInterface {

    suspend fun setTasks(list: List<TaskItem>)
    suspend fun updateTask(item: TaskItem)
    suspend fun addTask(item: TaskItem)
    suspend fun getTasks(): List<TaskItem>?
    suspend fun getTask(uuid: UUID): TaskItem?
    suspend fun deleteTask(item: TaskItem)

}