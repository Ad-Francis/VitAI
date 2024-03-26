package com.example.vitai.network

import android.content.Context
import com.example.vitai.R
import com.example.vitai.model.NutritionResponse
import retrofit2.Call
import android.util.Log
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NutritionApiRepository(private val context: Context) {
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://api.edamam.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService: EdamamApiService = retrofit.create(EdamamApiService::class.java)

    fun searchNutritionalFacts(query: String, onSuccess: (NutritionResponse?) -> Unit, onError: (String) -> Unit) {
        val appId = context.getString(R.string.edamam_app_id)
        val appKey = context.getString(R.string.edamam_app_key)

        apiService.getNutritionFacts(appId, appKey, query).enqueue(object : Callback<NutritionResponse> {
            override fun onResponse(call: Call<NutritionResponse>, response: Response<NutritionResponse>) {
                if (response.isSuccessful) {
                    onSuccess(response.body())
                } else {
                    // Log error detail for non-successful response
                    val errorBody = response.errorBody()?.string()
                    Log.e("API_ERROR", "Non-successful response: $errorBody")
                    onError("Error: ${response.message()} - $errorBody")
                }
            }

            override fun onFailure(call: Call<NutritionResponse>, t: Throwable) {
                // Log failure detail
                Log.e("API_ERROR", "API call failed", t)
                onError("Failed to load nutrition facts. Please try again. Error: ${t.message}")
            }
        })
    }
}