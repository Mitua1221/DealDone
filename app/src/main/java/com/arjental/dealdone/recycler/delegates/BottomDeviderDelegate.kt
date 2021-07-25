package com.arjental.dealdone.recycler.delegates

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arjental.dealdone.R
import com.arjental.dealdone.recycler.delegates.interfaces.Delegate
import com.arjental.dealdone.models.ItemState
import com.arjental.dealdone.models.TaskItem
import com.arjental.dealdone.recycler.viewholders.BottomDeviederViewHolder

class BottomDeviderDelegate(context: Context) : Delegate {

    override fun forItem(listItem: TaskItem): Boolean = listItem.state == ItemState.BOTTOMDEVIDER

    override fun getViewHolder(parent: ViewGroup): RecyclerView.ViewHolder = BottomDeviederViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.bottom_devider_recycler,
            parent,
            false
        )
    )

    override fun bindViewHolder(viewHolder: RecyclerView.ViewHolder, item: TaskItem) {
    }
}