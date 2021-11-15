package com.example.mobilesoftwareassignment

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class JourneyViewModel(application: Application) : AndroidViewModel(application) {
    private var mRepository: JourneyRepository = JourneyRepository(application)
}