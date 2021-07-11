package com.arjental.dealdone.models.newtworkmodels

data class ItemFromApi(
    val id: String,
    var text: String,
    var importance: String,
    var done: Boolean,
    var deadline: Long,
    var created_at: Long,
    var updated_at: Long,
)


