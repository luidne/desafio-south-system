package br.com.southsystem.events.ui.eventdetails.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.southsystem.events.data.repository.EventRepository
import br.com.southsystem.events.data.repository.LoginRepository

class EventFactory(
    private val id: Int,
    private val respository: EventRepository,
    private val loginRespository: LoginRepository
    ) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return EventViewModel(id, respository, loginRespository) as T
    }
}