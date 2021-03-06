package com.arjental.dealdone.databases.tasksdatabase

import androidx.room.TypeConverter
import java.util.*

class TasksTypeConverter () {

//    @TypeConverter
//    fun fromDate(date: Date?): Long? {
//        return date?.time
//    }
//
//    @TypeConverter
//    fun toDate(millisSinceEpoch: Long?): Date? {
//        return millisSinceEpoch?.let {
//            Date(it)
//        }
//    }

    //UUID
    @TypeConverter
    fun toUUID(uuid: String): UUID {
        return UUID.fromString(uuid)
    }

    @TypeConverter
    fun fromUUID(uuid: UUID): String {
        return uuid.toString()
    }

}