object Constants {
    const val BASE_URL = "https://api.currencylayer.com/"
    const val API_KEY = "498cbcb29ee661e652a4bda266a3d4d4"
}

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Loading<T> : Resource<T>()
    class Error<T>(message: String) : Resource<T>(message = message)
}

object CurrencyConverter {
    fun convert(
        amount: Double,
        rates: Map<String, Double>,
        targetCurrency: String
    ): Double {
        val usdRate = rates["USD$targetCurrency"] ?: 1.0
        return amount * usdRate
    }
}