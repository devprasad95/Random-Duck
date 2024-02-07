package com.example.randomduck

import retrofit2.Response
import retrofit2.http.GET

interface DuckApi {
    @GET("random")
    suspend fun getDuck() : Response<Duck>
}