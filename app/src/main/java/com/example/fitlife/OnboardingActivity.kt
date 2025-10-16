package com.example.fitlife

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.fitlife.ui.adapters.OnboardingAdapter
import com.google.android.material.button.MaterialButton

class OnboardingActivity : AppCompatActivity() {
    
    private lateinit var viewPager: ViewPager2
    private lateinit var btnNext: MaterialButton
    private lateinit var adapter: OnboardingAdapter
    
    private var currentPage = 0
    private val totalPages = 3
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)
        
        setupViews()
        setupViewPager()
        setupClickListeners()
    }
    
    private fun setupViews() {
        viewPager = findViewById(R.id.viewPager)
        btnNext = findViewById(R.id.btnNext)
    }
    
    private fun setupViewPager() {
        adapter = OnboardingAdapter(this)
        viewPager.adapter = adapter
        
        // Handle page changes
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentPage = position
                updateButtonText()
            }
        })
    }
    
    private fun setupClickListeners() {
        btnNext.setOnClickListener {
            if (currentPage < totalPages - 1) {
                viewPager.currentItem = currentPage + 1
            } else {
                navigateToMain()
            }
        }
    }
    
    private fun updateButtonText() {
        btnNext.text = if (currentPage == totalPages - 1) {
            getString(R.string.onboarding_get_started)
        } else {
            getString(R.string.onboarding_next)
        }
    }
    
    private fun navigateToMain() {
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
    }
}
