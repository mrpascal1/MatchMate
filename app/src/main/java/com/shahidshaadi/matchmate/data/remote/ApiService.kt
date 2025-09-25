package com.shahidshaadi.matchmate.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("api/")
    suspend fun getMatches(
        @Query("results") count: Int = 10,
        @Query("page") page: Int
    ): UserResponse
}