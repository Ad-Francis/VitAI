
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vitai.R
import com.example.vitai.model.IngredientParsed

class ParsedIngredientsAdapter(private var ingredientsList: List<IngredientParsed>) :
    RecyclerView.Adapter<ParsedIngredientsAdapter.ViewHolder>() {

    private var showHeader = false

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val quantityTextView: TextView = view.findViewById(R.id.quantityTextView)
        val unitTextView: TextView = view.findViewById(R.id.measureTextView)
        val foodTextView: TextView = view.findViewById(R.id.foodTextView)
        val caloriesTextView: TextView = view.findViewById(R.id.caloriesTextView)
        val weightTextView: TextView = view.findViewById(R.id.weightTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.row_parsed_ingredient, parent, false)
        return ViewHolder(view)
    }

    fun updateIngredients(newIngredients: List<IngredientParsed>?) {
        showHeader = !newIngredients.isNullOrEmpty()
        Log.d("RecyclerViewAdapter", "Updating ingredients. showHeader: $showHeader, Items count: ${newIngredients?.size ?: 0}")
        ingredientsList = newIngredients ?: emptyList()
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        val viewType = if (position == 0 && showHeader) TYPE_HEADER else TYPE_ITEM
        Log.d("RecyclerViewAdapter", "getItemViewType for position $position: $viewType")
        return viewType
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("RecyclerViewAdapter", "onBindViewHolder for position $position")
        if (getItemViewType(position) == TYPE_HEADER) {
            // List of all header TextViews
            val headerTextViews = listOf(holder.quantityTextView, holder.unitTextView, holder.foodTextView, holder.caloriesTextView, holder.weightTextView)

            // Set texts and make them bold
            headerTextViews.forEach { textView ->
                textView.apply {
                    text = when (this) {
                        holder.quantityTextView -> "Qty"
                        holder.unitTextView -> "Unit"
                        holder.foodTextView -> "Food"
                        holder.caloriesTextView -> "Cals"
                        holder.weightTextView -> "Weight"
                        else -> ""
                    }
                    setTypeface(typeface, Typeface.BOLD) // Make text bold
                }
            }
        } else {
            // Adjust the position to account for the header
            val itemPosition = if (showHeader) position - 1 else position
            val parsedIngredient = ingredientsList[itemPosition]

            // Binding logic for item data
            val quantityText = if (parsedIngredient.quantity % 1.0 == 0.0) {
                parsedIngredient.quantity.toInt().toString()  // Convert to Int if no fractional part
            } else {
                parsedIngredient.quantity.toString()  // Keep as is if fractional part exists
            }
            holder.quantityTextView.text = quantityText
            holder.unitTextView.text = parsedIngredient.measure
            holder.foodTextView.text = parsedIngredient.food
            val calories = parsedIngredient.nutrients["ENERC_KCAL"]?.quantity?.toInt()?.toString()
            holder.caloriesTextView.text = "${calories}"
            val weight = parsedIngredient.weight.toInt()
            holder.weightTextView.text = "${weight} g"
        }
    }

    override fun getItemCount(): Int = if (showHeader) ingredientsList.size + 1 else ingredientsList.size
}