import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.yomabankcodingchallenge.data.model.CurrencyConversionResponse
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

    suspend fun convertCurrencyOnline(from: String, to: String, amount: Double): Resource<CurrencyConversionResponse> {
        return try {
            val response = apiService.convertCurrency(
                apiKey = Constants.API_KEY,
                from = from,
                to = to,
                amount = amount,
                format = 1
            )

            if (response.success) {
                Resource.Success(response)
            } else {
                Resource.Error("Error: ${response.terms}") // Or handle the error appropriately
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }
}