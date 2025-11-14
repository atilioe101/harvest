package com.ace.harvest.features.visits.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ace.harvest.features.visits.data.model.AreaEntity

@Dao
interface AreaDao {
    @Query("SELECT * FROM areas")
    suspend fun getAreas(): List<AreaEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAreas(areas: List<AreaEntity>)

    @Query("DELETE FROM areas")
    suspend fun clearAreas()

    @Query("SELECT MIN(cachedAt) FROM areas")
    suspend fun oldestCacheTimestamp(): Long?
}
