package com.example.yomabankcodingchallenge.utils

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromQuotesMap(quotes: Map<String, Double>?): String? {
        return Gson().toJson(quotes)
    }

    @TypeConverter
    fun toQuotesMap(quotesString: String?): Map<String, Double>? {
        val type = object : TypeToken<Map<String, Double>>() {}.type
        return Gson().fromJson(quotesString, type)
    }
}