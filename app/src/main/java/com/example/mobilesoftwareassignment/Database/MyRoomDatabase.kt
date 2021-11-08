package com.example.mobilesoftwareassignment.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

@Database(entities = [ImageData::class], exportSchema = false, version = 1)
abstract class MyRoomDatabase: RoomDatabase() {
    abstract fun myDao(): MyDAO

    companion object {
        private var INSTANCE: MyRoomDatabase? = null
        private val mutex = Mutex()

        fun getDatabase(context: Context): MyRoomDatabase? {
            if (INSTANCE == null) {
                runBlocking {
                    withContext(Dispatchers.Default) {
                        // add lock to MyRoomDatabase class
                        mutex.withLock(MyRoomDatabase::class) {
                            INSTANCE = Room.databaseBuilder(
                                context.applicationContext,
                                MyRoomDatabase::class.java, "number_database"
                            )   // Wipes and rebuilds instead of migrating if no Migration object.
                                // Migration is not part of this codelab.
                                .fallbackToDestructiveMigration()
                                .addCallback(sRoomDatabaseCallback)
                                .build()
                        }
                    }
                }
            }
            return INSTANCE
        }

        private val sRoomDatabaseCallback: Callback = object : Callback() {
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                // do any init operation about any initialisation here
            }
        }
    }
}
