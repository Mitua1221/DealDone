package com.arjental.dealdone.delegates

import android.content.Context
import android.graphics.Paint
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.arjental.dealdone.R
import com.arjental.dealdone.Translator
import com.arjental.dealdone.models.ItemState
import com.arjental.dealdone.models.TaskItem
import com.arjental.dealdone.models.TaskItemPriorities
import com.arjental.dealdone.userinterface.TasksFragment
import com.arjental.dealdone.viewholders.TaskViewHolder
import com.arjental.dealdone.viewmodels.TasksFagmentViewModel

class TaskItemDelegate(context: Context, viewModels: ViewModel) : Delegate {

    val viewModel = viewModels
    val colorDone = ResourcesCompat.getColor(context.resources, R.color.label_light_tertiary, context.theme)
    val colorBlack = ResourcesCompat.getColor(context.resources, R.color.label_light_primary, context.theme)

    override fun forItem(listItem: TaskItem): Boolean =
        (listItem.state == ItemState.EXIST || listItem.state == ItemState.UNCHANGED) && listItem.deadline == null

    override fun getViewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
        TaskViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.tasks_recycler_layout,
                parent,
                false
            )
        )

    override fun bindViewHolder(viewHolder: RecyclerView.ViewHolder, item: TaskItem) {
        (viewHolder as TaskViewHolder).let { taskViewHolder ->
            taskViewHolder.taskTextView.text = item.text
            taskViewHolder.checkBox.isChecked = item.isSolved
            when (item.priority) {
                TaskItemPriorities.HIGH -> {
                    taskViewHolder.priorIcon.setImageResource(R.drawable.ic_top_priority)
                    taskViewHolder.priorIcon.visibility = View.VISIBLE
                }
                TaskItemPriorities.MEDIUM -> {
                    taskViewHolder.priorIcon.visibility = View.GONE
                }
                TaskItemPriorities.NONE -> {
                    taskViewHolder.priorIcon.setImageResource(R.drawable.ic_none_priority)
                    taskViewHolder.priorIcon.visibility = View.VISIBLE
                }
            }
            if (item.isSolved) {
                taskViewHolder.taskTextView.strike = true
            }
            taskViewHolder.editImageButton.setOnClickListener {
                Translator.editedTask.value = item.copy()
                it.findNavController().navigate(R.id.action_dealsFragment_to_newTaskFragment)
            }
            if (item.isSolved) {
                taskViewHolder.taskTextView.strike = true
                taskViewHolder.taskTextView.setTextColor(colorDone)
            } else {
                taskViewHolder.taskTextView.strike = false
                taskViewHolder.taskTextView.setTextColor(colorBlack)
            }
            if (viewModel is TasksFagmentViewModel) {
                taskViewHolder.checkBox.setOnClickListener {
                    if (it is CheckBox) {
                        if (it.isChecked) {
                            taskViewHolder.taskTextView.strike = true
                            taskViewHolder.taskTextView.setTextColor(colorDone)
                            item.isSolved = true
                            viewModel.changeDone(item, true)
                        } else {
                            taskViewHolder.taskTextView.strike = false
                            taskViewHolder.taskTextView.setTextColor(colorBlack)
                            item.isSolved = false
                            viewModel.changeDone(item, false)
                        }
                    }
                    viewModel.qualitySolvedChange.value = true
                }

            }
        }
    }
}

inline var TextView.strike: Boolean
    set(visible) {
        paintFlags = if (visible) paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        else paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
    }
    get() = paintFlags and Paint.STRIKE_THRU_TEXT_FLAG == Paint.STRIKE_THRU_TEXT_FLAG