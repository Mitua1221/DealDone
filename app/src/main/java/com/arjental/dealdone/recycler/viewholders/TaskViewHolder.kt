package com.arjental.dealdone.recycler.viewholders

import android.view.View
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.arjental.dealdone.R

class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val taskTextView: TextView = itemView.findViewById(R.id.task_description)
    val editImageButton: ImageButton = itemView.findViewById(R.id.task_edit_button)
    val checkBox: CheckBox = itemView.findViewById(R.id.task_checkbox)
    val checkBoxImportance: CheckBox = itemView.findViewById(R.id.task_checkbox_importance)
    val priorIcon: ImageView = itemView.findViewById(R.id.image_priority)
    val checkBoxBackground: FrameLayout = itemView.findViewById(R.id.checkbox_importance_background_no_time)
}