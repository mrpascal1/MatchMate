package com.shahidshaadi.matchmate.di

import android.content.Context
import androidx.room.Room
import com.shahidshaadi.matchmate.data.local.MatchDao
import com.shahidshaadi.matchmate.data.local.MatchDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MatchDatabase {
        return Room.databaseBuilder(
            context,
            MatchDatabase::class.java,
            "matchmate_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideMatchDao(database: MatchDatabase): MatchDao = database.matchDao()
}