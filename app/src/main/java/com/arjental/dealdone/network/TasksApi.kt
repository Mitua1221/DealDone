package com.arjental.dealdone.network

import com.arjental.dealdone.models.TaskItem
import com.arjental.dealdone.models.newtworkmodels.ItemFromApi
import com.arjental.dealdone.models.newtworkmodels.SyncItemsFromApi
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface TasksApi {

    @GET("/tasks/")
    suspend fun getTasks(): Response<List<ItemFromApi>>

//    @GET("/tasks/")
//    suspend fun getTasksDemo(): Call<List<ItemFromApi>>

    @POST("/tasks/")
    suspend fun pushTask(
        @Body task: ItemFromApi
    ): Response<ItemFromApi>

    @PUT("/tasks/{task_id}/")
    suspend fun updateTask(
        @Path(value = "task_id", encoded = true) taskId: String,
        @Body task: ItemFromApi
    ): Response<ItemFromApi>

    @DELETE("/tasks/{task_id}/")
    suspend fun deleteTask(
        @Path(value = "task_id", encoded = true) taskId: String,
    ): Response<ItemFromApi>

    @PUT("/tasks/")
    suspend fun synchronizedTasks(
        @Body post: SyncItemsFromApi
    ): Response<List<ItemFromApi>>

}
