package com.ace.harvest.features.visits.domain.model

data class Area(
    val id: String,
    val name: String,
    val description: String?,
    val latitude: Double?,
    val longitude: Double?
)
