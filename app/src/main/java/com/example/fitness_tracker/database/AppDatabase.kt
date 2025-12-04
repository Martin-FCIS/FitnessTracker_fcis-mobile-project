package com.example.fitness_tracker.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
@Database(entities = [WaterEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun waterDao(): WaterDao
    companion object{
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context) : AppDatabase{
            if(INSTANCE == null){
                synchronized(AppDatabase::class){
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "water_database"
                    ).build()
                }
            }
            return INSTANCE!!
        }
    }
}