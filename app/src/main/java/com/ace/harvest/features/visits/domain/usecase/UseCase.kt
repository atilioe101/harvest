package com.ace.harvest.features.visits.domain.usecase

fun interface UseCase<in Params, out Result> {
    suspend fun execute(params: Params): Result
}
