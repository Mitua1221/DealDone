package com.arjental.dealdone.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arjental.dealdone.Translator
import com.arjental.dealdone.exceptions.ExceptionsHandler
import com.arjental.dealdone.models.ItemState
import com.arjental.dealdone.models.TaskItem
import com.arjental.dealdone.models.TaskItemPriorities
import com.arjental.dealdone.repository.Actualizer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

private const val TAG = "TasksFragmentViewModel"

class TasksListFragmentViewModel @Inject constructor(
    private val translator: Translator,
    private val actualizer: Actualizer,
    private val exceptionHandler: ExceptionsHandler,
) : ViewModel() {

    var isHidden = true
    val pasteList: MutableLiveData<List<TaskItem>> = MutableLiveData()
    val tasksList: MutableLiveData<List<TaskItem>> = MutableLiveData()
    var recyclerList: List<TaskItem> = emptyList()
    val qualitySolvedChange: MutableLiveData<Boolean> = MutableLiveData(true)
    var tasksListModelScope = viewModelScope

    init {
        tasksListModelScope.launch {
            translator.actualTaskList.collect {
                launch(Dispatchers.Main) { tasksList.value = it }
            }
        }
    }

    fun updateOrAddTask(taskItem: TaskItem) {
        this.actualizer.updateOrAddTask(taskItem)
    }

    fun deleteTask(task: TaskItem) {
        actualizer.deleteTask(task)
    }

    fun getTaskFromTranslator(item: TaskItem) {
        translator.editedTask.value = item.copy()
    }

    fun setSortedListToPaste(isHid: Boolean) {
        val readyList = mutableListOf<TaskItem>()
        readyList.apply {
            add(
                TaskItem(
                    id = UUID.randomUUID(),
                    isSolved = false,
                    text = "TOPDEVIDER",
                    priority = TaskItemPriorities.NONE,
                    state = ItemState.TOPDEVIDER,
                    createDate = 0L,
                    updateDate = 0L,
                )
            )
            if (!tasksList.value.isNullOrEmpty()) {
                if (isHid) {
                    addAll(sortingTask(tasksList.value?.toMutableList()!!.filterNot { it.isSolved }
                        .toMutableList()))
                } else {
                    addAll(sortingTask(tasksList.value!!.toMutableList()))
                }
            }
            add(
                TaskItem(
                    id = UUID.randomUUID(),
                    isSolved = false,
                    text = "TOPDEVIDER",
                    priority = TaskItemPriorities.NONE,
                    state = ItemState.ADD,
                    createDate = 0L,
                    updateDate = 0L,
                )
            )
            add(
                TaskItem(
                    id = UUID.randomUUID(),
                    isSolved = false,
                    text = "TOPDEVIDER",
                    priority = TaskItemPriorities.NONE,
                    state = ItemState.BOTTOMDEVIDER,
                    createDate = 0L,
                    updateDate = 0L,
                )
            )
        }
        pasteList.value = readyList
    }

    private fun sortingTask(receivedList: MutableList<TaskItem>): MutableList<TaskItem> {
        var list = receivedList
        if (list.isNotEmpty()) {
            list.removeAll { it.state != ItemState.EXIST }
            val lists = mutableListOf<MutableList<TaskItem>>(
                mutableListOf(),
                mutableListOf(),
                mutableListOf()
            )
            list.forEach {
                when (it.priority) {
                    TaskItemPriorities.NONE -> {
                        lists[2].add(it)
                    }
                    TaskItemPriorities.MEDIUM -> {
                        lists[1].add(it)
                    }
                    TaskItemPriorities.HIGH -> {
                        lists[0].add(it)
                    }
                }
            }
            list = mutableListOf()
            lists.forEach {
                val ret = mutableListOf<TaskItem>()
                it.forEach { its ->
                    if (its.deadline != null) ret.add(its)
                }
                list.addAll(ret.sortedWith(compareBy { it.deadline }))
                list.addAll(it.filter { it.deadline == null })
            }
        }
        return list
    }

    fun changeElement(taskItem: TaskItem) {
        taskItem.state = ItemState.EXIST
        val list =
            if (!tasksList.value.isNullOrEmpty()) tasksList.value!!.toMutableList() else mutableListOf()
        val elementIndex = list.indexOfFirst { it.id == taskItem.id }
        if (elementIndex != -1) list[elementIndex] = taskItem else list.add(taskItem)
        tasksList.value = list
    }

    fun addElement(taskItem: TaskItem) {
        taskItem.state = ItemState.EXIST
        val list =
            if (!tasksList.value.isNullOrEmpty()) tasksList.value!!.toMutableList() else mutableListOf()
        list.add(taskItem)
        tasksList.value = list
    }

    fun deleteElement(taskItem: TaskItem) {
        val list =
            if (!tasksList.value.isNullOrEmpty()) tasksList.value!!.toMutableList() else mutableListOf()

        if (list.isNotEmpty()) {
            val firstIndex = list.indexOfFirst { it.id == taskItem.id }
            if (firstIndex != -1) list.removeAt(firstIndex)
        }

        tasksList.value = list
    }

    fun changeDone(taskItem: TaskItem, done: Boolean) {
        val list =
            if (!tasksList.value.isNullOrEmpty()) tasksList.value!!.toMutableList() else mutableListOf()
        if (!list.isNullOrEmpty()) {
            val firstIndex = tasksList.value!!.indexOfFirst { it.id == taskItem.id }
            if (firstIndex != -1) {
                tasksList.value!![firstIndex].isSolved = done
                tasksList.value!![firstIndex].updateDate = time()
                updateOrAddTask(tasksList.value!![firstIndex])
            }
        }
    }

    fun time() = Calendar.getInstance().time.time

    fun unsolvedQuality(): Int {
        if (!tasksList.value.isNullOrEmpty()) {
            return tasksList.value!!.count { it.isSolved }
        } else {
            return 0
        }
    }

    fun updateTasks() {
        actualizer.actualize()
    }

    fun showDoneElements() {
        setSortedListToPaste(false)
        isHidden = false
    }

    fun hideDoneElements() {
        setSortedListToPaste(true)
        isHidden = true
    }

    fun getChangedTaskFromTranslator() {
        if (translator.editedTask.value != null) {
            when (translator.editedTask.value!!.state) {
                ItemState.DELETED -> {
                    deleteElement(translator.editedTask.value!!.copy())
                    translator.editedTask.value = null
                }
                ItemState.NEW -> {
                    addElement(translator.editedTask.value!!.copy())
                    translator.editedTask.value = null
                }
                ItemState.EXIST -> {
                    changeElement(translator.editedTask.value!!.copy())
                    translator.editedTask.value = null
                }
                else -> {
                    val item = translator.editedTask.value!!.copy()
                    translator.editedTask.value = null
                    exceptionHandler.illegalStateOfTask(TAG, item, item.state)
                }
            }
        }
    }

}