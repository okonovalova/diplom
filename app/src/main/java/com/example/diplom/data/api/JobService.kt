package com.example.diplom.data.api

import com.example.diplom.data.model.JobData
import com.example.diplom.data.request.JobCreateRequest
import retrofit2.Response
import retrofit2.http.*

interface JobService {

    @GET("api/my/jobs")
    suspend fun getJobs(): Response<List<JobData>>

    @POST("api/my/jobs")
    suspend fun createJob(@Body jobCreateRequest: JobCreateRequest): Response<JobData>

    @DELETE("api/my/jobs/{job_id}")
    suspend fun removeJob(@Path("job_id") job_id: String): Response<Unit>
}