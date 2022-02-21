package br.com.southsystem.events.ui.events

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.southsystem.events.R
import br.com.southsystem.events.data.model.Event
import br.com.southsystem.events.util.dateToString
import br.com.southsystem.events.util.toBRL
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

class ListEventsAdapter(
    private val context: Context,
    private val events: MutableList<Event>,
    var clickListener: (Event) -> Unit = {}
) : RecyclerView.Adapter<ListEventsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.card_event_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event = events[position]
        bind(holder, event)
    }

    override fun getItemCount(): Int {
        return events.size
    }

    fun bind(holder: ViewHolder, event: Event) {
        if(event.image.isNotEmpty()) {
            Picasso.get().setIndicatorsEnabled(true)
            Picasso.get()
                .load(event.image)
                .into(holder.imageView, object: Callback {
                    override fun onSuccess() {
                        holder.imageView.visibility = View.VISIBLE
                    }
                    override fun onError(e: Exception?) {
                        holder.imageView.visibility = View.GONE
                    }
                })
        } else {
            holder.imageView.visibility = View.GONE
        }
        holder.textViewTitle.text = event.title
        holder.textViewDate.text = event.date.dateToString()
        holder.textViewPrice.text = event.price.toBRL()

        holder.itemView.setOnClickListener {
            clickListener(event)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById(R.id.imageView)
        var textViewTitle: TextView = itemView.findViewById(R.id.textViewTitle)
        var textViewDate: TextView = itemView.findViewById(R.id.textViewDate)
        var textViewPrice: TextView = itemView.findViewById(R.id.textViewPrice)
    }

}