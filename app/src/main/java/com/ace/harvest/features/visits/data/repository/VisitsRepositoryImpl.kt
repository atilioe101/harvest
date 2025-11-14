package com.ace.harvest.features.visits.data.repository

import com.ace.harvest.features.visits.data.datasource.local.VisitsLocalDataSource
import com.ace.harvest.features.visits.data.datasource.remote.VisitsRemoteDataSource
import com.ace.harvest.features.visits.data.model.AreaDto
import com.ace.harvest.features.visits.data.model.AreaEntity
import com.ace.harvest.features.visits.domain.model.Area
import com.ace.harvest.features.visits.domain.repository.VisitsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class VisitsRepositoryImpl @Inject constructor(
    private val remoteDataSource: VisitsRemoteDataSource,
    private val localDataSource: VisitsLocalDataSource
) : VisitsRepository {

    override suspend fun getAreas(): List<Area> = withContext(Dispatchers.IO) {
        val isCacheValid = localDataSource.isCacheValid(CACHE_DURATION_MILLIS)
        if (isCacheValid) {
            return@withContext localDataSource.getAreas().map { it.toDomain() }
        }

        val remoteAreas = remoteDataSource.getAreas()
        val cacheTimestamp = System.currentTimeMillis()
        val entities = remoteAreas.map { it.toEntity(cacheTimestamp) }
        localDataSource.clearAreas()
        localDataSource.saveAreas(entities)
        entities.map { it.toDomain() }
    }

    private fun AreaEntity.toDomain(): Area = Area(
        id = id,
        name = name,
        description = description,
        latitude = latitude,
        longitude = longitude
    )

    private fun AreaDto.toEntity(cacheTimestamp: Long): AreaEntity = AreaEntity(
        id = id,
        name = name,
        description = description,
        latitude = latitude,
        longitude = longitude,
        cachedAt = cacheTimestamp
    )

    private companion object {
        const val CACHE_DURATION_MILLIS: Long = 24L * 60L * 60L * 1000L
    }
}
