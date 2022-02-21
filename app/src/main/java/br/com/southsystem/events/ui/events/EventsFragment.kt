package br.com.southsystem.events.ui.events

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import br.com.southsystem.events.R
import br.com.southsystem.events.data.model.Event
import br.com.southsystem.events.data.repository.EventRepository
import br.com.southsystem.events.ui.eventdetails.EventDetailsActivity
import br.com.southsystem.events.ui.events.viewmodel.ListEventsFactory
import br.com.southsystem.events.ui.events.viewmodel.EventsViewModel
import com.google.android.material.snackbar.Snackbar

const val EVENT_ID = "event_id"

class EventsFragment : Fragment() {

    companion object {
        fun newInstance() = EventsFragment()
    }

    private val viewModel: EventsViewModel by lazy {
        val repository = EventRepository()
        val factory = ListEventsFactory(repository)
        val provider = ViewModelProvider(this, factory)
        provider.get(EventsViewModel::class.java)
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_events, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = requireView().findViewById(R.id.recyclerEvents)
        swipeRefresh = requireView().findViewById(R.id.swipeRefresh)

        subscriberObservers()

        swipeRefresh.setOnRefreshListener {
            viewModel.getAll()
        }

        viewModel.getAll()
    }

    private fun subscriberObservers() {
        viewModel.eventsLiveData
            .observe(viewLifecycleOwner, { events ->
                if(events.isNotEmpty()) {
                    val listEventsAdapter = ListEventsAdapter(
                        requireContext(),
                        events.toMutableList()
                    )
                    listEventsAdapter.clickListener = this::showDetails
                    recyclerView.adapter = listEventsAdapter
                }
            })

        viewModel.errorLiveData
            .observe(viewLifecycleOwner, { error ->
                if(error != null) {
                    Snackbar.make(
                        requireView(),
                        "Ops! $error",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            })

        viewModel.loadingLiveData
            .observe(viewLifecycleOwner, { loading ->
                swipeRefresh.isRefreshing = loading ?: true
            })
    }

    fun showDetails(event: Event) {
        val intent = Intent(requireActivity(), EventDetailsActivity::class.java)
        intent.putExtra(EVENT_ID, event.id)
        startActivity(intent)
    }

}