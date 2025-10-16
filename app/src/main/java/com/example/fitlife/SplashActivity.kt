package com.example.fitlife

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.fitlife.data.Prefs
import com.example.fitlife.MainActivity
import com.example.fitlife.OnboardingActivity

class SplashActivity : AppCompatActivity() {
    
    private val splashDuration = 2500L // 2.5 seconds
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        
        // Navigate to MainActivity after splash duration
        Handler(Looper.getMainLooper()).postDelayed({
            navigateToMain()
        }, splashDuration)
    }
    
    private fun navigateToMain() {
        // Always go to loading screen first
        val intent = Intent(this, LoadingActivity::class.java)
        startActivity(intent)
        finish() // Prevent back navigation to splash
    }
    
    override fun onBackPressed() {
        // Disable back button during splash
        // Do nothing
    }
}
