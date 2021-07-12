package com.arjental.dealdone.workmanager

import android.app.PendingIntent
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.arjental.dealdone.MainActivity
import com.arjental.dealdone.NOTIFICATION_CHANNEL_ID
import com.arjental.dealdone.R
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

            var s = ""

            when (state) {

                Triple(true, false, false) -> {//serv - emty | avil - not | db - not empty
                    actualizer.updateServerTasks(updList = emptyList(), delList = deleteOnServer(taskListFromDb!!))
                    s = "1"
                    //отправляем только на удаление, мы не знаем ничего об актуальности тасков на серве
                }

                Triple(true, true, false) -> {//serv - emty | avil - yes | db - not empty
                    actualizer.updateServerTasks(updList = setOnServer(taskListFromDb!!), delList = emptyList())
                    s = "2"
                    //Отправить все, состояние которых не удаленные
                }

                Triple(false, true, true) -> {//serv - not emty | avil - yes | db - empty
                    repository.setTasksToDatabase(taskListFromServer.first)
                    s = "3"
                    //просто вставляем таски в бз
                }

                Triple(false, true, false) -> {//serv - not emty | avil - yes | db - not empty
                    val list = actualizer.comparatorDbListAndApiList(taskListFromDb!!, taskListFromServer.first)
                    actualizer.updateDatabaseTasks(list[1])
                    actualizer.updateServerTasks(updList = list[2], delList = list[3])
                    s = "4"
                    //обновляем все
                }

                else -> {
                    //serv - emty | avil - not | db - empty
                    //serv - emty | avil - yes | db - empty
                }

            }


            val intent = MainActivity.newIntent(context)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

            val resources = context.resources
            val notification = NotificationCompat
                .Builder(context, NOTIFICATION_CHANNEL_ID)
                .setTicker(resources.getString(R.string.tasks_to_done_title))
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle(resources.getString(R.string.tasks_to_done_title))
                .setContentText(
                    resources.getString(
                        R.string.new_tasks_text,
                        "YEP + $s"
                    )
                )
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(0, notification)


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