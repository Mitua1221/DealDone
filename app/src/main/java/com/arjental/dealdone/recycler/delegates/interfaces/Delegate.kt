package com.arjental.dealdone.recycler.delegates.interfaces

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arjental.dealdone.models.TaskItem

interface Delegate {
    fun forItem(listItem: TaskItem): Boolean
    fun getViewHolder(parent: ViewGroup): RecyclerView.ViewHolder
    fun bindViewHolder(viewHolder: RecyclerView.ViewHolder, item: TaskItem)
}