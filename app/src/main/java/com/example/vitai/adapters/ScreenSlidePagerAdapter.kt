package com.example.vitai.adapters
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.vitai.fragments.*

class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> NutritionFactsFragment()
            1 -> FoodDatabaseFragment()
            2 -> RecipeSearchFragment()
            else -> Fragment()
        }
    }
}