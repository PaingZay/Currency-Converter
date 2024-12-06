package com.example.yomabankcodingchallenge

import CurrencyViewModel
import CurrencyViewModelFactory
import ExchangeRateAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ExchangeRateFragment : Fragment() {
    private lateinit var ratesRecyclerView: RecyclerView
    private lateinit var adapter: ExchangeRateAdapter
    private lateinit var viewModel: CurrencyViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_exchange_rate, container, false)

        viewModel = ViewModelProvider(requireActivity(), CurrencyViewModelFactory(requireActivity())).get(CurrencyViewModel::class.java)

        ratesRecyclerView = view.findViewById(R.id.recyclerView)
        ratesRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        observeExchangeRates()

        return view
    }

    private fun observeExchangeRates() {
        viewModel.exchangeRates.observe(viewLifecycleOwner) { resource ->
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
                }

                else -> {}
            }
        }
    }
}