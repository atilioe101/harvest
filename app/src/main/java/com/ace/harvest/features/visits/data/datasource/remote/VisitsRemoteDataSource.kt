package com.ace.harvest.features.visits.data.datasource.remote

import com.ace.harvest.features.visits.data.model.AreaDto

interface VisitsRemoteDataSource {
    suspend fun getAreas(): List<AreaDto>
}
