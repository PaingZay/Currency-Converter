package com.example.yomabankcodingchallenge.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.yomabankcodingchallenge.data.model.ExchangeRate

@Dao
interface ExchangeRateDao {

    @Query("SELECT * FROM exchange_rates")
    suspend fun getAllExchangeRates(): List<ExchangeRate>

    @Query("SELECT * FROM exchange_rates WHERE source = :source")
    suspend fun getExchangeRatesBySource(source: String): List<ExchangeRate>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExchangeRates(exchangeRates: List<ExchangeRate>)
}