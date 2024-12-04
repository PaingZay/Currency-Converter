//package com.example.yomabankcodingchallenge.ui.adapter
//
//import android.content.Context
//import android.widget.ArrayAdapter
//
//class CurrencySpinnerAdapter(context: Context, private val currencyList: List<String>) :
//    ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, currencyList) {
//
//    init {
//        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//    }
//
//    override fun getItem(position: Int): String {
//        return currencyList[position]
//    }
//}