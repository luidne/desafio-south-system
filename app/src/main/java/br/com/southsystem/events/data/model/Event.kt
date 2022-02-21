package br.com.southsystem.events.data.model

import java.util.*

data class Event (
    val id: Int,
    val date: Date,
    val title: String,
    val description: String,
    val price: Double,
    val image: String,
    val latitude: Double,
    val longitude: Double,
    val people: List<Person>
)