package com.ace.harvest.features.visits.data.datasource.remote

import com.ace.harvest.features.visits.data.model.AreaDto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class VisitsRemoteDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : VisitsRemoteDataSource {

    override suspend fun getAreas(): List<AreaDto> {
        val snapshot = firestore.collection(AREAS_COLLECTION).get().await()
        return snapshot.documents.map { document ->
            AreaDto.fromDocument(document)
        }
    }

    private companion object {
        const val AREAS_COLLECTION = "areas"
    }
}
