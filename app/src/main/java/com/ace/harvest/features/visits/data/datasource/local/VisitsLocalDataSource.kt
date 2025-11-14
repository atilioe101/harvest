package com.ace.harvest.features.visits.data.datasource.local

import com.ace.harvest.features.visits.data.model.AreaEntity

interface VisitsLocalDataSource {
    suspend fun getAreas(): List<AreaEntity>
    suspend fun saveAreas(areas: List<AreaEntity>)
    suspend fun clearAreas()
    suspend fun isCacheValid(expirationThresholdMillis: Long): Boolean
}
