import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.yomabankcodingchallenge.data.model.CurrencyResponse
import com.example.yomabankcodingchallenge.data.model.ExchangeRate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class CurrencyViewModel(
) : ViewModel() {

    private val currencyRepository: CurrencyRepository

    init {
        val apiService = RetrofitClient.createApiService()
        currencyRepository = CurrencyRepository(apiService)
    }

    private val _exchangeRates = MutableLiveData<Resource<List<ExchangeRate>>>()
    val exchangeRates: LiveData<Resource<List<ExchangeRate>>> get() = _exchangeRates

    private val _conversionResult = MutableLiveData<Double>()
    val conversionResult: LiveData<Double> get() = _conversionResult

    fun fetchExchangeRates() {
        viewModelScope.launch {
            _exchangeRates.value = Resource.Loading()
            try {
                val currencyResponseResource = currencyRepository.getExchangeRates()

                if (currencyResponseResource is Resource.Success) {

                    val quotes = currencyResponseResource.data?.quotes ?: emptyMap()
                    val source = currencyResponseResource.data?.source ?: "USD"
                    val timestamp = currencyResponseResource.data?.timestamp ?: System.currentTimeMillis()

                    val exchangeRatesList = listOf(
                        ExchangeRate(
                            currency = source,
                            timestamp = timestamp,
                            quotes = quotes
                        )
                    )

                    _exchangeRates.value = Resource.Success(exchangeRatesList)
                } else if (currencyResponseResource is Resource.Error) {
                    _exchangeRates.value = Resource.Error(currencyResponseResource.message ?: "Unknown error")
                }
            } catch (e: Exception) {
                _exchangeRates.value = Resource.Error("Failed to fetch exchange rates: ${e.message}")
            }
        }
    }

    fun convertCurrency(amount: Double, from: String, to: String) {
        viewModelScope.launch {
            val result = currencyRepository.convertCurrencyOnline(from, to, amount)
            _conversionResult.value = result.data?.result
        }
    }
}