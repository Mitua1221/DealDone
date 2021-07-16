package com.arjental.dealdone.databases.tasksdatabase

import android.content.Context
import androidx.room.Room
import com.arjental.dealdone.databases.tasksdatabase.interfaces.TasksDbInterface
import com.arjental.dealdone.models.TaskItem
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TasksDb @Inject constructor(context: Context): TasksDbInterface {

    private val database: TasksDatabase = Room.databaseBuilder(
        context.applicationContext,
        TasksDatabase::class.java,
        DATABASE_NAME
    ).build()

    private val tasksDao = database.taskDao()

    override suspend fun setTasks(list: List<TaskItem>) = tasksDao.addTasks(list)

    override suspend fun updateTask(item: TaskItem) = tasksDao.updateTask(item)

    override suspend fun addTask(item: TaskItem) = tasksDao.addTask(item)

    override suspend fun getTasks() = tasksDao.getTasks()

    override suspend fun getTask(uuid: UUID) = tasksDao.getTask(uuid)

    override suspend fun deleteTask(item: TaskItem) = tasksDao.deleteTask(item)

    companion object {
        private const val DATABASE_NAME = "tasks-database"
    }

}