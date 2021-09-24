package com.arjental.dealdone.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arjental.dealdone.Translator
import com.arjental.dealdone.models.ItemState
import com.arjental.dealdone.models.TaskItem
import com.arjental.dealdone.models.TaskItemPriorities
import com.arjental.dealdone.repository.Actualizer
import kotlinx.coroutines.CoroutineScope
import java.text.DateFormat
import java.util.*
import javax.inject.Inject

class EditTaskFragmentViewModel @Inject constructor(
    private val translator: Translator,
    private val actualizer: Actualizer,
) : ViewModel() {

    val newTask: MutableLiveData<TaskItem> = MutableLiveData(null)

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

    fun saveTask(title: String) {
        newTask.value?.text = title
        newTask.value?.updateDate = time()
        translator.editedTask.value = newTask.value?.copy()
        updateOrAddTask()
        newTask.value = null
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

    fun setNonePriority() {
        newTask.value?.priority = TaskItemPriorities.NONE
    }

    fun returnLocalizedDate(): String {
        val dateTimeFormat: DateFormat =
            DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())
        return dateTimeFormat.format(Date(newTask.value?.deadline!!))
    }

}