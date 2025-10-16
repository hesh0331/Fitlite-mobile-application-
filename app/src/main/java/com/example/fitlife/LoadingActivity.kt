//Shown during data loading or initialization
package com.example.fitlife

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.fitlife.databinding.ActivityLoadingBinding

class LoadingActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityLoadingBinding
    private val loadingDuration = 2000L // 2 seconds
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoadingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Start loading animation
        startLoading()
    }
    
    private fun startLoading() {
        // Simulate loading process
        Handler(Looper.getMainLooper()).postDelayed({
            navigateToMain()
        }, loadingDuration)
    }
    
    private fun navigateToMain() {
        // Always go to onboarding after loading
        val intent = Intent(this, OnboardingActivity::class.java)
        startActivity(intent)
        finish() // Prevent back navigation to loading
    }
    
    override fun onBackPressed() {
        // Disable back button during loading
        // Do nothing
    }
}


