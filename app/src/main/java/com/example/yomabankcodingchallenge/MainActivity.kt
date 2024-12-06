package com.example.yomabankcodingchallenge

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            loadFragment(CurrencyConverterFragment(), R.id.fragment_container_currency_converter)
            loadFragment(ExchangeRateFragment(), R.id.fragment_container_exchange_rate)
        }
    }

    private fun loadFragment(fragment: Fragment, containerId: Int) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(containerId, fragment)
        transaction.commit()
    }
}