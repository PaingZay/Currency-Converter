package com.example.yomabankcodingchallenge.data.database
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.example.yomabankcodingchallenge.data.model.ExchangeRate
import com.example.yomabankcodingchallenge.utils.Converters

@Database(entities = [ExchangeRate::class], version = 1)
@TypeConverters(Converters::class) // Register the converters here
abstract class AppDatabase : RoomDatabase() {
    abstract fun exchangeRateDao(): ExchangeRateDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "exchange_rates_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}