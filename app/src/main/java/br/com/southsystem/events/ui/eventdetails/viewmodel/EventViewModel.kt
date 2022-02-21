package br.com.southsystem.events.ui.eventdetails.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.southsystem.events.data.model.Event
import br.com.southsystem.events.data.model.Person
import br.com.southsystem.events.data.repository.EventRepository
import br.com.southsystem.events.data.repository.LoginRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EventViewModel (
    val id: Int,
    private val repository: EventRepository,
    private val loginRepository: LoginRepository
    ) : ViewModel() {

    var eventLiveData = MutableLiveData<Event?>()
    var checkinLiveData = MutableLiveData<Boolean?>()
    var loadingLiveData = MutableLiveData<Boolean?>()
    var errorLiveData = MutableLiveData<String?>(null)

    fun get() : LiveData<Event?> {
        loadingLiveData.postValue(true)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val event = repository.getById(id)

                withContext(Dispatchers.Main) {
                    eventLiveData.value  = event
                }
            } catch (ex: Exception) {
                errorLiveData.postValue(ex.toString())
            }

            loadingLiveData.postValue(false)
        }

        return eventLiveData
    }

    fun checkin(login: () -> Unit): MutableLiveData<Boolean?> {
        if(loginRepository.isLoggedIn) {
            loadingLiveData.value = true

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val user = LoginRepository.user
                    if (user != null) {
                        val person = Person(user.displayName, user.userId, id)
                        repository.checkin(person).let {
                            checkinLiveData.postValue(it)
                            get()
                        }
                    }
                } catch (ex: Exception) {
                    errorLiveData.postValue(ex.toString())
                }

                loadingLiveData.postValue(false)
            }
        } else {
            login()
        }

        return checkinLiveData
    }

}