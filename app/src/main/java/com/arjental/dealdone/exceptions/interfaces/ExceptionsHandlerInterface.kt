package com.arjental.dealdone.exceptions.interfaces

import com.arjental.dealdone.models.ItemState
import com.arjental.dealdone.models.TaskItem

interface ExceptionsHandlerInterface{
    fun illegalStateOfTask(tag: String, changeElement: TaskItem, state: ItemState)
    fun unsuccessfulResponse(tag: String, code: String, method: String)
    fun exceptionInResponse(tag: String,  exception: Exception, method: String)
}