package br.com.southsystem.events.data.repository

import br.com.southsystem.events.data.model.Person
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.Mockito
import kotlin.random.Random

class EventRepositoryTest {

    var repository = EventRepository()

    @Test
    fun returnTrue_WhenListNotEmpty() {
        val items = repository.getAll()
        assertTrue(items.isNotEmpty())
    }

    @Test
    fun returnTrue_WhenEventExist() {
        val items = repository.getAll()
        val eventSelected = items[Random.nextInt(items.size - 1)]

        val item = repository.getById(eventSelected.id)

        assertNotNull(item)
    }

    @Test
    fun returnTrue_WhenPersonDoCheckinInOneEvent() {
        val person = Person(eventId = 0, name = "Luídne da Silva Mota", email = "nome@mydomain.com")
        val mockRepository = Mockito.mock(EventRepository::class.java)

        Mockito
            .`when`(mockRepository.checkin(person))
            .thenReturn(true)

        val isSuccess = mockRepository.checkin(person)

        assertTrue(isSuccess)
    }
}