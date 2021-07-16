package com.arjental.dealdone.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arjental.dealdone.Translator
import com.arjental.dealdone.models.ItemState
import com.arjental.dealdone.models.TaskItem
import com.arjental.dealdone.models.TaskItemPriorities
import com.arjental.dealdone.repository.Actualizer
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class EditTaskViewModel @Inject constructor(val translator: Translator, val actualizer: Actualizer) : ViewModel() {

    val newTask: MutableLiveData<TaskItem> = MutableLiveData(null)

//    @Inject lateinit var translator: Translator
//    @Inject lateinit var actualizer: Actualizer

//    val translator = translator
//    val actualizer = actualizer

    fun createNewTask() {
        newTask.value = TaskItem(
            id = UUID.randomUUID(),
            isSolved = false,
            text = "",
            priority = TaskItemPriorities.NONE,
            deadline = null,
            state = ItemState.NEW,
            createDate = time(),
            updateDate = time(),
        )
    }

    fun time() = Calendar.getInstance().time.time

    fun updateOrAddTask() {
        val task = newTask.value?.copy()
        if (task != null) {
            task.state = ItemState.EXIST
            actualizer.updateOrAddTask(task)
        }
    }

    fun deleteTask() {
        val task = newTask.value?.copy()
        if (task != null) {
            actualizer.deleteTask(task)
        }
    }

    fun collectTaskFromTranslator() {
        newTask.value = translator.editedTask.value?.copy()
        translator.editedTask.value = null
    }

}