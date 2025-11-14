package com.ace.harvest.features.visits.domain.repository

import com.ace.harvest.features.visits.domain.model.Area

interface VisitsRepository {
    suspend fun getAreas(): List<Area>
}
