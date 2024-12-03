import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.yomabankcodingchallenge.data.model.CurrencyResponse

class CurrencyRepository(private val apiService: CurrencyApiService) {
    suspend fun getExchangeRates(): Resource<CurrencyResponse> {
        return try {
            val response = apiService.getExchangeRates(
                apiKey = Constants.API_KEY,
                currencies = "EUR,GBP,CAD,PLN,MMK",
                source = "USD",
                format = 1
            )
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }
}