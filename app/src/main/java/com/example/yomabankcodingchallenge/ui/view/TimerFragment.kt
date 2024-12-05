package com.example.yomabankcodingchallenge.ui.view

import CurrencyViewModel
import CurrencyViewModelFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.yomabankcodingchallenge.R

class TimerFragment : Fragment() {

    private lateinit var viewModel: CurrencyViewModel
    private lateinit var timerTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_timer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        timerTextView = view.findViewById(R.id.timerTextView)

        val factory = CurrencyViewModelFactory(requireContext())
        viewModel = ViewModelProvider(this, factory).get(CurrencyViewModel::class.java)

        viewModel.remainingTime.observe(viewLifecycleOwner) { remainingTimeMillis ->
            Log.d("TimerFragment", "Remaining time observed: $remainingTimeMillis")
            val minutes = (remainingTimeMillis / 60000).toInt()
            val seconds = ((remainingTimeMillis % 60000) / 1000).toInt()
            timerTextView.text = String.format("%02d:%02d", minutes, seconds)
        }
    }
}