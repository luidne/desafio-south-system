package br.com.southsystem.events.ui.events.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.southsystem.events.data.model.Event
import br.com.southsystem.events.data.repository.EventRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EventsViewModel (private val repository: EventRepository) : ViewModel() {

    var eventsLiveData = MutableLiveData<List<Event>>(emptyList())
    var loadingLiveData = MutableLiveData<Boolean?>()
    var errorLiveData = MutableLiveData<String?>(null)

    fun getAll() : LiveData<List<Event>> {
        loadingLiveData.value = true

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val all = repository.getAll()

                withContext(Dispatchers.Main) {
                    eventsLiveData.value = all
                }
            } catch (ex: Exception) {
                Log.e(javaClass.simpleName, ex.toString())
                errorLiveData.postValue(ex.toString())
            }

            loadingLiveData.postValue(false)
        }

        return eventsLiveData
    }

}