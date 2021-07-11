package com.arjental.dealdone.models.newtworkmodels

data class SyncItemsFromApi(
    val deleted: List<String>,
    val other: List<ItemFromApi>,
)
