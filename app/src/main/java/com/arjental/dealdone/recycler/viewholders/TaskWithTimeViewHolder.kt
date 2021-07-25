package com.arjental.dealdone.recycler.viewholders

import android.view.View
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.arjental.dealdone.R

class TaskWithTimeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val taskTextView: TextView = itemView.findViewById(R.id.task_description)
    val editImageButton: ImageButton = itemView.findViewById(R.id.task_edit_button)
    val dateTextView: TextView = itemView.findViewById(R.id.task_date)
    val checkBox: CheckBox = itemView.findViewById(R.id.task_checkbox_item_with_date)
    val priorIcon: ImageView = itemView.findViewById(R.id.image_priority_with_time)
}