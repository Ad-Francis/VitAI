package com.example.vitai.network

import com.example.vitai.model.NutritionResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface EdamamApiService {
    @GET("/api/nutrition-data")
    @Headers("Content-Type: application/json")
    fun getNutritionFacts(
        @Query("app_id") appId: String,
        @Query("app_key") appKey: String,
        @Query("ingr") ingredient: String
    ): Call<NutritionResponse>
}