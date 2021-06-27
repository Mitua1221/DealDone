package com.arjental.dealdone.models

data class TaskItem(
    var isSolved: Boolean,
    var text: String,
    var priority: Int,
    var deadline: String? = null
)
