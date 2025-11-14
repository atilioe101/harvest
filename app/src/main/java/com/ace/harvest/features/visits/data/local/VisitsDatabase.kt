package com.ace.harvest.features.visits.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ace.harvest.features.visits.data.model.AreaEntity

@Database(
    entities = [AreaEntity::class],
    version = 1,
    exportSchema = false
)
abstract class VisitsDatabase : RoomDatabase() {
    abstract fun areaDao(): AreaDao
}
