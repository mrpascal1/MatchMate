package com.shahidshaadi.matchmate.di

import com.shahidshaadi.matchmate.data.local.MatchDao
import com.shahidshaadi.matchmate.data.remote.ApiService
import com.shahidshaadi.matchmate.data.repository.MatchRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideMatchRepository(
        apiService: ApiService,
        matchDao: MatchDao
    ): MatchRepository = MatchRepository(apiService, matchDao)
}