package com.arjental.dealdone.repository

import android.util.Log
import com.arjental.dealdone.Translator
import com.arjental.dealdone.models.ItemState
import com.arjental.dealdone.models.TaskItem
import kotlinx.coroutines.*
import java.util.*

private const val TAG = "Actualizer"

class Actualizer {

    private val actualizationScope =
        CoroutineScope(Dispatchers.IO + CoroutineName("ActualizationScope"))
    private val repository = Repository.get()

    fun actualize() {

        actualizationScope.launch {
            val doWork = Translator.needToUpdate.compareAndSet(true, false)
            if (doWork) {
                val taskListFromServerA = async { repository.getTasks() }
                val taskListFromDbA = async { repository.getTasksFromDatabase() }
                val taskListFromDb = taskListFromDbA.await()
                val taskListFromServer = taskListFromServerA.await()

                val serverIsEmpty = taskListFromServer.first.isEmpty()
                val serverIsAviliable = taskListFromServer.second
                val dbIsEmpty= taskListFromDb.isNullOrEmpty()

                val state = Triple(serverIsEmpty, serverIsAviliable, dbIsEmpty)

                Log.d(TAG, "1")

                when (state) {

                    Triple(true, false, false) -> {//serv - emty | avil - not | db - not empty
                        Log.d(TAG, "2")
                        val listWithoutDeleted = notDeleted(taskListFromDb!!)
                        withContext (Dispatchers.Main) { Translator.taskList.value = listWithoutDeleted }
                        updateServerTasks(updList = emptyList(), delList = deleteOnServer(taskListFromDb))
                        //отправляем только на удаление, мы не знаем ничего об актуальности тасков на серве
                    }

                    Triple(true, true, false) -> {//serv - emty | avil - yes | db - not empty
                        Log.d(TAG, "3")
                        val listWithoutDeleted = notDeleted(taskListFromDb!!)
                        withContext (Dispatchers.Main) { Translator.taskList.value = listWithoutDeleted }
                        updateServerTasks(updList = listWithoutDeleted, delList = emptyList())
                        //Отправить все, состояние которых не удаленные
                    }

                    Triple(false, true, true) -> {//serv - not emty | avil - yes | db - empty
                        Log.d(TAG, "4")
                        withContext (Dispatchers.Main) { Translator.taskList.value = taskListFromServer.first }
                        repository.setTasksToDatabase(taskListFromServer.first)
                        //просто вставляем таски в бз
                    }

                    Triple(false, true, false) -> {//serv - not emty | avil - yes | db - not empty
                        Log.d(TAG, "5")
                        val list = comparatorDbListAndApiList(taskListFromDb!!, taskListFromServer.first)
                        withContext (Dispatchers.Main) { Translator.taskList.value = list[0] }
                        updateDatabaseTasks(list[1])
                        updateServerTasks(updList = list[2], delList = list[3])
                        //обновляем все
                    }

                    else -> {
                        withContext (Dispatchers.Main) { Translator.taskList.value = emptyList() }
                        Log.d(TAG, "6")
                        //serv - emty | avil - not | db - empty
                        //serv - emty | avil - yes | db - empty
                    }
                }
            }
        }



//        actualizationScope.launch {
//            val taskListFromServerA = async { repository.getTasks() }
//            val taskListFromDbA = async { repository.getTasksFromDatabase() }
//            val taskListFromDb = taskListFromDbA.await()
//            val taskListFromServer = taskListFromServerA.await()
//            Log.d(TAG, "1")
//            if (taskListFromServer.second) { //if we got something from server
//                if (taskListFromDb == null || taskListFromDb == emptyList<TaskItem>()) { //if local database is empty
//                    if (taskListFromServer.first != emptyList<TaskItem>()) { //if local database is empty, list from server isn't
//                        Log.d(TAG, "2")
//                        withContext (Dispatchers.Main) { Translator.taskList.value = taskListFromServer.first }
//                        repository.setTasksToDatabase(taskListFromServer.first)
//                    } else { //if local database is empty, list from server empty too
//                        Log.d(TAG, "3")
//                        withContext (Dispatchers.Main) { Translator.taskList.value = emptyList() }
//                    }
//                } else { //if local database is not empty
//                    if (taskListFromServer.first != emptyList<TaskItem>()) {//if local database is not empty, server is not empty
//                        Log.d(TAG, "4")
//                        val actualList =
//                            comparatorDbListAndApiList(taskListFromDb, taskListFromServer.first)
//                        withContext (Dispatchers.Main) { Translator.taskList.value = actualList[0] }
//                        val defDb = async {
//                            if (actualList[1].isNotEmpty()) {
//                                updateDatabaseTasks(actualList[1])
//                            }
//                        }
//                        val defServ = async {
//                            if (actualList[2].isNotEmpty() || actualList[3].isNotEmpty()) {
//                                updateServerTasks(updList = actualList[2], delList = actualList[3])
//                            }
//                        }
//                        defDb.await()
//                        defServ.await()
//                    } else {//if local database is not empty, server is empty
//                        Log.d(TAG, "5")
//                        withContext (Dispatchers.Main) { Translator.taskList.value = taskListFromDb }
//                        addTasksToServer(taskListFromDb)
//                    }
//                }
//            } else { //we doesn't got something from server, work local
//                if (taskListFromDb == null || taskListFromDb == emptyList<TaskItem>()) {
//                    Log.d(TAG, "6")
//                    withContext (Dispatchers.Main) { Translator.taskList.value = emptyList() }
//                } else {
//                    Log.d(TAG, "7")
//                    withContext (Dispatchers.Main) { Translator.taskList.value = taskListFromDb }
//                }
//            }
//            this.cancel()
//        }
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

        Log.d(TAG, "UPD")

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

//    suspend fun addTasksToServer(list: List<TaskItem>) {
//        repository.syncTasks(emptyList(), list)
//    }

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

    companion object {
        private var INSTANCE: Actualizer? = null
        fun initialize() {
            if (INSTANCE == null) {
                INSTANCE = Actualizer()
            }
        }

        fun get(): Actualizer {
            return INSTANCE ?: throw IllegalStateException("Actualizer first must be initialized")
        }
    }

}