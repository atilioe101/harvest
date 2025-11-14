package com.ace.harvest.features.visits.domain.usecase

import com.ace.harvest.features.visits.domain.model.Area
import com.ace.harvest.features.visits.domain.repository.VisitsRepository
import javax.inject.Inject

class GetAreasUseCase @Inject constructor(
    private val repository: VisitsRepository
) : UseCase<Unit, List<Area>> {
    override suspend fun execute(params: Unit): List<Area> = repository.getAreas()
}
