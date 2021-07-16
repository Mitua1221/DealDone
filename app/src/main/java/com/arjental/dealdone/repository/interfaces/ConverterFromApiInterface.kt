package com.arjental.dealdone.repository.interfaces

import com.arjental.dealdone.models.TaskItem
import com.arjental.dealdone.models.TaskItemPriorities
import com.arjental.dealdone.models.newtworkmodels.ItemFromApi

interface ConverterFromApiInterface {

    suspend fun convertFromApiTaskListToTaskItemList(body: List<ItemFromApi>?): List<TaskItem>
    suspend fun convertFromTaskItemListToApiTaskList(body: List<TaskItem>?): List<ItemFromApi>
    suspend fun convertFromTaskItemToApiItem(item: TaskItem): ItemFromApi
    suspend fun convertFromApiItemToTaskItem(item: ItemFromApi?): TaskItem?
    suspend fun setImportaceToApi(tip: TaskItemPriorities): String
    suspend fun setImportanceToTask(importance: String): TaskItemPriorities
    suspend fun currentDate(): Long
}