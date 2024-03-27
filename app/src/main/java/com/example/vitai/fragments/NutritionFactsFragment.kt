    package com.example.vitai.fragments

import ParsedIngredientsAdapter
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vitai.R
import com.example.vitai.model.Nutrient
import com.example.vitai.model.NutrientRDV
import com.example.vitai.model.NutritionResponse
import com.example.vitai.network.NutritionApiRepository
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate

    class NutritionFactsFragment : Fragment() {

        private lateinit var queryEditText: EditText
        private lateinit var tableLayoutNutritionFacts: TableLayout
        private lateinit var nutritionApiRepository: NutritionApiRepository
        private lateinit var ingredientsRecyclerView: RecyclerView

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
            val view = inflater.inflate(R.layout.fragment_nutrition_facts, container, false)

            // Initialize NutritionApiRepository
            nutritionApiRepository = NutritionApiRepository(requireContext())

            ingredientsRecyclerView = view.findViewById<RecyclerView>(R.id.ingredientsRecyclerView)
            ingredientsRecyclerView.layoutManager = LinearLayoutManager(context)
            ingredientsRecyclerView.adapter = ParsedIngredientsAdapter(emptyList())

            tableLayoutNutritionFacts = view.findViewById(R.id.tableLayoutNutritionFacts)
            val pieChart = view.findViewById<PieChart>(R.id.pieChart)
            pieChart?.setNoDataText("")

            // Correctly initialize queryEditText at the class level
            queryEditText = view.findViewById(R.id.queryEditText)

            // Now queryEditText is the class variable, and you can safely use it
            queryEditText.textSize = 20f // Set text size

            val clearButton = view.findViewById<Button>(R.id.clearButton)
            clearButton.setOnClickListener {
                queryEditText.setText("") // Use queryEditText directly
            }

            val editText = view?.findViewById<EditText>(R.id.queryEditText)
            editText?.textSize = 20f // Text size in sp

            // Calculate the top padding in pixels (for 16dp conversion)
            val density = resources.displayMetrics.density
            val topPadding = (16 * density).toInt() // Converts 16dp into pixels

            // Apply existing left, right, bottom padding, and new top padding
            tableLayoutNutritionFacts.setPadding(
            tableLayoutNutritionFacts.paddingLeft, // Keep existing left padding
            topPadding, // New top padding
            tableLayoutNutritionFacts.paddingRight, // Keep existing right padding
            tableLayoutNutritionFacts.paddingBottom // Keep existing bottom padding
            )

            val submitQueryButton: Button = view.findViewById(R.id.submitQueryButton)
            submitQueryButton.setOnClickListener {
                val query = queryEditText.text.toString()
                // Use the repository to fetch nutrition facts
                nutritionApiRepository.searchNutritionalFacts(query, onSuccess = { nutritionInfo ->
                    // If successful, update UI. Ensure updates happen on the main thread.
                    activity?.runOnUiThread {
                        if (nutritionInfo != null) {
                            displayNutritionFacts(nutritionInfo) // this is another function
                            setupPieChart(nutritionInfo)
                            displayIngredients(nutritionInfo)
                        } else {
                            Toast.makeText(context, "No data received", Toast.LENGTH_LONG).show()
                        }
                    }
                }, onError = { errorMessage ->
                    // Handle error. Ensure error handling happens on the main thread.
                    activity?.runOnUiThread {
                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                    }
                })
            }

            return view
        }

        class CustomPercentFormatter(private val chart: PieChart) : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                // Only show percentages for values above 11%
                return if (value > 11) String.format("%.1f%%", value) else ""
            }
        }
        private fun setupPieChart(nutritionData: NutritionResponse) {
            val pieChart = view?.findViewById<PieChart>(R.id.pieChart) ?: return
            val legendText = ContextCompat.getColor(requireContext(), R.color.legend_text)

            val caloriesFromProtein = nutritionData.totalNutrientsKCal.proteinCalories.quantity.toFloat()
            val caloriesFromFat = nutritionData.totalNutrientsKCal.fatCalories.quantity.toFloat()
            val caloriesFromCarbs = nutritionData.totalNutrientsKCal.carbCalories.quantity.toFloat()

            val totalCalories = caloriesFromProtein + caloriesFromFat + caloriesFromCarbs
            val entries = ArrayList<PieEntry>().apply {
                add(PieEntry(caloriesFromProtein / totalCalories * 100, "Protein"))
                add(PieEntry(caloriesFromFat / totalCalories * 100, "Fat"))
                add(PieEntry(caloriesFromCarbs / totalCalories * 100, "Carbs"))
            }

            val dataSet = PieDataSet(entries, "Caloric Breakdown").apply {
                setColors(listOf(ColorTemplate.MATERIAL_COLORS[1], ColorTemplate.MATERIAL_COLORS[2], ColorTemplate.MATERIAL_COLORS[0]))
                valueTextColor = Color.WHITE
                valueTextSize = 14f
                valueTypeface = Typeface.DEFAULT_BOLD // Set value typeface to bold
                valueFormatter = PercentFormatter(pieChart) // Format values as percentages
            }

            val data = PieData(dataSet).apply {
                setValueTextSize(14f)
                dataSet.sliceSpace = 5f // Adjust the space value as needed
                dataSet.valueFormatter = CustomPercentFormatter(pieChart)
                setValueTextColor(Color.BLACK)
                setValueTypeface(Typeface.DEFAULT_BOLD) // Set the typeface for value text to bold
                pieChart.legend.isEnabled = true
                pieChart.legend.setDrawInside(false)
                pieChart.legend.textColor = legendText
                pieChart.legend.textSize = 14f
                pieChart.legend.xEntrySpace = 12f
            }

            pieChart.apply {
                this.data = data
                description.isEnabled = false
                isDrawHoleEnabled = true
                setHoleColor(Color.TRANSPARENT)
                setEntryLabelColor(Color.WHITE)
                setEntryLabelTypeface(Typeface.DEFAULT_BOLD) // Set label typeface to bold
                setUsePercentValues(true)
                animateY(1400, com.github.mikephil.charting.animation.Easing.EaseInOutQuad)

                // Enable rotation of the chart by touch
                isRotationEnabled = true

                // Disable the labels if segments are too small, or adjust as needed
                setDrawEntryLabels(false) // Disables drawing of labels on each segment
                invalidate() // Refresh the chart
            }
        }

        private fun displayIngredients(nutritionResponse: NutritionResponse) {
            val parsedIngredientsList = nutritionResponse.ingredients.flatMap { it.parsed }
            (ingredientsRecyclerView.adapter as? ParsedIngredientsAdapter)?.updateIngredients(parsedIngredientsList)
        }


        // The rest of the code that uses resultsTextView should be removed or commented out.
        private fun displayNutritionFacts(nutritionInfo: NutritionResponse) {
            activity?.runOnUiThread {
                tableLayoutNutritionFacts.removeAllViews()
                val helper = TableLayoutHelper(requireContext(), tableLayoutNutritionFacts)
                // Add Macro Nutrients header
                tableLayoutNutritionFacts.addView(helper.createHeaderRow("Macro Nutrients"))
                // Add the "Daily Value*" header for Macro Nutrients
                tableLayoutNutritionFacts.addView(helper.createDailyValueHeader("Daily Value*"))
                tableLayoutNutritionFacts.addView(helper.addSectionSpacing())

    // First, display calories as a special case since it's not in the totalNutrients
                helper.addNutritionRow("Calories", Nutrient(nutritionInfo.calories.toFloat(), "kcal"), nutritionInfo.totalDaily.energy, true)

    // Now, iterate through the rest of the nutrients
                nutritionInfo.totalNutrients.apply {
                    helper.addNutritionRow("Total Fat", fat, nutritionInfo.totalDaily.fat, isBold = true)
                    helper.addNutritionRow("Saturated Fat", saturatedFat, nutritionInfo.totalDaily.saturatedFat, isBold = false)
                    helper.addNutritionRow("Trans Fat", transFat, nutritionInfo.totalDaily.transFat, isBold = false)
                    helper.addNutritionRow("Cholesterol", cholesterol, nutritionInfo.totalDaily.cholesterol, isBold = true)
                    helper.addNutritionRow("Sodium", sodium, nutritionInfo.totalDaily.sodium, isBold = true)
                    helper.addNutritionRow("Total Carbohydrate", carbohydrates, nutritionInfo.totalDaily.carbohydrates, isBold = true)
                    helper.addNutritionRow("Total Sugars", sugars, nutritionInfo.totalDaily.sugars, isBold = false)
                    helper.addNutritionRow("Dietary Fiber", fiber, nutritionInfo.totalDaily.fiber, isBold = false)
                    helper.addNutritionRow("Protein", protein, nutritionInfo.totalDaily.protein, isBold = true)
                    // Add additional rows for other nutrients such as vitamins and minerals here
                }

                // Add spacing before the Micro Nutrients section
                tableLayoutNutritionFacts.addView(helper.addSectionSpacing())
                tableLayoutNutritionFacts.addView(helper.addSectionSpacing())
                tableLayoutNutritionFacts.addView(helper.addSectionSpacing())

                // Add Micro Nutrients header
                tableLayoutNutritionFacts.addView(helper.createHeaderRow("Micro Nutrients"))
                // Add the "Daily Value*" header for Macro Nutrients
                tableLayoutNutritionFacts.addView(helper.createDailyValueHeader("Daily Value*"))
                tableLayoutNutritionFacts.addView(helper.addSectionSpacing())

                // Displaying micronutrients with RDVs
                mapOf(
                    "Calcium" to Pair(nutritionInfo.totalNutrients.calcium, nutritionInfo.totalDaily.calcium),
                    "Iron" to Pair(nutritionInfo.totalNutrients.iron, nutritionInfo.totalDaily.iron),
                    "Magnesium" to Pair(nutritionInfo.totalNutrients.magnesium, nutritionInfo.totalDaily.magnesium),
                    "Potassium" to Pair(nutritionInfo.totalNutrients.potassium, nutritionInfo.totalDaily.potassium),
                    "Zinc" to Pair(nutritionInfo.totalNutrients.zinc, nutritionInfo.totalDaily.zinc),
                    "Phosphorus" to Pair(nutritionInfo.totalNutrients.phosphorus, nutritionInfo.totalDaily.phosphorus),
                    "Vitamin A" to Pair(nutritionInfo.totalNutrients.vitaminA, nutritionInfo.totalDaily.vitaminA),
                    "Vitamin C" to Pair(nutritionInfo.totalNutrients.vitaminC, nutritionInfo.totalDaily.vitaminC),
                    "Thiamin" to Pair(nutritionInfo.totalNutrients.thiamin, nutritionInfo.totalDaily.thiamin),
                    "Riboflavin" to Pair(nutritionInfo.totalNutrients.riboflavin, nutritionInfo.totalDaily.riboflavin),
                    "Niacin" to Pair(nutritionInfo.totalNutrients.niacin, nutritionInfo.totalDaily.niacin),
                    "Vitamin B6" to Pair(nutritionInfo.totalNutrients.vitaminB6, nutritionInfo.totalDaily.vitaminB6),
                    "Folate" to Pair(nutritionInfo.totalNutrients.folate, nutritionInfo.totalDaily.folate),
                    "Vitamin B12" to Pair(nutritionInfo.totalNutrients.vitaminB12, nutritionInfo.totalDaily.vitaminB12),
                    "Vitamin D" to Pair(nutritionInfo.totalNutrients.vitaminD, nutritionInfo.totalDaily.vitaminD),
                    "Vitamin E" to Pair(nutritionInfo.totalNutrients.vitaminE, nutritionInfo.totalDaily.vitaminE),
                    "Vitamin K" to Pair(nutritionInfo.totalNutrients.vitaminK, nutritionInfo.totalDaily.vitaminK)
                ).forEach { (name, pair) ->
                    val (nutrient, rdv) = pair
                    helper.addNutritionRow(name, nutrient, rdv, true)
                }
                tableLayoutNutritionFacts.addView(helper.addSectionSpacing())
                // Add footer note
                tableLayoutNutritionFacts.addView(helper.createFooterRow("*Based on 2000 daily calories"))
                tableLayoutNutritionFacts.addView(helper.addSectionSpacing())
                tableLayoutNutritionFacts.addView(helper.addSectionSpacing())
                tableLayoutNutritionFacts.addView(helper.addSectionSpacing())
            }
        }
    }

    class TableLayoutHelper(private val context: Context, private val tableLayout: TableLayout) {

        fun createHeaderRow(text: String, marginTop: Int = 0): TableRow {
            val headerTextView = TextView(context).apply {
                this.text = text
                textSize = 24f // Adjust header font size as needed
                setTypeface(null, Typeface.BOLD)
                gravity = Gravity.START
            }

            return TableRow(context).apply {
                addView(headerTextView)
                val layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT)
                layoutParams.setMargins(0, marginTop, 0, 16) // Increase marginTop as needed for spacing above the header
                this.layoutParams = layoutParams
            }
        }

        fun createDailyValueHeader(text: String): TableRow {
            val row = TableRow(context)
            val textView = TextView(context).apply {
                layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT)
                gravity = Gravity.END // Ensure this aligns it with the RDV values as per your layout
                this.text = text
                textSize = 22f
                setTypeface(null, Typeface.BOLD)
            }
            // This assumes your RDV values are in the last cell. Adjust the column span if needed.
            row.addView(textView, TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1f).apply {
                gravity = Gravity.END
            })
            return row
        }

        fun addNutritionRow(nutrientName: String, nutrient: Nutrient?, dailyValue: NutrientRDV?, isBold: Boolean) {
            val nutrientText = StringBuilder().apply {
                append(nutrientName)
                nutrient?.let {
                    append(": ${String.format("%.1f %s", it.quantity, it.unit)}")
                }
            }.toString()

            val nutrientSpannable = SpannableString(nutrientText).apply {
                if (isBold) {
                    setSpan(StyleSpan(Typeface.BOLD), 0, nutrientName.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                }
            }

            val rdvText = dailyValue?.let {
                "${String.format("%.0f%%", it.quantity)}"
            } ?: ""

            val rdvSpannable = SpannableString(rdvText).apply {
                setSpan(StyleSpan(Typeface.BOLD), 0, rdvText.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
            }

            val nutrientTextView = TextView(context).apply {
                text = nutrientSpannable
                textSize = 22f
                gravity = Gravity.START
            }

            val rdvTextView = TextView(context).apply {
                text = rdvSpannable
                textSize = 22f
                gravity = Gravity.END
            }

            val row = TableRow(context).apply {
                addView(nutrientTextView)
                addView(rdvTextView, TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f))
                layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT).apply {
                    marginStart = 16
                    marginEnd = 16
                }
            }

            tableLayout.addView(row)
        }

        fun createFooterRow(text: String): TableRow {
            val footerTextView = TextView(context).apply {
                this.text = text
                textSize = 18f // Adjust footer font size as needed

                gravity = Gravity.END // Align to the right
            }

            return TableRow(context).apply {
                addView(footerTextView)
                layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT).apply {
                    setMargins(0, 16, 0, 32) // Adjust margins as needed
                }
            }
        }

        fun addSectionSpacing(): TableRow {
            return TableRow(context).apply {
                val spacerView = View(context)
                spacerView.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 32) // 32px spacing
                addView(spacerView)
            }
        }
    }
    class IngredientsTableHelper(private val context: Context, private val tableLayout: TableLayout) {

        fun addIngredientRow(quantity: String, unit: String, food: String, calories: String, weight: String) {
            val row = TableRow(context).apply {
                addView(createTextView(quantity, Typeface.NORMAL, Gravity.START, 1f))
                addView(createTextView(unit, Typeface.NORMAL, Gravity.START, 1f))
                addView(createTextView(food, Typeface.NORMAL, Gravity.START, 1f)) // Food name might need more space
                addView(createTextView(calories, Typeface.NORMAL, Gravity.START, 1f))
                addView(createTextView(weight, Typeface.NORMAL, Gravity.START, 1f))
                setBackgroundResource(R.drawable.clear_bg) // Optional: Define a drawable for row background
            }
            tableLayout.addView(row, TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT).apply {
                topMargin = 8 // Add spacing above each row
                bottomMargin = 8 // Add spacing below each row
            })
        }

        private fun createTextView(text: String, style: Int, gravity: Int, weight: Float): TextView {
            return TextView(context).apply {
                this.text = text
                setTypeface(null, style)
                this.gravity = gravity
                textSize = 18f // Consider making this adjustable
                layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, weight).apply {
                    leftMargin = 8
                    rightMargin = 8
                }
                this.setTextColor(ContextCompat.getColor(context, R.color.white))
                setPadding(16, 16, 16, 16) // Add padding for better spacing within cells
            }
        }

        fun addHeaderRow() {
            val headerRow = TableRow(context).apply {
                addView(createTextView("Qty", Typeface.BOLD, Gravity.CENTER, 1f))
                addView(createTextView("Unit", Typeface.BOLD, Gravity.CENTER, 1f))
                addView(createTextView("Food", Typeface.BOLD, Gravity.CENTER, 2f)) // Giving more space to the food column
                addView(createTextView("Calories", Typeface.BOLD, Gravity.CENTER, 1f))
                addView(createTextView("Weight", Typeface.BOLD, Gravity.CENTER, 1f))
                setBackgroundResource(R.drawable.button_bg) // Use a distinct color or drawable for the header background
            }
            tableLayout.addView(headerRow, TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT).apply {
                // Adjust margins if needed, similar to ingredient rows for consistency
                topMargin = 8
                bottomMargin = 8
            })
        }

        // Optional method to clear the table
        fun clearTable() {
            tableLayout.removeAllViews()
        }
    }
