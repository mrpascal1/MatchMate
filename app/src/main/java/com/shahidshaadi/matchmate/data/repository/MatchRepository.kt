package com.shahidshaadi.matchmate.data.repository

import com.shahidshaadi.matchmate.data.local.MatchDao
import com.shahidshaadi.matchmate.data.remote.ApiService
import com.shahidshaadi.matchmate.model.MatchProfile
import com.shahidshaadi.matchmate.model.toMatchProfile
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MatchRepository @Inject constructor(
    private val apiService: ApiService,
    private val matchDao: MatchDao
) {

    val allMatches: Flow<List<MatchProfile>> = matchDao.getAll()

    private var nextPageToLoad: Int = 1

     suspend fun initializePagination() {
        nextPageToLoad = (matchDao.getMaxPage() ?: 0) + 1
    }

    suspend fun loadInitialPage(): Result<Unit> {
        if (matchDao.getMaxPage() == null) {
            return try {
                val response = apiService.getMatches(count = 10, page = 1)
                val profiles = response.results?.map { it.toMatchProfile(1) }
                profiles?.let {
                    matchDao.insertAll(it)
                    nextPageToLoad = 2
                }
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
        return Result.success(Unit)
    }

    suspend fun loadNextPage(): Result<Unit> {
        return try {
            val response = apiService.getMatches(count = 10, page = nextPageToLoad)
            val profiles = response.results?.map { it.toMatchProfile(page = nextPageToLoad) }
            profiles?.let {
                matchDao.insertAll(it)
                nextPageToLoad++
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateMatchStatus(id: String, status: String) {
        val match = matchDao.getMatchById(id)
        match?.let {
            matchDao.updateStatus(it.copy(status = status))
        }
    }
}