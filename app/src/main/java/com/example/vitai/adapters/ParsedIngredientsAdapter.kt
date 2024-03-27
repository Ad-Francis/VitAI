import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vitai.R
import com.example.vitai.model.IngredientParsed

class ParsedIngredientsAdapter(private var ingredientsList: List<IngredientParsed>) :
    RecyclerView.Adapter<ParsedIngredientsAdapter.ViewHolder>() {

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

    fun updateIngredients(newIngredients: List<IngredientParsed>) {
        ingredientsList = newIngredients
        notifyDataSetChanged() // Refresh the RecyclerView
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val parsedIngredient = ingredientsList[position]

        holder.quantityTextView.text = parsedIngredient.quantity.toString()
        holder.unitTextView.text = parsedIngredient.measure
        holder.foodTextView.text = parsedIngredient.food
        // Convert calories to integer before displaying
        // Make sure to handle cases where calories might not be present
        val calories = parsedIngredient.nutrients["ENERC_KCAL"]?.quantity?.toInt() ?: 0
        holder.caloriesTextView.text = "${calories} Kcal"
        // Convert weight to integer before displaying
        val weight = parsedIngredient.weight.toInt()
        holder.weightTextView.text = "${weight} g"
    }

    override fun getItemCount(): Int = ingredientsList.size
}