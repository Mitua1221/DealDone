package com.arjental.dealdone.databases.tasksdatabase

import androidx.room.*
import com.arjental.dealdone.models.TaskItem
import java.util.*

@Dao
interface TasksDao {

    //Get All Table
    @Query("SELECT * FROM taskitem")
    suspend fun getTasks(): List<TaskItem>?

    //Get Specific TaskItem
    @Query("SELECT * FROM taskitem WHERE id=(:uuid)")
    suspend fun getTask(uuid: UUID): TaskItem?

    //Update Specific TaskItem
    @Update
    fun updateTask(item: TaskItem)

    //Insert TaskItem
    @Insert
    fun addTask(item: TaskItem)

    //Insert list of TaskItem
    @Insert
    fun addTasks(item: List<TaskItem>)

    //Delete TaskItem from DB
    @Delete
    fun deleteTask(item: TaskItem)

}