package com.example.yomabankcodingchallenge

import CurrencyApiService
import CurrencyRepository
import CurrencyViewModel
import ExchangeRateAdapter
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yomabankcodingchallenge.data.model.CurrencyData
import com.example.yomabankcodingchallenge.data.model.CurrencyResponse
import com.example.yomabankcodingchallenge.data.model.ExchangeRate

class MainActivity : AppCompatActivity() {
    private lateinit var amountEditText: EditText
    private lateinit var ratesRecyclerView: RecyclerView
    private lateinit var adapter: ExchangeRateAdapter
    private lateinit var convertButton: Button
    private lateinit var resultTextView: TextView
    private lateinit var fromCurrencySpinner: Spinner
    private lateinit var toCurrencySpinner: Spinner

    // Use the by viewModels() delegate to initialize the ViewModel
    private val viewModel: CurrencyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Initialize app components................................
        ratesRecyclerView = findViewById(R.id.recyclerView)
        ratesRecyclerView.layoutManager = LinearLayoutManager(this)
        convertButton = findViewById<Button>(R.id.convertButton)
        resultTextView = findViewById<TextView>(R.id.resultTextView)
        fromCurrencySpinner = findViewById<Spinner>(R.id.convertFromSpinner)
        toCurrencySpinner = findViewById<Spinner>(R.id.convertToSpinner)
        amountEditText = findViewById<EditText>(R.id.inputAmount)

        //BindData to Spinner.......................................
        val currencyMap = CurrencyData.getCurrencyMap()
        val currencyList = currencyMap.map { (code, name) -> "$code - $name" }

        val currencyOptionadapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencyList)
        currencyOptionadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        fromCurrencySpinner.adapter = currencyOptionadapter
        toCurrencySpinner.adapter = currencyOptionadapter


        //CurrencyConvert Section..................................
        convertButton.setOnClickListener {
            val amountValue = amountEditText.text.toString().toDoubleOrNull()
            val fromCurrency = fromCurrencySpinner.selectedItem.toString().split(" - ")[0]
            val toCurrency = toCurrencySpinner.selectedItem.toString().split(" - ")[0]

            if (amountValue != null) {
                viewModel.convertCurrency(amountValue, fromCurrency, toCurrency)
            } else {
                resultTextView.text = "Please enter a valid amount."
            }
        }

        viewModel.conversionResult.observe(this, Observer { result ->
            resultTextView.text = "Converted Amount: $result"
        })


        //Exchange Section .......................................
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
                    //
                }
                else -> {}
            }
        }

        viewModel.fetchExchangeRates()
    }
}