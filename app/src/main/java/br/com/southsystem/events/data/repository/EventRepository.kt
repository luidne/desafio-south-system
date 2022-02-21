package br.com.southsystem.events.data.repository

import br.com.southsystem.events.data.api.EventEndpointInterface
import br.com.southsystem.events.data.api.ServiceBuilder
import br.com.southsystem.events.data.model.Event
import br.com.southsystem.events.data.model.Person

class EventRepository {

    private var service = ServiceBuilder.buildService(EventEndpointInterface::class.java)

    fun getAll(): List<Event> {
        val items = service.getAll()
            .execute()
            .body()

        return items ?: emptyList()
    }

    fun getById(id: Int): Event? {
        val item = service.getById(id)
            .execute()
            .body()

        return item
    }

    fun checkin(person: Person): Boolean {
        val response = service.checkin(person)
            .execute()
        return response.isSuccessful
    }

}