package com.arjental.dealdone.delegates

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.arjental.dealdone.R
import com.arjental.dealdone.models.ItemState
import com.arjental.dealdone.models.TaskItem
import com.arjental.dealdone.userinterface.TasksFragment
import com.arjental.dealdone.viewholders.NewTaskViewHolder

class NewItemDelegate(context: Context) : Delegate {

    override fun forItem(listItem: TaskItem): Boolean = listItem.state == ItemState.ADD

    override fun getViewHolder(parent: ViewGroup): RecyclerView.ViewHolder = NewTaskViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.new_tasks_recycler_layout,
            parent,
            false
        )
    )

    override fun bindViewHolder(viewHolder: RecyclerView.ViewHolder, item: TaskItem) {
        (viewHolder as NewTaskViewHolder).let { taskViewHolder ->
            taskViewHolder.newImageButton.setOnClickListener {
                it.findNavController().navigate(R.id.action_dealsFragment_to_newTaskFragment)
            }
        }
    }
}