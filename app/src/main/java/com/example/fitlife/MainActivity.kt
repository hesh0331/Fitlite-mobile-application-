//Main dashboard
package com.example.fitlife

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.fitlife.ui.HomeFragment
import com.example.fitlife.ui.CalendarFragment
import com.example.fitlife.ui.HabitsFragment
import com.example.fitlife.ui.MoodFragment
import com.example.fitlife.ui.MoodSelectionFragment
import com.example.fitlife.ui.SettingsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    
    private lateinit var bottomNavigationView: BottomNavigationView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Only apply top padding, not bottom to keep bottom nav at edge
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
        
        setupBottomNavigation()
        
        // Set default fragment
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, HomeFragment())
                .commit()
        }
    }
    
    private fun setupBottomNavigation() {
        bottomNavigationView = findViewById(R.id.bottom_nav)
        bottomNavigationView.setOnItemSelectedListener { item ->
            try {
                val fragment = when (item.itemId) {
                    R.id.nav_home -> HomeFragment()
                    R.id.nav_calendar -> CalendarFragment()
                    R.id.nav_mood -> MoodSelectionFragment()
                    R.id.nav_settings -> SettingsFragment()
                    else -> null
                }
                
                fragment?.let {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, it)
                        .commit()
                    true
                } ?: false
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle error gracefully - stay on current fragment
                false
            }
        }
    }
    
}