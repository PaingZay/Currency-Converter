import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yomabankcodingchallenge.data.database.AppDatabase
import com.example.yomabankcodingchallenge.data.model.ExchangeRate
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CurrencyViewModel(private val context: Context
) : ViewModel() {

    private val db = AppDatabase.getDatabase(context)

    private val currencyRepository: CurrencyRepository

    init {
        val apiService = RetrofitClient.createApiService()
        currencyRepository = CurrencyRepository(apiService)
//        startPeriodicFetch()
    }

    private val _exchangeRates = MutableLiveData<Resource<List<ExchangeRate>>>()
    val exchangeRates: LiveData<Resource<List<ExchangeRate>>> get() = _exchangeRates

    private val _conversionResult = MutableLiveData<Double?>()
    val conversionResult: MutableLiveData<Double?> get() = _conversionResult

    private val _conversionTimestamp = MutableLiveData<Long?>()
    val conversionTimestamp: LiveData<Long?> get() = _conversionTimestamp

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _remainingTime = MutableLiveData<Long>()
    val remainingTime: LiveData<Long> get() = _remainingTime

    private var lastFetchTime: Long = 0
    private val fetchInterval = 30 * 60 * 1000

    fun getExchangeRate(source: String) {
        viewModelScope.launch {
            val currentTime = System.currentTimeMillis()
            val localExchangeRates = db.exchangeRateDao().getExchangeRatesBySource(source)

            if (localExchangeRates.isEmpty() || (currentTime - lastFetchTime) >= fetchInterval) {
                fetchExchangeRates(source)
            } else {
                _exchangeRates.value = Resource.Success(localExchangeRates)
            }
        }
    }

    fun loadExchangeRates(source: String) {
        viewModelScope.launch {
            val localExchangeRates = db.exchangeRateDao().getExchangeRatesBySource(source)
            _exchangeRates.value = Resource.Success(localExchangeRates)
        }
    }

    fun fetchExchangeRates(source: String) {
        viewModelScope.launch {
            _exchangeRates.value = Resource.Loading()
            try {
                val currencyResponseResource = currencyRepository.fetchExchangeRates(source)

                if (currencyResponseResource is Resource.Success) {

                    val quotes = currencyResponseResource.data?.quotes ?: emptyMap()
                    val source = currencyResponseResource.data?.source ?: "USD"
                    val timestamp = currencyResponseResource.data?.timestamp ?: System.currentTimeMillis()

                    val exchangeRatesList = listOf(
                        ExchangeRate(
                            source = source,
                            timestamp = timestamp,
                            quotes = quotes
                        )
                    )

                    db.exchangeRateDao().insertExchangeRates(exchangeRatesList)
                    _exchangeRates.value = Resource.Success(exchangeRatesList)

                } else if (currencyResponseResource is Resource.Error) {
                    _exchangeRates.value = Resource.Error(currencyResponseResource.message ?: "Unknown error")
                }
            } catch (e: Exception) {
                _exchangeRates.value = Resource.Error("Failed to fetch exchange rates: ${e.message}")
            }
        }
    }

//    private var periodicFetchJob: Job? = null
//
//    private fun startPeriodicFetch() {
//        // Cancel any existing job to prevent multiple timers
//        periodicFetchJob?.cancel()
//
//        periodicFetchJob = viewModelScope.launch {
//            while (true) {
//                val currentTime = System.currentTimeMillis()
//                val timeSinceLastFetch = currentTime - lastFetchTimestamp
//
//                val remainingTimeMillis = refreshIntervalMillis - timeSinceLastFetch
//
//                if (remainingTimeMillis <= 0) {
//                    // Time to refresh
//                    _remainingTime.value = remainingTimeMillis
//
//                      // Exit the loop after fetching, a new job will be started
//                } else {
//                    fetchExchangeRates()
//                    Log.d("CurrencyViewModel", "Remaining time: $remainingTimeMillis")
//                }
//
//                delay(timerUpdateIntervalMillis)
//            }
//        }
//    }

//    override fun onCleared() {
//        super.onCleared()
//        periodicFetchJob?.cancel()
//    }

    fun convertCurrency(amount: Double, from: String, to: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val result = currencyRepository.convertCurrencyOnline(from, to, amount)
                _conversionResult.value = result.data?.result
                _conversionTimestamp.value = result.data?.info?.timestamp
            } catch (e: Exception) {
                _conversionResult.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }
}