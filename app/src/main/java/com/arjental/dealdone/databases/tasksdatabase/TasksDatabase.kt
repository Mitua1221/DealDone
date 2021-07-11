package com.arjental.dealdone.databases.tasksdatabase

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.arjental.dealdone.models.TaskItem

@Database(entities = [ TaskItem::class ], version=1, exportSchema = false)
@TypeConverters(TasksTypeConverter::class)
abstract class TasksDatabase : RoomDatabase() {

    abstract fun taskDao(): TasksDao

}
