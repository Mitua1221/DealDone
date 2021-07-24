package com.arjental.dealdone.repository

import android.util.Log
import com.arjental.dealdone.Translator
import com.arjental.dealdone.models.ItemState
import com.arjental.dealdone.models.TaskItem
import com.arjental.dealdone.repository.interfaces.ActualizerInterface
import kotlinx.coroutines.*
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "Actualizer"

@Singleton
class Actualizer @Inject constructor(): ActualizerInterface {

    var actualizationScope =
        CoroutineScope(Dispatchers.IO + CoroutineName("ActualizationScope"))

    @Inject lateinit var repository: Repository

    @Inject lateinit var translator: Translator

    fun actualize() {
        actualizationScope.launch {
            actualizationWork(this)
        }
    }

    suspend fun actualizationWork(coroutineScope: CoroutineScope) {
        val doWork = translator.updateFlag.compareAndSet(true, false)
        if (doWork) {
            val taskListFromServerA = coroutineScope.async { repository.getTasks() }
            val taskListFromDbA = coroutineScope.async { repository.getTasksFromDatabase() }
            val taskListFromDb = taskListFromDbA.await()
            val taskListFromServer = taskListFromServerA.await()

            val serverIsEmpty = taskListFromServer.first.isEmpty()
            val serverIsAviliable = taskListFromServer.second
            val dbIsEmpty= taskListFromDb.isNullOrEmpty()

            val state = Triple(serverIsEmpty, serverIsAviliable, dbIsEmpty)

            when (state) {

                Triple(true, false, false) -> {//serv - emty | avil - not | db - not empty
                    val listWithoutDeleted = notDeleted(taskListFromDb!!)
                    translator.actualTaskList.emit(listWithoutDeleted)
                    updateServerTasks(updList = emptyList(), delList = deleteOnServer(taskListFromDb))
                    //отправляем только на удаление, мы не знаем ничего об актуальности тасков на серве
                }

                Triple(true, true, false) -> {//serv - emty | avil - yes | db - not empty
                    val listWithoutDeleted = notDeleted(taskListFromDb!!)
                    translator.actualTaskList.emit(listWithoutDeleted)
                    updateServerTasks(updList = listWithoutDeleted, delList = emptyList())
                    //Отправить все, состояние которых не удаленные
                }

                Triple(false, true, true) -> {//serv - not emty | avil - yes | db - empty
                    translator.actualTaskList.emit(taskListFromServer.first)
                    repository.setTasksToDatabase(taskListFromServer.first)
                    //просто вставляем таски в бз
                }

                Triple(false, true, false) -> {//serv - not emty | avil - yes | db - not empty
                    val list = comparatorDbListAndApiList(taskListFromDb!!, taskListFromServer.first)
                    translator.actualTaskList.emit(list[0])
                    updateDatabaseTasks(list[1])
                    updateServerTasks(updList = list[2], delList = list[3])
                    //обновляем все
                }

                else -> {
                    translator.actualTaskList.emit(emptyList())
                    //serv - emty | avil - not | db - empty
                    //serv - emty | avil - yes | db - empty
                }
            }
        }
    }

    private suspend fun notDeleted(taskListFromDb: List<TaskItem>) = taskListFromDb.filterNot { it.state == ItemState.DELETED }

    suspend fun updateDatabaseTasks(list: List<TaskItem>) {
        list.forEach {

            repository.updateTaskInDatabase(it)
        }
    }

    private suspend fun deleteOnServer(taskListFromDb: List<TaskItem>): List<TaskItem> {
        val toDel = mutableListOf<TaskItem>()
        taskListFromDb.forEach {
            if (it.state == ItemState.DELETED) toDel.add(it)
        }
        return toDel
    }

    suspend fun updateServerTasks(updList: List<TaskItem>, delList: List<TaskItem>) {
        val deletedTasksIds = mutableListOf<String>()
        delList.forEach { deletedTasksIds.add(it.id.toString()) }
        val s = repository.syncTasks(
            deletedTasksIds,
            updList
        )
        if (!s.isNullOrEmpty()) {
            delList.forEach { repository.deleteTaskFromDatabase(it) }
        }
    }

    suspend fun comparatorDbListAndApiList(
        taskListFromDb: List<TaskItem>,
        taskListFromServer: List<TaskItem>,
    ): MutableList<List<TaskItem>> {
        val actualPasteListInApplication = mutableListOf<TaskItem>()
        val listToUpdateInDb = mutableListOf<TaskItem>()
        val listToUpdateOnServer = mutableListOf<TaskItem>()
        val listToDeleteOnServer = mutableListOf<TaskItem>()
        val map: MutableMap<UUID, TaskItem> =
            taskListFromServer.associateBy { it.id } as MutableMap<UUID, TaskItem>
        taskListFromDb.forEach {
            if (it.state == ItemState.DELETED) {
                listToDeleteOnServer.add(it)
            } else if (map[it.id] != null) { //task exists in db, task exists on server
                if (it.updateDate >= map[it.id]!!.updateDate) {
                    //new in db
                    actualPasteListInApplication.add(it)
                    listToUpdateOnServer.add(it)
                } else {
                    //new in server
                    actualPasteListInApplication.add(map[it.id]!!)
                    listToUpdateInDb.add(map[it.id]!!)
                }
                map.remove(it.id)
            } else { //task exists in db, not exists on server
                actualPasteListInApplication.add(it)
                listToUpdateOnServer.add(it)
            }
        }
        if (map.isNotEmpty()) {
            actualPasteListInApplication.addAll(map.values)
        }
        return mutableListOf(
            actualPasteListInApplication,
            listToUpdateInDb,
            listToUpdateOnServer,
            listToDeleteOnServer,
        )
    }

    fun updateOrAddTask(task: TaskItem) {
        actualizationScope.launch {
            val db = async {
                val e = repository.getTaskFromDatabase(task.id)
                if (e == null) {
                    repository.addTaskToDatabase(task)
                } else {
                    repository.updateTaskInDatabase(task)
                }
            }
            val serv = async {
                repository.syncTasks(emptyList(), listOf(task))
            }
        }
    }

    fun deleteTask(task: TaskItem) {
        actualizationScope.launch {
            val ret = repository.deleteTask(task)
            if (ret == null) {
                task.state = ItemState.DELETED
                repository.updateTaskInDatabase(task)
            } else repository.deleteTaskFromDatabase(task)
        }
    }

}