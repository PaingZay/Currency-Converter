import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.yomabankcodingchallenge.R
import com.example.yomabankcodingchallenge.data.model.ExchangeRate

class ExchangeRateAdapter(private val exchangeRates: List<ExchangeRate>) : RecyclerView.Adapter<ExchangeRateAdapter.QuoteViewHolder>() {

    class QuoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val currencyTextView: TextView = itemView.findViewById(R.id.currencyTextView)
        val rateTextView: TextView = itemView.findViewById(R.id.rateTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_exchange_rate, parent, false)
        return QuoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuoteViewHolder, position: Int) {
        val totalQuotes = exchangeRates.sumOf { it.quotes.size }

        var currentPosition = position
        for (exchangeRate in exchangeRates) {
            if (currentPosition < exchangeRate.quotes.size) {
                val quoteEntry = exchangeRate.quotes.entries.elementAt(currentPosition)
                holder.currencyTextView.text = quoteEntry.key
                holder.rateTextView.text = quoteEntry.value.toString()
                return
            }
            currentPosition -= exchangeRate.quotes.size
        }
    }

    override fun getItemCount(): Int {
        return exchangeRates.sumOf { it.quotes.size }
    }
}