package com.example.razorpaydemo.retrofit

import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.*

interface ApiCall {
    @Headers("Content-Type: application/json")
    @POST()
    fun getRazorPayOrderId(
        @Header("Authorization") credentials: String,
        @Url url: String,
        @Body jsonObject: JsonObject
    ): Call<Any>
}