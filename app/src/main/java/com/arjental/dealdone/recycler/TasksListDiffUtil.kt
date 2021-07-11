package com.arjental.dealdone.recycler

import androidx.recyclerview.widget.DiffUtil
import com.arjental.dealdone.models.TaskItem

class TaskListDiffUtil(
    private val newList: List<TaskItem>,
    private val oldList: List<TaskItem>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when {

            oldList[oldItemPosition].state == newList[newItemPosition].state -> {
                true
            }

            else -> {
                false
            }

        }
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {

        return when {
            newList[newItemPosition].text != oldList[oldItemPosition].text -> {
                false
            }
            newList[newItemPosition].isSolved != oldList[oldItemPosition].isSolved -> {
                false
            }
            newList[newItemPosition].id != oldList[oldItemPosition].id -> {
                false
            }
            newList[newItemPosition].priority != oldList[oldItemPosition].priority -> {
                false
            }
            newList[newItemPosition].deadline != oldList[oldItemPosition].deadline -> {
                false
            }
            else -> true
        }
    }
}