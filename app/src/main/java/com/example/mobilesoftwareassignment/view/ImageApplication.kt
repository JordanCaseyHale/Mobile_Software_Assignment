package com.example.mobilesoftwareassignment.view

import android.app.Application
import com.example.mobilesoftwareassignment.data.ImageRoomDatabase

// test push
class ImageApplication: Application() {
    val databaseObj: ImageRoomDatabase by lazy { ImageRoomDatabase.getDatabase(this) }
}
