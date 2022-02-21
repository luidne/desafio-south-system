package br.com.southsystem.events.ui.eventdetails

import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import br.com.southsystem.events.R
import br.com.southsystem.events.data.model.Event
import br.com.southsystem.events.data.repository.EventRepository
import br.com.southsystem.events.ui.eventdetails.viewmodel.EventFactory
import br.com.southsystem.events.ui.eventdetails.viewmodel.EventViewModel
import br.com.southsystem.events.ui.events.EVENT_ID
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.squareup.picasso.Picasso
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
import br.com.southsystem.events.ui.login.LoginActivity
import br.com.southsystem.events.data.repository.LoginDataSource
import br.com.southsystem.events.data.repository.LoginRepository
import br.com.southsystem.events.ui.login.loginStartActivity
import br.com.southsystem.events.util.dateToString
import br.com.southsystem.events.util.toBRL

import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.snackbar.Snackbar
import kotlin.math.abs

class EventDetailsFragment :
    Fragment(),
    AppBarLayout.OnOffsetChangedListener,
    NestedScrollView.OnScrollChangeListener {

    companion object {
        fun newInstance() = EventDetailsFragment()
    }

    private val activityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if(result.resultCode == Activity.RESULT_OK) {
            eventViewModel.checkin {}
        }
    }

    private val percentageToShowImage = 20
    private var titleCollapsingToolbarLayout = ""
    private var maxScrollSize = 0
    private var isImageHidden = false
    private lateinit var fabShare: View
    private lateinit var fabCheckin: View

    private lateinit var collapseToolbarLayout: CollapsingToolbarLayout
    private lateinit var scrollView: NestedScrollView
    private lateinit var imageViewHeader: ImageView
    private lateinit var textViewTitle: TextView
    private lateinit var textViewDate: TextView
    private lateinit var buttonShowMap: Button
    private lateinit var textViewPrice: TextView
    private lateinit var textViewDescription: TextView

    private var eventId = 0

    private val eventViewModel: EventViewModel by lazy {
        val factory = EventFactory(eventId, EventRepository(), LoginRepository(LoginDataSource()))
        val provider = ViewModelProvider(this, factory)
        provider.get(EventViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().intent.extras?.let {
            eventId = it.getInt(EVENT_ID, 0)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_event_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = view.findViewById(R.id.toolbar) as Toolbar
        toolbar.setNavigationOnClickListener { requireActivity().onBackPressed() }
        val appbar = view.findViewById(R.id.event_detai_appbar) as AppBarLayout
        appbar.addOnOffsetChangedListener(this)

        findViews()

        scrollView.setOnScrollChangeListener(this)

        fabShare.setOnClickListener {
            eventViewModel.eventLiveData.value?.let { event ->
                event.let {
                    shareEvent(it)
                }
            }
        }

        fabCheckin.setOnClickListener {
            eventViewModel.checkin {
                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.putExtra(loginStartActivity, false)
                activityResultLauncher.launch(intent)
            }
        }

        subscribeObservers()

        eventViewModel.get()
    }

    private fun subscribeObservers() {
        eventViewModel.checkinLiveData
            .observe(viewLifecycleOwner, { checkin ->
                checkin?.let {
                    statusCheckin(it)
                }
            })

        eventViewModel.eventLiveData
            .observe(viewLifecycleOwner, { result ->
                result?.let {
                    fillViews(it)
                    titleCollapsingToolbarLayout = it.title
                }
            })

        eventViewModel.errorLiveData
            .observe(viewLifecycleOwner, { error ->
                error?.let {
                    Snackbar.make(
                        requireView(),
                        "Ops! $error",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            })
    }

    fun shareEvent(event: Event) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT,
                """${getString(R.string.share_header)}
                    ${event.title} | ${event.date.dateToString()}""".trimMargin())
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    fun showMap(lat: Double, lng: Double) {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://www.google.com/maps/search/?api=1&query=$lat,$lng")
        )
        startActivity(intent)
    }

    fun statusCheckin(checkin: Boolean) {
        if(checkin) {
            Toast.makeText(
                requireContext(),
                getString(R.string.checkin_success),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.checkin_failed),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun findViews() {
        fabShare = requireView().findViewById(R.id.event_detail_fab_share)
        fabCheckin = requireView().findViewById(R.id.event_detail_fab_checkin)
        buttonShowMap = requireView().findViewById(R.id.buttonShowMap)
        collapseToolbarLayout = requireView().findViewById(R.id.collapsingToobarLayout)
        scrollView = requireView().findViewById(R.id.event_details_scrollview)
        imageViewHeader = requireView().findViewById(R.id.imageViewHeader)
        textViewDate = requireView().findViewById(R.id.textViewDate)
        textViewTitle = requireView().findViewById(R.id.textViewTitle)
        textViewPrice = requireView().findViewById(R.id.textViewPrice)
        textViewDescription = requireView().findViewById(R.id.textViewDescription)
    }

    private fun fillViews(event: Event) {
        Picasso.get()
            .load(event.image)
            .placeholder(R.drawable.placeholder_header)
            .into(imageViewHeader)

        textViewTitle.text = event.title
        textViewDate.text = event.date.dateToString()
        buttonShowMap.setOnClickListener {
            showMap(event.latitude, event.longitude)
        }
        textViewPrice.text = event.price.toBRL()
        textViewDescription.text = event.description
    }

    private fun isViewOnScreen(view: View, viewContainer: View): Boolean {
        val scrollBounds = Rect()
        viewContainer.getHitRect(scrollBounds)
        return (view.getLocalVisibleRect(scrollBounds))
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout, i: Int) {
        if (maxScrollSize == 0) maxScrollSize = appBarLayout.totalScrollRange

        val currentScrollPercentage: Int = (abs(i) * 100
                / maxScrollSize)

        if (currentScrollPercentage >= percentageToShowImage) {
            if (!isImageHidden) {
                isImageHidden = true
                ViewCompat.animate(fabShare).scaleY(0f).scaleX(0f).start()
            }
        }

        if (currentScrollPercentage < percentageToShowImage) {
            if (isImageHidden) {
                isImageHidden = false
                ViewCompat.animate(fabShare).scaleY(1f).scaleX(1f).start()
            }
        }
    }

    override fun onScrollChange(
        v: NestedScrollView?,
        scrollX: Int,
        scrollY: Int,
        oldScrollX: Int,
        oldScrollY: Int
    ) {
        if(!isViewOnScreen(textViewTitle, scrollView)) {
            collapseToolbarLayout.title = titleCollapsingToolbarLayout
        } else {
            collapseToolbarLayout.title = ""
        }
    }
}