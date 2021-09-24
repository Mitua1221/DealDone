package com.arjental.dealdone.recycler.delegates

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.arjental.dealdone.R
import com.arjental.dealdone.recycler.delegates.interfaces.Delegate
import com.arjental.dealdone.models.ItemState
import com.arjental.dealdone.models.TaskItem
import com.arjental.dealdone.models.TaskItemPriorities
import com.arjental.dealdone.recycler.viewholders.TaskWithTimeViewHolder
import com.arjental.dealdone.viewmodels.TasksListFragmentViewModel
import java.text.DateFormat
import java.util.*

class TaskItemDelegateWithTime(context: Context, viewModels: ViewModel) : Delegate {

    private val viewModel = viewModels

    private val colorDone = ResourcesCompat.getColor(context.resources, R.color.label_light_tertiary, context.theme)
    private val colorBlack = ResourcesCompat.getColor(context.resources, R.color.label_light_primary, context.theme)

    override fun forItem(listItem: TaskItem): Boolean =
        (listItem.state == ItemState.EXIST || listItem.state == ItemState.UNCHANGED) && listItem.deadline != null

    override fun getViewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
        TaskWithTimeViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.tasks_recycler_layout_with_date,
                parent,
                false
            )
        )

    override fun bindViewHolder(viewHolder: RecyclerView.ViewHolder, item: TaskItem) {
        (viewHolder as TaskWithTimeViewHolder).let { taskWithTimeViewHolder ->
            taskWithTimeViewHolder.taskTextView.text = item.text
            val dateTimeFormat: DateFormat =
                DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())
            taskWithTimeViewHolder.dateTextView.text = dateTimeFormat.format(Date(item.deadline!!))
            taskWithTimeViewHolder.checkBox.isChecked = item.isSolved
            taskWithTimeViewHolder.checkBoxImportance.isChecked = item.isSolved
            if (item.isSolved) {
                taskWithTimeViewHolder.taskTextView.strike = true
                taskWithTimeViewHolder.dateTextView.strike = true
                taskWithTimeViewHolder.taskTextView.setTextColor(colorDone)
            } else {
                taskWithTimeViewHolder.taskTextView.strike = false
                taskWithTimeViewHolder.dateTextView.strike = false
                taskWithTimeViewHolder.taskTextView.setTextColor(colorBlack)
            }
            taskWithTimeViewHolder.editImageButton.setOnClickListener {
                if (viewModel is TasksListFragmentViewModel) viewModel.getTaskFromTranslator(item)
                it.findNavController().navigate(R.id.action_dealsFragment_to_newTaskFragment)
            }
            when (item.priority) {
                TaskItemPriorities.HIGH -> {
                    taskWithTimeViewHolder.checkBoxImportance.visibility = View.VISIBLE
                    taskWithTimeViewHolder.checkBox.visibility = View.GONE
                    taskWithTimeViewHolder.checkBoxBackground.visibility = View.VISIBLE
                    taskWithTimeViewHolder.priorIcon.setImageResource(R.drawable.ic_top_priority)
                    taskWithTimeViewHolder.priorIcon.visibility = View.VISIBLE
                }
                TaskItemPriorities.MEDIUM -> {
                    taskWithTimeViewHolder.checkBoxImportance.visibility = View.GONE
                    taskWithTimeViewHolder.checkBox.visibility = View.VISIBLE
                    taskWithTimeViewHolder.checkBoxBackground.visibility = View.GONE
                    taskWithTimeViewHolder.priorIcon.visibility = View.GONE
                }
                TaskItemPriorities.NONE -> {
                    taskWithTimeViewHolder.checkBoxImportance.visibility = View.GONE
                    taskWithTimeViewHolder.checkBox.visibility = View.VISIBLE
                    taskWithTimeViewHolder.checkBoxBackground.visibility = View.GONE
                    taskWithTimeViewHolder.priorIcon.setImageResource(R.drawable.ic_none_priority)
                    taskWithTimeViewHolder.priorIcon.visibility = View.VISIBLE
                }
            }
            if (viewModel is TasksListFragmentViewModel) {
                if (taskWithTimeViewHolder.checkBox.isVisible) {
                    taskWithTimeViewHolder.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                        if (isChecked) {
                            taskWithTimeViewHolder.taskTextView.strike = true
                            taskWithTimeViewHolder.dateTextView.strike = true
                            taskWithTimeViewHolder.taskTextView.setTextColor(colorDone)
                            item.isSolved = true
                            viewModel.changeDone(item, true)
                        } else {
                            taskWithTimeViewHolder.taskTextView.strike = false
                            taskWithTimeViewHolder.dateTextView.strike = false
                            taskWithTimeViewHolder.taskTextView.setTextColor(colorBlack)
                            item.isSolved = false
                            viewModel.changeDone(item, false)
                        }
                        viewModel.qualitySolvedChange.value = true
                    }
                } else {
                    taskWithTimeViewHolder.checkBoxImportance.setOnCheckedChangeListener { buttonView, isChecked ->
                        if (isChecked) {
                            taskWithTimeViewHolder.taskTextView.strike = true
                            taskWithTimeViewHolder.dateTextView.strike = true
                            taskWithTimeViewHolder.taskTextView.setTextColor(colorDone)
                            if (item.priority == TaskItemPriorities.HIGH) taskWithTimeViewHolder.checkBoxBackground.visibility = View.GONE
                            item.isSolved = true
                            viewModel.changeDone(item, true)
                        } else {
                            taskWithTimeViewHolder.taskTextView.strike = false
                            taskWithTimeViewHolder.dateTextView.strike = false
                            taskWithTimeViewHolder.taskTextView.setTextColor(colorBlack)
                            if (item.priority == TaskItemPriorities.HIGH) taskWithTimeViewHolder.checkBoxBackground.visibility = View.VISIBLE
                            item.isSolved = false
                            viewModel.changeDone(item, false)
                        }
                        viewModel.qualitySolvedChange.value = true
                    }
                }

            }
        }
    }
}