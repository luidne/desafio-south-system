package br.com.southsystem.events

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import br.com.southsystem.events.ui.events.EventsFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, EventsFragment.newInstance())
                .commitNow()
        }
    }
}