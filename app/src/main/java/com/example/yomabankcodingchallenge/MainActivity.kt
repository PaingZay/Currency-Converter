package com.example.yomabankcodingchallenge

import CurrencyApiService
import CurrencyRepository
import CurrencyViewModel
import ExchangeRateAdapter
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yomabankcodingchallenge.data.model.CurrencyResponse
import com.example.yomabankcodingchallenge.data.model.ExchangeRate

class MainActivity : AppCompatActivity() {
    private lateinit var currencySpinner: Spinner
    private lateinit var amountEditText: EditText
    private lateinit var ratesRecyclerView: RecyclerView
    private lateinit var adapter: ExchangeRateAdapter

    // Use the by viewModels() delegate to initialize the ViewModel
    private val viewModel: CurrencyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ratesRecyclerView = findViewById(R.id.recyclerView)
        ratesRecyclerView.layoutManager = LinearLayoutManager(this)

        viewModel.exchangeRates.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    // Show loading indicator
                }
                is Resource.Success -> {
                    resource.data?.let { exchangeRates ->
                        adapter = ExchangeRateAdapter(exchangeRates)
                        ratesRecyclerView.adapter = adapter
                    }
                }
                is Resource.Error -> {
                    // Show error message
                }

                else -> {}
            }
        }

        // Fetch exchange rates using the ViewModel
        viewModel.fetchExchangeRates()

        loadCurrencies()

        findViewById<Button>(R.id.convertButton).setOnClickListener { convertCurrency() }
    }

    private fun showLoading() {
    }

    private fun hideLoading() {
    }

    private fun displayRates(data: CurrencyResponse?) {
    }

    private fun showError(message: String?) {
    }

    private fun loadCurrencies() {
    }

    private fun convertCurrency() {
    }
}