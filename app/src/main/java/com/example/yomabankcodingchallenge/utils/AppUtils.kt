object Constants {
    const val BASE_URL = "https://api.currencylayer.com/"
    const val API_KEY = "1ad18a90d52eb421eff123fdbd367729"
}

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Loading<T> : Resource<T>()
    class Error<T>(message: String) : Resource<T>(message = message)
}