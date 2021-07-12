package com.arjental.dealdone.repository

import com.arjental.dealdone.models.ItemState
import com.arjental.dealdone.models.TaskItem
import com.arjental.dealdone.models.TaskItemPriorities
import com.arjental.dealdone.models.newtworkmodels.ItemFromApi
import com.arjental.dealdone.repository.interfaces.ConverterFromApiInterface
import java.lang.IllegalArgumentException
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConverterFromApi @Inject constructor(): ConverterFromApiInterface {

    suspend inline fun convertFromApiTaskListToTaskItemList(body: List<ItemFromApi>?): List<TaskItem> {
        if (body.isNullOrEmpty()) return emptyList() else {
            val list = mutableListOf<TaskItem>()
            body.forEach {
                list.add(
                    convertFromApiItemToTaskItem(it)!!
                )
            }
            return list
        }
    }

    suspend inline fun convertFromTaskItemListToApiTaskList(body: List<TaskItem>?): List<ItemFromApi> {
        if (body.isNullOrEmpty()) return emptyList() else {
            val list = mutableListOf<ItemFromApi>()
            body.forEach {
                list.add(
                    convertFromTaskItemToApiItem(it)
                )
            }
            return list
        }
    }

    suspend inline fun convertFromTaskItemToApiItem(item: TaskItem): ItemFromApi {
        return ItemFromApi(
            id = item.id.toString(),
            text = item.text,
            importance = setImportaceToApi(item.priority),
            done = item.isSolved,
            deadline = (item.deadline ?: 0) / 1000 ,
            created_at = (item.createDate) / 1000 ,
            updated_at = (item.updateDate) / 1000 ,
        )
    }

    suspend inline fun convertFromApiItemToTaskItem(item: ItemFromApi?): TaskItem? {
        return if (item != null) {
            TaskItem(
                id = UUID.fromString(item.id),
                isSolved = item.done,
                text = item.text,
                priority = setImportanceToTask(item.importance),
                deadline = if (item.deadline * 1000 == 0L) null else item.deadline * 1000,
                state = ItemState.EXIST,
                createDate = item.created_at * 1000,
                updateDate = item.updated_at * 1000,
            )
        } else null
    }

    suspend inline fun setImportaceToApi(tip: TaskItemPriorities) =
        when (tip) {
            TaskItemPriorities.NONE -> "low"
            TaskItemPriorities.MEDIUM -> "basic"
            TaskItemPriorities.HIGH -> "important"
            else -> throw IllegalArgumentException("unreal Task Priority setImportaceToApi")
        }

    suspend inline fun setImportanceToTask(importance: String) =
        when (importance) {
            "low" -> TaskItemPriorities.NONE
            "basic" -> TaskItemPriorities.MEDIUM
            "important" -> TaskItemPriorities.HIGH
            else -> throw IllegalArgumentException("unreal Task Priority")
        }

    suspend inline fun currentDate() = Calendar.getInstance().time.time

    companion object {
        private var INSTANCE: ConverterFromApi? = null
        fun initialize() {
            if (INSTANCE == null) {
                INSTANCE = ConverterFromApi()
            }
        }

        fun get(): ConverterFromApi {
            return INSTANCE ?: throw IllegalStateException("ConverterFromApi first must be initialized")
        }
    }

}