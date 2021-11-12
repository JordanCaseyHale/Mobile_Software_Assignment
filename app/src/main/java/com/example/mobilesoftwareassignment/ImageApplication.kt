package com.example.mobilesoftwareassignment

import android.app.Application
import com.example.mobilesoftwareassignment.data.ImageRoomDatabase

class ImageApplication: Application() {
    val databaseObj: ImageRoomDatabase by lazy { ImageRoomDatabase.getDatabase(this) }
}
