package com.arjental.dealdone

import androidx.lifecycle.MutableLiveData
import com.arjental.dealdone.models.TaskItem
import com.arjental.dealdone.repository.Actualizer
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Translator @Inject constructor() {

    var needToUpdate = AtomicBoolean(true)

    var taskList: MutableLiveData<List<TaskItem>> = MutableLiveData(null)

    var taskListFlow = MutableSharedFlow<List<TaskItem>>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    var editedTask: MutableLiveData<TaskItem> = MutableLiveData(null)

    val timeSelectedFromCalendar: MutableLiveData<Date?> = MutableLiveData(null)

}