package com.arjental.dealdone.recycler.viewholders

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.arjental.dealdone.R

class NewTaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val newTextView: TextView = itemView.findViewById(R.id.new_task_description)
    val newImageButton: ImageButton = itemView.findViewById(R.id.new_task_button)
}