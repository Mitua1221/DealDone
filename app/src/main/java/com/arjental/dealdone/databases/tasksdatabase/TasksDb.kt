package com.arjental.dealdone.databases.tasksdatabase

import android.content.Context
import androidx.room.Room
import com.arjental.dealdone.models.TaskItem
import java.util.*

class TasksDb(context: Context) {

    private val database: TasksDatabase = Room.databaseBuilder(
        context.applicationContext,
        TasksDatabase::class.java,
        DATABASE_NAME
    ).build()

    private val tasksDao = database.taskDao()

    suspend fun setTasks(list: List<TaskItem>) = tasksDao.addTasks(list)

    suspend fun updateTask(item: TaskItem) = tasksDao.updateTask(item)

    suspend fun addTask(item: TaskItem) = tasksDao.addTask(item)

    suspend fun getTasks() = tasksDao.getTasks()

    suspend fun getTask(uuid: UUID) = tasksDao.getTask(uuid)

    suspend fun deleteTask(item: TaskItem) = tasksDao.deleteTask(item)

    companion object {
        private const val DATABASE_NAME = "tasks-database"
        private var INSTANCE: TasksDb? = null
        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = TasksDb(context)
            }
        }

        fun get(): TasksDb {
            return INSTANCE ?: throw IllegalStateException("TasksDb first must be initialized")
        }
    }

}