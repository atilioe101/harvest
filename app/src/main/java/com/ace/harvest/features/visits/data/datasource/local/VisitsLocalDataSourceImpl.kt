package com.ace.harvest.features.visits.data.datasource.local

import com.ace.harvest.features.visits.data.local.AreaDao
import com.ace.harvest.features.visits.data.model.AreaEntity
import javax.inject.Inject

class VisitsLocalDataSourceImpl @Inject constructor(
    private val areaDao: AreaDao
) : VisitsLocalDataSource {
    override suspend fun getAreas(): List<AreaEntity> = areaDao.getAreas()

    override suspend fun saveAreas(areas: List<AreaEntity>) {
        areaDao.insertAreas(areas)
    }

    override suspend fun clearAreas() {
        areaDao.clearAreas()
    }

    override suspend fun isCacheValid(expirationThresholdMillis: Long): Boolean {
        val oldestCacheTimestamp = areaDao.oldestCacheTimestamp() ?: return false
        val currentTime = System.currentTimeMillis()
        return (currentTime - oldestCacheTimestamp) < expirationThresholdMillis
    }
}
