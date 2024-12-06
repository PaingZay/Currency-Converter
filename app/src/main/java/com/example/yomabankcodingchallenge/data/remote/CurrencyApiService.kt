import com.example.yomabankcodingchallenge.data.model.CurrencyConversionResponse
import com.example.yomabankcodingchallenge.data.model.CurrencyResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyApiService {
    @GET("live")
    suspend fun getExchangeRates(
        @Query("access_key") apiKey: String,
        @Query("currencies") currencies: String,
        @Query("source") source: String,
        @Query("format") format: Int = 1
    ): CurrencyResponse

        @GET("convert")
        suspend fun convertCurrency(
            @Query("access_key") apiKey: String,
            @Query("from") from: String,
            @Query("to") to: String,
            @Query("amount") amount: Double,
            @Query("format") format: Int = 1
        ): CurrencyConversionResponse

}

object RetrofitClient {
    fun createApiService(): CurrencyApiService {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CurrencyApiService::class.java)
    }
}
