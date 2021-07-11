package com.arjental.dealdone.workmanager

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.arjental.dealdone.Translator
import com.arjental.dealdone.models.ItemState
import com.arjental.dealdone.models.TaskItem
import com.arjental.dealdone.repository.Actualizer
import com.arjental.dealdone.repository.Repository

private const val TAG = "TasksWorkerActualizatio"

class TasksWorkerActualization(val context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {

        Repository.initialize(context)
        val repository = Repository.get()
        Actualizer.initialize()
        val actualizer = Actualizer.get()
        val doWork = Translator.needToUpdate.compareAndSet(true, false)

        if (doWork) {
            val taskListFromDb = repository.getTasksFromDatabase()
            val taskListFromServer = repository.getTasks()
            Log.d(TAG, "11")

            val serverIsEmpty = taskListFromServer.first.isEmpty()
            val serverIsAviliable = taskListFromServer.second
            val dbIsEmpty= taskListFromDb.isNullOrEmpty()

            val state = Triple(serverIsEmpty, serverIsAviliable, dbIsEmpty)

            when (state) {

                Triple(true, false, false) -> {//serv - emty | avil - not | db - not empty
                    actualizer.updateServerTasks(updList = emptyList(), delList = deleteOnServer(taskListFromDb!!))
                    //отправляем только на удаление, мы не знаем ничего об актуальности тасков на серве
                }

                Triple(true, true, false) -> {//serv - emty | avil - yes | db - not empty
                    actualizer.updateServerTasks(updList = setOnServer(taskListFromDb!!), delList = emptyList())
                    //Отправить все, состояние которых не удаленные
                }

                Triple(false, true, true) -> {//serv - not emty | avil - yes | db - empty
                    repository.setTasksToDatabase(taskListFromServer.first)
                    //просто вставляем таски в бз
                }

                Triple(false, true, false) -> {//serv - not emty | avil - yes | db - not empty
                    val list = actualizer.comparatorDbListAndApiList(taskListFromDb!!, taskListFromServer.first)
                    actualizer.updateDatabaseTasks(list[1])
                    actualizer.updateServerTasks(updList = list[2], delList = list[3])
                    //обновляем все
                }

                else -> {
                    //serv - emty | avil - not | db - empty
                    //serv - emty | avil - yes | db - empty
                }

            }
        }

        return Result.success()
    }

    private suspend fun deleteOnServer(taskListFromDb: List<TaskItem>): List<TaskItem> {
        val toDel = mutableListOf<TaskItem>()
        taskListFromDb.forEach {
            if (it.state == ItemState.DELETED) toDel.add(it)
        }
        return toDel
    }

    private suspend fun setOnServer(taskListFromDb: List<TaskItem>): List<TaskItem> {
        val toSet = mutableListOf<TaskItem>()
        taskListFromDb.forEach {
            if (it.state == ItemState.EXIST) toSet.add(it)
        }
        return toSet
    }

}