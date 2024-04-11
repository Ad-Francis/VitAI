package com.example.vitai

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.vitai.adapters.ScreenSlidePagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var enterAppButton: View // This is the button to enter the app from the main menu
    private lateinit var mainMenuBackgroundView: View // Reference to the main menu background view

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Views
        viewPager = findViewById(R.id.pager)
        tabLayout = findViewById(R.id.tab_layout)
        enterAppButton = findViewById(R.id.enterAppButton) // Initialize the enterAppButton
        mainMenuBackgroundView = findViewById(R.id.mainMenuBackgroundView) // Initialize the main menu background view

        val pagerAdapter = ScreenSlidePagerAdapter(this)
        viewPager.adapter = pagerAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Nutrition\nFacts"
                1 -> "Food\nDatabase"
                2 -> "Recipe\nSearch"
                else -> null
            }
        }.attach()

        setupMainMenu()
    }

    private fun setupMainMenu() {
        // Initially, hide the ViewPager and show the main menu
        viewPager.visibility = View.GONE
        tabLayout.visibility = View.GONE

        // When the 'enter app' button is clicked, switch views
        enterAppButton.setOnClickListener {
            // Hide the main menu elements
            mainMenuBackgroundView.visibility = View.GONE // Hide the main menu background
            enterAppButton.visibility = View.GONE // Hide the enter button

            // Show the ViewPager and TabLayout for the app content
            viewPager.visibility = View.VISIBLE
            tabLayout.visibility = View.VISIBLE
        }
    }
}