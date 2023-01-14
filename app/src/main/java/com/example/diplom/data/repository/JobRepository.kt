package com.example.diplom.data.repository

import com.example.diplom.data.api.JobService
import com.example.diplom.data.mapper.JobMapper
import com.example.diplom.data.network.BaseRemoteRepository
import com.example.diplom.data.network.DataResult
import com.example.diplom.data.request.JobCreateRequest
import com.example.diplom.domain.entity.Job
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class JobRepository @Inject constructor(
    private val jobService: JobService
) : BaseRemoteRepository {

    private val cache: MutableList<Job> = mutableListOf()

    suspend fun getJobs(): DataResult<List<Job>> {
        return withContext(Dispatchers.Default) {
            val result = getResult(
                request = { jobService.getJobs() },
                mapTo = JobMapper::mapListDataToDomain
            )
            result.data?.let {
                cache.clear()
                cache.addAll(it)
            }
            result
        }
    }

    suspend fun createJob(
        name: String,
        position: String,
        start: String,
        finish: String?,
        link: String?,
    ): DataResult<Job> {
        val body = JobCreateRequest(
            id = 0,
            name = name,
            position = position,
            start = start,
            finish = finish,
            link = link,
        )
        return withContext(Dispatchers.Default) {
            getResult(
                request = { jobService.createJob(body) },
                mapTo = JobMapper::mapDataToDomain
            )
        }
    }

    suspend fun editJob(
        id: Int,
        name: String,
        position: String,
        start: String,
        finish: String?,
        link: String?,
    ): DataResult<Job> {
        val body = JobCreateRequest(
            id = id,
            name = name,
            position = position,
            start = start,
            finish = finish,
            link = link,
        )
        return withContext(Dispatchers.Default) {
            getResult(
                request = { jobService.createJob(body) },
                mapTo = JobMapper::mapDataToDomain
            )
        }
    }

    suspend fun removeJob(id: String): DataResult<Unit> {
        return withContext(Dispatchers.Default) {
            getResult(
                request = { jobService.removeJob(id) },
                mapTo = {}
            )
        }
    }
}