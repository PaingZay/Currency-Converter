package com.example.yomabankcodingchallenge.data.model

data class CurrencyResponse(
    val success : Boolean,
    val terms: String,
    val privacy: String,
    val timestamp: Long,
    val source: String,
    val quotes: Map<String, Double>
)