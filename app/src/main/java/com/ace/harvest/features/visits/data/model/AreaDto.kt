package com.ace.harvest.features.visits.data.model

import com.google.firebase.firestore.DocumentSnapshot

data class AreaDto(
    val id: String,
    val name: String,
    val description: String?,
    val latitude: Double?,
    val longitude: Double?,
    val updatedAt: Long?
) {
    companion object {
        fun fromDocument(document: DocumentSnapshot): AreaDto {
            val name = document.getString("name") ?: ""
            val description = document.getString("description")
            val latitude = document.getDouble("latitude")
            val longitude = document.getDouble("longitude")
            val updatedAt = document.getLong("updatedAt")
            return AreaDto(
                id = document.id,
                name = name,
                description = description,
                latitude = latitude,
                longitude = longitude,
                updatedAt = updatedAt
            )
        }
    }
}
