package com.shahidshaadi.matchmate.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.shahidshaadi.matchmate.model.MatchProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(matches: List<MatchProfile>)

    @Update
    suspend fun updateStatus(match: MatchProfile)

    @Query("SELECT * FROM matches ORDER BY page ASC, name ASC")
    fun getAll(): Flow<List<MatchProfile>>

    @Query("SELECT * FROM matches WHERE id = :id LIMIT 1")
    suspend fun getMatchById(id: String): MatchProfile?

    @Query("SELECT MAX(page) FROM matches")
    suspend fun getMaxPage(): Int?

}