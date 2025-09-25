package com.shahidshaadi.matchmate.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.shahidshaadi.matchmate.model.MatchProfile

@Database(entities = [MatchProfile::class], version = 2, exportSchema = false)
abstract class MatchDatabase : RoomDatabase() {
    abstract fun matchDao(): MatchDao
}