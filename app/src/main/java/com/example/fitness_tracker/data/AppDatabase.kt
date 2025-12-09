package com.example.fitness_tracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.fitness_tracker.data.food.FoodDao
import com.example.fitness_tracker.data.food.FoodEntity
import com.example.fitness_tracker.data.water.WaterDao
import com.example.fitness_tracker.data.water.WaterEntity

@Database(entities = [WaterEntity::class, FoodEntity::class], version = 2,exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun waterDao(): WaterDao
    abstract fun foodDao(): FoodDao
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