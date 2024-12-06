package com.example.yomabankcodingchallenge

import CurrencyViewModel
import CurrencyViewModelFactory
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.yomabankcodingchallenge.data.model.CurrencyData
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Locale


class CurrencyConverterFragment : Fragment() {
    private lateinit var amountEditText: EditText
    private lateinit var convertButton: Button
    private lateinit var resultTextView: TextView
    private lateinit var fromCurrencySpinner: Spinner
    private lateinit var toCurrencySpinner: Spinner
    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var viewModel: CurrencyViewModel
    private lateinit var conversionTimestampLabel: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_currency_converter, container, false)

        viewModel = ViewModelProvider(requireActivity(), CurrencyViewModelFactory(requireActivity())).get(CurrencyViewModel::class.java)

        // Initialize UI components
        amountEditText = view.findViewById(R.id.inputAmount)
        convertButton = view.findViewById(R.id.convertButton)
        resultTextView = view.findViewById(R.id.resultTextView)
        fromCurrencySpinner = view.findViewById(R.id.convertFromSpinner)
        toCurrencySpinner = view.findViewById(R.id.convertToSpinner)
        loadingProgressBar = view.findViewById(R.id.loadingProgressBar)
        conversionTimestampLabel = view.findViewById(R.id.convertTimestamp)

        setupCurrencySpinners()

        convertButton.setOnClickListener {
            convertCurrency()
        }

        observeViewModel()
        observeTimestamp()

        return view
    }

    private var selectedCurrency: String? = null

    private fun setupCurrencySpinners() {
        val currencyMap = CurrencyData.getCurrencyMap()
        val currencyCodes = currencyMap.keys.toList()
        val formattedCurrencyList = currencyMap.map { (code, name) -> "$code - $name" }

        val currencyOptionAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, formattedCurrencyList)
        currencyOptionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        fromCurrencySpinner.adapter = currencyOptionAdapter
        toCurrencySpinner.adapter = currencyOptionAdapter

        fromCurrencySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedCurrency = currencyCodes[position]
                amountEditText.hint = "Enter amount in $selectedCurrency"
                viewModel.getExchangeRate(selectedCurrency!!)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                amountEditText.hint = "Enter amount"
            }
        }
    }

    private fun convertCurrency() {
        val amountValue = amountEditText.text.toString().toDoubleOrNull()
        val fromCurrency = fromCurrencySpinner.selectedItem.toString().split(" - ")[0]
        val toCurrency = toCurrencySpinner.selectedItem.toString().split(" - ")[0]

        if (amountValue != null) {
            viewModel.convertCurrency(amountValue, fromCurrency, toCurrency)
        } else {
            resultTextView.text = "Please enter a valid amount."
        }
    }

    private fun observeViewModel() {
        viewModel.conversionResult.observe(viewLifecycleOwner, Observer { result ->
            resultTextView.text = ("$result $selectedCurrency" ?: "Conversion Failed").toString()
        })

        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            loadingProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            convertButton.isEnabled = !isLoading
        })
    }

    private fun observeTimestamp() {
        viewModel.conversionTimestamp.observe(viewLifecycleOwner, Observer { timestamp ->
            timestamp?.let {
                val date = Date(it * 1000)
                val dateFormat = SimpleDateFormat("MMMM d, yyyy, h:mm a", Locale.getDefault())
                val formattedDate = dateFormat.format(date)
                conversionTimestampLabel.text = "$formattedDate"
            }
        })
    }
}