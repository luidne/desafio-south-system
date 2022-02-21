package br.com.southsystem.events.data.api

import br.com.southsystem.events.data.model.Event
import br.com.southsystem.events.data.model.Person
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface EventEndpointInterface {

    @GET("events/{id}")
    fun getById(@Path("id") id: Int): Call<Event>

    @GET("events")
    fun getAll(): Call<List<Event>>

    @Headers("Content-Type: application/json")
    @POST("checkin")
    fun checkin(@Body person: Person): Call<ResponseBody>

}