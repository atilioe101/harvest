package com.ace.harvest.features.visits.di

import android.content.Context
import androidx.room.Room
import com.ace.harvest.features.visits.data.datasource.local.VisitsLocalDataSource
import com.ace.harvest.features.visits.data.datasource.local.VisitsLocalDataSourceImpl
import com.ace.harvest.features.visits.data.datasource.remote.VisitsRemoteDataSource
import com.ace.harvest.features.visits.data.datasource.remote.VisitsRemoteDataSourceImpl
import com.ace.harvest.features.visits.data.local.AreaDao
import com.ace.harvest.features.visits.data.local.VisitsDatabase
import com.ace.harvest.features.visits.data.repository.VisitsRepositoryImpl
import com.ace.harvest.features.visits.domain.repository.VisitsRepository
import com.ace.harvest.features.visits.domain.usecase.GetAreasUseCase
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object VisitsModule {

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideVisitsDatabase(
        @ApplicationContext context: Context
    ): VisitsDatabase = Room.databaseBuilder(
        context,
        VisitsDatabase::class.java,
        "visits.db"
    ).fallbackToDestructiveMigration().build()

    @Provides
    fun provideAreaDao(database: VisitsDatabase): AreaDao = database.areaDao()

    @Provides
    @Singleton
    fun provideVisitsLocalDataSource(areaDao: AreaDao): VisitsLocalDataSource =
        VisitsLocalDataSourceImpl(areaDao)

    @Provides
    @Singleton
    fun provideVisitsRemoteDataSource(firestore: FirebaseFirestore): VisitsRemoteDataSource =
        VisitsRemoteDataSourceImpl(firestore)

    @Provides
    @Singleton
    fun provideVisitsRepository(
        remoteDataSource: VisitsRemoteDataSource,
        localDataSource: VisitsLocalDataSource
    ): VisitsRepository = VisitsRepositoryImpl(remoteDataSource, localDataSource)

    @Provides
    @Singleton
    fun provideGetAreasUseCase(repository: VisitsRepository): GetAreasUseCase =
        GetAreasUseCase(repository)
}
