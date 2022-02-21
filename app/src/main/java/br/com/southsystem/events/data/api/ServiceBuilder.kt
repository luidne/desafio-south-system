package br.com.southsystem.events.data.api

import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

object ServiceBuilder {

    private val client = OkHttpClient.Builder().build()
    private val gson: GsonBuilder = GsonBuilder().registerTypeAdapter(
        Date::class.java,
        JsonDeserializer { json, _, _ -> Date(json.asLong) }
    )

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://5f5a8f24d44d640016169133.mockapi.io/api/")
        .addConverterFactory(GsonConverterFactory.create(gson.create()))
        .client(client)
        .build()

    fun<T> buildService(service: Class<T>): T{
        return retrofit.create(service)
    }

}