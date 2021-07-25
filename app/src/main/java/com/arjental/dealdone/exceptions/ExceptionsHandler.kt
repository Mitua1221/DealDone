package com.arjental.dealdone.exceptions

import android.util.Log
import com.arjental.dealdone.exceptions.interfaces.ExceptionsHandlerInterface
import com.arjental.dealdone.models.ItemState
import com.arjental.dealdone.models.TaskItem
import javax.inject.Inject

private const val TAG = "ExceptionsHandler"

class ExceptionsHandler @Inject constructor(): ExceptionsHandlerInterface {

    override fun illegalStateOfTask(tag: String, changeElement: TaskItem, state: ItemState) {
        Log.e(TAG, "in $tag throws IllegalArgumentException of element $changeElement with invalid state $state")
    }

    override fun unsuccessfulResponse(tag: String,  code: String, method: String) {
        Log.w(TAG, "in $tag is unsuccessful response with code $code in method $method")
    }

    override fun exceptionInResponse(tag: String,  exception: Exception, method: String) {
        Log.e(TAG, "in $tag throws Exception: $exception in method $method")
    }

}