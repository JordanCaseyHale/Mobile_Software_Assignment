package com.example.mobilesoftwareassignment.Database

import androidx.room.Delete
import androidx.room.Insert

interface MyDAO {
    @Insert
    fun insertAll(vararg ImageData: ImageData?)

    @Delete
    fun delete(ImageData: ImageData?)

    @Delete
    fun deleteAll(vararg ImageData: ImageData?)
}