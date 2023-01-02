package com.example.diplom.data.mapper

import com.example.diplom.data.model.JobData
import com.example.diplom.domain.entity.Job

object JobMapper {
    fun mapListDataToDomain(data: List<JobData>?): List<Job> {
        return data?.map { mapDataToDomain(it) } ?: emptyList()
    }
    fun mapDataToDomain(data: JobData?): Job {
        if (data == null) throw IllegalArgumentException("Job can not be null")
        return Job(
            id = data.id,
            name = data.name,
            position = data.position,
            start = data.start,
            finish = data.finish,
            link = data.link,
        )
    }
}