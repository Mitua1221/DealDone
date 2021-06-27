package com.arjental.dealdone.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arjental.dealdone.models.TaskItem

class TasksFagmentViewModel : ViewModel() {

    val tasksList: MutableLiveData<List<TaskItem>> = MutableLiveData(
        listOf(
            TaskItem(isSolved = false, text = "FIrst", priority = 0),
            TaskItem(isSolved = false, text = "FIrst", priority = 0),
            TaskItem(isSolved = false, text = "FIrst", priority = 0),
            TaskItem(isSolved = false, text = "FIrst", priority = 0),
            TaskItem(isSolved = false, text = "FIrst", priority = 0),
            TaskItem(isSolved = false, text = "FIrst", priority = 0),
            TaskItem(isSolved = false, text = "FIrst", priority = 0),
            TaskItem(isSolved = false, text = "FIrst", priority = 0),
            TaskItem(isSolved = false, text = "FIrst", priority = 0),
            TaskItem(isSolved = false, text = "FIrst", priority = 0),
            TaskItem(isSolved = false, text = "FIrst", priority = 0),
            TaskItem(isSolved = false, text = "FIrst", priority = 0),
            TaskItem(isSolved = false, text = "FIrst", priority = 0)
        )
    )

    var recyclerList = tasksList.value

}