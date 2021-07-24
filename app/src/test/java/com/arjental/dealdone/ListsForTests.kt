package com.arjental.dealdone

import com.arjental.dealdone.models.ItemState
import com.arjental.dealdone.models.TaskItem
import com.arjental.dealdone.models.TaskItemPriorities
import com.arjental.dealdone.models.newtworkmodels.ItemFromApi
import java.util.*

class ListsForTests {

    val elementsFromApi = listOf<ItemFromApi>(
        ItemFromApi(
            id = UUID.randomUUID().toString(),
            text = "Test Element 1",
            importance = "low",
            done = true,
            deadline = 1624850000,
            created_at = 1624830000,
            updated_at = 1624830522,
        ),
        ItemFromApi(
            id = UUID.randomUUID().toString(),
            text = "",
            importance = "basic",
            done = false,
            deadline = 0,
            created_at = 0,
            updated_at = 0,
        ),
        ItemFromApi(
            id = UUID.randomUUID().toString(),
            text = "3",
            importance = "important",
            done = true,
            deadline = 0,
            created_at = 0,
            updated_at = 0,
        )
    )

    val expectedFromConverter = listOf<TaskItem>(
        TaskItem(
            id = UUID.fromString(elementsFromApi[0].id),
            isSolved = true,
            text = "Test Element 1",
            priority = TaskItemPriorities.NONE,
            deadline = 1624850000000L,
            state = ItemState.EXIST,
            createDate = 1624830000000L,
            updateDate = 1624830522000L,
        ),
        TaskItem(
            id = UUID.fromString(elementsFromApi[1].id),
            isSolved = false,
            text = "",
            priority = TaskItemPriorities.MEDIUM,
            deadline = null,
            state = ItemState.EXIST,
            createDate = 0,
            updateDate = 0,
        ),
        TaskItem(
            id = UUID.fromString(elementsFromApi[2].id),
            isSolved = true,
            text = "3",
            priority = TaskItemPriorities.HIGH,
            deadline = null,
            state = ItemState.EXIST,
            createDate = 0,
            updateDate = 0,
        ),
    )

    val uncorrectElementsFromApi = listOf<ItemFromApi>(
        ItemFromApi(
            id = UUID.randomUUID().toString(),
            text = "Test Element 1",
            importance = "fail",
            done = true,
            deadline = 1624850000,
            created_at = 1624830000,
            updated_at = 1624830522,
        ),
        ItemFromApi(
            id = UUID.randomUUID().toString(),
            text = "",
            importance = "fail",
            done = false,
            deadline = 0,
            created_at = 0,
            updated_at = 0,
        ),
        ItemFromApi(
            id = UUID.randomUUID().toString(),
            text = "3",
            importance = "fail",
            done = true,
            deadline = 0,
            created_at = 0,
            updated_at = 0,
        )
    )

    val actualizerListOfItems = listOf(
        TaskItem(
            id = UUID.randomUUID(),
            isSolved = true,
            text = "Test Element 1",
            priority = TaskItemPriorities.NONE,
            deadline = 1624850000000L,
            state = ItemState.EXIST,
            createDate = 1624830000000L,
            updateDate = 1624830522000L,
        ),
        TaskItem(
            id = UUID.randomUUID(),
            isSolved = false,
            text = "",
            priority = TaskItemPriorities.MEDIUM,
            deadline = null,
            state = ItemState.EXIST,
            createDate = 0,
            updateDate = 0,
        ),
        TaskItem(
            id = UUID.randomUUID(),
            isSolved = true,
            text = "3",
            priority = TaskItemPriorities.HIGH,
            deadline = null,
            state = ItemState.DELETED,
            createDate = 0,
            updateDate = 0,
        ),
    )

    val actualizerListOfItemsFromServ = listOf(
        TaskItem(
            id = UUID.randomUUID(),
            isSolved = true,
            text = "Test Element 1",
            priority = TaskItemPriorities.NONE,
            deadline = 1624850000000L,
            state = ItemState.EXIST,
            createDate = 1624830000000L,
            updateDate = 1624830522000L,
        ),
        TaskItem(
            id = UUID.randomUUID(),
            isSolved = false,
            text = "",
            priority = TaskItemPriorities.MEDIUM,
            deadline = null,
            state = ItemState.EXIST,
            createDate = 0,
            updateDate = 0,
        ),
        TaskItem(
            id = UUID.randomUUID(),
            isSolved = true,
            text = "3",
            priority = TaskItemPriorities.HIGH,
            deadline = null,
            state = ItemState.EXIST,
            createDate = 0,
            updateDate = 0,
        ),
    )

    val actualizerPasteList = listOf(
        TaskItem(
            id = actualizerListOfItems[0].id,
            isSolved = true,
            text = "Test Element 1",
            priority = TaskItemPriorities.NONE,
            deadline = 1624850000000L,
            state = ItemState.EXIST,
            createDate = 1624830000000L,
            updateDate = 1624830522000L,
        ),
        TaskItem(
            id = actualizerListOfItems[1].id,
            isSolved = false,
            text = "",
            priority = TaskItemPriorities.MEDIUM,
            deadline = null,
            state = ItemState.EXIST,
            createDate = 0,
            updateDate = 0,
        ),
    )

    //deleteTask actualizer method

    val taskToDelete = TaskItem(
            id = UUID.randomUUID(),
            isSolved = true,
            text = "Test Element to delete",
            priority = TaskItemPriorities.NONE,
            deadline = 1624850000000L,
            state = ItemState.EXIST,
            createDate = 1624830000000L,
            updateDate = 1624830522000L,
        )

    val deletedTask = TaskItem(
        id = taskToDelete.id,
        isSolved = true,
        text = "Test Element to delete",
        priority = TaskItemPriorities.NONE,
        deadline = 1624850000000L,
        state = ItemState.DELETED,
        createDate = 1624830000000L,
        updateDate = 1624830522000L,
    )


}