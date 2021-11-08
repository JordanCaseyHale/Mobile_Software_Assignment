package com.example.mobilesoftwareassignment.Database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "image", indices = [Index(value = ["id","title"])])
data class ImageData(
    @PrimaryKey(autoGenerate = true)var id: Int = 0,
    @ColumnInfo(name="uri") val imageUri: String,
    @ColumnInfo(name="title") var imageTitle: String,
    @ColumnInfo(name="description") var imageDescription: String? = null,
    @ColumnInfo(name="thumbnailUri") var thumbnailUri: String? = null,
    @ColumnInfo(name="location") var imageLocation: Int = 0
)
