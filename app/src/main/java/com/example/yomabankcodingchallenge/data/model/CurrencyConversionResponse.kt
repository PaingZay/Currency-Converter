package com.example.yomabankcodingchallenge.data.model

data class CurrencyConversionResponse(
    val success: Boolean,
    val terms: String,
    val privacy: String,
    val query: Query,
    val info: Info,
    val result: Double
)

data class Query(
    val from: String,
    val to: String,
    val amount: Int
)

data class Info(
    val timestamp: Long,
    val quote: Double
)