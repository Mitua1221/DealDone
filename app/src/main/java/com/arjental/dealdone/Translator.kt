package com.arjental.dealdone

import androidx.lifecycle.MutableLiveData
import com.arjental.dealdone.models.TaskItem
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

object Translator {

    var needToUpdate = AtomicBoolean(true)

    var taskList: MutableLiveData<List<TaskItem>> = MutableLiveData(null)

    var editedTask: MutableLiveData<TaskItem> = MutableLiveData(null)

    val timeSelectedFromCalendar: MutableLiveData<Date?> = MutableLiveData(null)

}