package com.arjental.dealdone.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class TaskItem(
    @PrimaryKey val id: UUID,
    var isSolved: Boolean,
    var text: String,
    var priority: TaskItemPriorities,
    var deadline: Long? = null,
    var state: ItemState,
    var createDate: Long,
    var updateDate: Long,
)

