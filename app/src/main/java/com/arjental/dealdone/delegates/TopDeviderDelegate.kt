package com.arjental.dealdone.delegates

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.arjental.dealdone.R
import com.arjental.dealdone.models.ItemState
import com.arjental.dealdone.models.TaskItem
import com.arjental.dealdone.viewholders.NewTaskViewHolder
import com.arjental.dealdone.viewholders.TopDeviederViewHolder

class TopDeviderDelegate(context: Context) : Delegate {

    override fun forItem(listItem: TaskItem): Boolean = listItem.state == ItemState.TOPDEVIDER

    override fun getViewHolder(parent: ViewGroup): RecyclerView.ViewHolder = TopDeviederViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.top_devider_recycler,
            parent,
            false
        )
    )

    override fun bindViewHolder(viewHolder: RecyclerView.ViewHolder, item: TaskItem) {
    }
}