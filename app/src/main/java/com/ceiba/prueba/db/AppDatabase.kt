package com.ceiba.prueba.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null
        private val LOCK = Any()

        operator fun invoke(contex: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(contex).also { instance = it }
        }

        private fun buildDatabase(contex: Context) =
            Room.databaseBuilder(
                contex.applicationContext,
                AppDatabase::class.java,
                "user_db"
            ).build()
    }
}