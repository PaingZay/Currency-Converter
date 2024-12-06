package com.example.yomabankcodingchallenge

import CurrencyViewModel
import CurrencyViewModelFactory
import ExchangeRateAdapter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yomabankcodingchallenge.data.database.AppDatabase
import com.example.yomabankcodingchallenge.data.model.CurrencyData
import kotlinx.coroutines.launch
import org.w3c.dom.Text
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var amountEditText: EditText
    private lateinit var ratesRecyclerView: RecyclerView
    private lateinit var adapter: ExchangeRateAdapter
    private lateinit var convertButton: Button
    private lateinit var resultTextView: TextView
    private lateinit var exchangeRatesLabel: TextView
    private lateinit var fromCurrencySpinner: Spinner
    private lateinit var toCurrencySpinner: Spinner
    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var conversionTimestampLabel: TextView

    // Use the by viewModels() delegate to initialize the ViewModel
    //private val viewModel: CurrencyViewModel by viewModels()
    private lateinit var viewModel: CurrencyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this, CurrencyViewModelFactory(this)).get(CurrencyViewModel::class.java)

        //Initialize app components................................
        ratesRecyclerView = findViewById(R.id.recyclerView)
        ratesRecyclerView.layoutManager = LinearLayoutManager(this)
        convertButton = findViewById<Button>(R.id.convertButton)
        resultTextView = findViewById<EditText>(R.id.resultTextView)
        fromCurrencySpinner = findViewById<Spinner>(R.id.convertFromSpinner)
        toCurrencySpinner = findViewById<Spinner>(R.id.convertToSpinner)
        amountEditText = findViewById<EditText>(R.id.inputAmount)
        loadingProgressBar = findViewById(R.id.loadingProgressBar)
        exchangeRatesLabel = findViewById<TextView>(R.id.currencyExchangeRateLabel)
        conversionTimestampLabel = findViewById<TextView>(R.id.convertTimestamp)


        //BindData to Spinner.......................................
        val currencyMap = CurrencyData.getCurrencyMap()
        val currencyCodes = currencyMap.keys.toList()
        val formattedCurrencyList = currencyMap.map { (code, name) -> "$code - $name" }

        val currencyOptionadapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, formattedCurrencyList)
        currencyOptionadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        fromCurrencySpinner.adapter = currencyOptionadapter
        toCurrencySpinner.adapter = currencyOptionadapter

        fromCurrencySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedCurrency = currencyCodes[position]
                amountEditText.hint = "Enter amount in $selectedCurrency"
                exchangeRatesLabel.text = "$selectedCurrency Exchange Rates"
                viewModel.getExchangeRate(selectedCurrency)
                viewModel.refreshExchangeRates(selectedCurrency)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                amountEditText.hint = "Enter amount"
            }
        }

        //Keyboard Dismiss
        amountEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                amountEditText.inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(amountEditText, InputMethodManager.SHOW_IMPLICIT)
            }
        }
        amountEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(amountEditText.windowToken, 0)
                true
            } else {
                false
            }
        }

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

//        viewModel.

        viewModel.conversionTimestamp.observe(this, Observer { timestamp ->
            timestamp?.let {
                val date = Date(it * 1000)
                val dateFormat = SimpleDateFormat("MMMM d, yyyy, h:mm a", Locale.getDefault())
                val formattedDate = dateFormat.format(date)
                conversionTimestampLabel.text = "$formattedDate"
            }
        })

        viewModel.conversionTimestamp.observe(this, Observer { result ->
            if (result != null) {
                resultTextView.text = "$result"
            } else {
                Toast.makeText(this, "Conversion Failed", Toast.LENGTH_SHORT).show()
            }
        })


        viewModel.isLoading.observe(this, Observer { isLoading ->
            convertButton.isEnabled = !isLoading // Disable button while loading
            if (isLoading) {
                loadingProgressBar.visibility = View.VISIBLE
                convertButton.isEnabled = false
            } else {
                loadingProgressBar.visibility = View.GONE
                convertButton.isEnabled = true
            }
        })

//        Exchange Section .......................................
        viewModel.exchangeRates.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
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

//        viewModel.fetchExchangeRates()

        //Exchange Section .......................................
//        viewModel.exchangeRates.observe(this) { resource ->
//            when (resource) {
//                is Resource.Loading -> {
//                    // Show loading indicator
//                }
//                is Resource.Success -> {
//                    resource.data?.let { exchangeRates ->
//                        adapter = ExchangeRateAdapter(exchangeRates)
//                        ratesRecyclerView.adapter = adapter
//                    }
//                }
//                is Resource.Error -> {
//                    //
//                }
//                else -> {}
//            }
//        }

//        viewModel.loadExchangeRates()
    }
}