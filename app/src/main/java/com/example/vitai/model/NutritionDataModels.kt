package com.example.vitai.model

import com.google.gson.annotations.SerializedName

data class NutritionResponse(

    val calories: Int,
    val totalWeight: Float,
    val totalNutrientsKCal: TotalNutrientsKCal,
    @SerializedName("totalNutrients") val totalNutrients: TotalNutrients,
    @SerializedName("totalDaily") val totalDaily: TotalDaily, // Include RDV data
    val ingredients: List<IngredientDetail>
)

data class TotalNutrientsKCal(
    @SerializedName("ENERC_KCAL") val totalCalories: KCalData,
    @SerializedName("PROCNT_KCAL") val proteinCalories: KCalData,
    @SerializedName("FAT_KCAL") val fatCalories: KCalData,
    @SerializedName("CHOCDF_KCAL") val carbCalories: KCalData
)

data class IngredientDetail(
    val text: String,
    val parsed: List<IngredientParsed>
)

data class IngredientParsed(
    val quantity: Float,
    val measure: String, // Directly a string here
    val foodMatch: String,
    val food: String,
    val foodId: String,
    val weight: Float,
    val retainedWeight: Float,
    val nutrients: Map<String, NutrientDetail>
)

data class NutrientDetail(
    val label: String,
    val quantity: Float,
    val unit: String
)

data class KCalData(
    val label: String,
    val quantity: Int,
    val unit: String
)

data class TotalNutrients(
    @SerializedName("FAT") val fat: Nutrient,
    @SerializedName("FASAT") val saturatedFat: Nutrient,
    @SerializedName("FATRN") val transFat: Nutrient,
    @SerializedName("CHOLE") val cholesterol: Nutrient,
    @SerializedName("CHOCDF") val carbohydrates: Nutrient,
    @SerializedName("FIBTG") val fiber: Nutrient,
    @SerializedName("SUGAR") val sugars: Nutrient,
    @SerializedName("PROCNT") val protein: Nutrient,
    @SerializedName("NA") val sodium: Nutrient,
    @SerializedName("CA") val calcium: Nutrient,
    @SerializedName("MG") val magnesium: Nutrient,
    @SerializedName("K") val potassium: Nutrient,
    @SerializedName("FE") val iron: Nutrient,
    @SerializedName("ZN") val zinc: Nutrient,
    @SerializedName("P") val phosphorus: Nutrient,
    @SerializedName("VITA_RAE") val vitaminA: Nutrient,
    @SerializedName("VITC") val vitaminC: Nutrient,
    @SerializedName("THIA") val thiamin: Nutrient,
    @SerializedName("RIBF") val riboflavin: Nutrient,
    @SerializedName("NIA") val niacin: Nutrient,
    @SerializedName("VITB6A") val vitaminB6: Nutrient,
    @SerializedName("FOLDFE") val folate: Nutrient,
    @SerializedName("VITB12") val vitaminB12: Nutrient,
    @SerializedName("VITD") val vitaminD: Nutrient,
    @SerializedName("TOCPHA") val vitaminE: Nutrient,
    @SerializedName("VITK1") val vitaminK: Nutrient
    // Add additional nutrients as needed, following the same pattern
)

data class TotalDaily(
    @SerializedName("ENERC_KCAL") val energy: NutrientRDV?,
    @SerializedName("FAT") val fat: NutrientRDV?,
    @SerializedName("FASAT") val saturatedFat: NutrientRDV?,
    @SerializedName("FATRN") val transFat: NutrientRDV?,
    @SerializedName("CHOCDF") val carbohydrates: NutrientRDV?,
    @SerializedName("FIBTG") val fiber: NutrientRDV?,
    @SerializedName("SUGAR") val sugars: NutrientRDV?,
    @SerializedName("PROCNT") val protein: NutrientRDV?,
    @SerializedName("CHOLE") val cholesterol: NutrientRDV?,
    @SerializedName("NA") val sodium: NutrientRDV?,
    @SerializedName("CA") val calcium: NutrientRDV?,
    @SerializedName("MG") val magnesium: NutrientRDV?,
    @SerializedName("K") val potassium: NutrientRDV?,
    @SerializedName("FE") val iron: NutrientRDV?,
    @SerializedName("ZN") val zinc: NutrientRDV?,
    @SerializedName("P") val phosphorus: NutrientRDV?,
    @SerializedName("VITA_RAE") val vitaminA: NutrientRDV?,
    @SerializedName("VITC") val vitaminC: NutrientRDV?,
    @SerializedName("THIA") val thiamin: NutrientRDV?,
    @SerializedName("RIBF") val riboflavin: NutrientRDV?,
    @SerializedName("NIA") val niacin: NutrientRDV?,
    @SerializedName("VITB6A") val vitaminB6: NutrientRDV?,
    @SerializedName("FOLDFE") val folate: NutrientRDV?,
    @SerializedName("VITB12") val vitaminB12: NutrientRDV?,
    @SerializedName("VITD") val vitaminD: NutrientRDV?,
    @SerializedName("TOCPHA") val vitaminE: NutrientRDV?,
    @SerializedName("VITK1") val vitaminK: NutrientRDV?
)

data class Nutrient(
    val quantity: Float,
    val unit: String
)

data class NutrientRDV(
    val quantity: Float,
    @SerializedName("unit") val unit: String = "%" // RDV is usually represented as a percentage
)