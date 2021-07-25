package com.arjental.dealdone.workmanager

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.arjental.dealdone.Translator
import com.arjental.dealdone.di.ContextInjection
import com.arjental.dealdone.repository.Actualizer
import com.arjental.dealdone.repository.Repository
import javax.inject.Inject

private const val TAG = "TasksWorkerActualizatio"

class TasksWorkerActualization(val context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    @Inject
    lateinit var repository: Repository

    @Inject
    lateinit var actualizer: Actualizer

    @Inject
    lateinit var translator: Translator

    init {
        ContextInjection.inject(to = this, with = context)
    }

    override suspend fun doWork(): Result {

        val doWork = translator.updateFlag.compareAndSet(true, false)

        if (doWork) {

            val taskListFromDb = repository.getTasksFromDatabase()
            val taskListFromServer = repository.getTasks()

            val serverIsEmpty = taskListFromServer.first.isEmpty()
            val serverIsAviliable = taskListFromServer.second
            val dbIsEmpty = taskListFromDb.isNullOrEmpty()

            val state = Triple(serverIsEmpty, serverIsAviliable, dbIsEmpty)

            when (state) {

                Triple(true, false, false) -> {//serv - emty | avil - not | db - not empty
                    actualizer.updateTasksOnServer(
                        updList = emptyList(),
                        delList = actualizer.filterTasksToDeleteWorkManager(taskListFromDb!!)
                    )
                    //отправляем только на удаление, мы не знаем ничего об актуальности тасков на серве
                }

                Triple(true, true, false) -> {//serv - emty | avil - yes | db - not empty
                    actualizer.updateTasksOnServer(
                        updList = actualizer.filterTasksToSetOnServerWorkManager(taskListFromDb!!),
                        delList = emptyList()
                    )
                    //Отправить все, состояние которых не удаленные
                }

                Triple(false, true, true) -> {//serv - not emty | avil - yes | db - empty
                    repository.setTasksToDatabase(taskListFromServer.first)
                    //просто вставляем таски в бз
                }

                Triple(false, true, false) -> {//serv - not emty | avil - yes | db - not empty
                    val list = actualizer.comparatorTasksFromServerAndFromDatabase(
                        taskListFromDb!!,
                        taskListFromServer.first
                    )
                    actualizer.updateTasksInDatabase(list[1])
                    actualizer.updateTasksOnServer(updList = list[2], delList = list[3])
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

}