package com.example.mobilesoftwareassignment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.mobilesoftwareassignment.databinding.ActivityMainBinding

class JourneyView : AppCompatActivity() {
    private var journeyViewModel: JourneyViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.journeyViewModel = ViewModelProvider(this)[JourneyViewModel::class.java]

        val startButton: Button = findViewById(R.id.startButton)
        startButton?.setOnClickListener {
            findNavController(R.id.action_JourneyStart_to_JourneyInProgress)
        }
    }
}