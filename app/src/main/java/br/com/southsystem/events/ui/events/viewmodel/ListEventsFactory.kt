package br.com.southsystem.events.ui.events.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.southsystem.events.data.repository.EventRepository

class ListEventsFactory(
    private val respository: EventRepository
    ) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return EventsViewModel(respository) as T
    }
}