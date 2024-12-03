package com.example.yomabankcodingchallenge.data.model

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exchange_rates")
data class ExchangeRate(
    @PrimaryKey @NonNull val currency: String,
    val timestamp: Long,
    val quotes: Map<String, Double>,
)