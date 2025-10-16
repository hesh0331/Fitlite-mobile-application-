package com.example.fitlife

//Manages login/logout, verifying credentials, and connecting with Firebase/Auth API.

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fitlife.data.Prefs
import com.example.fitlife.databinding.ActivityAuthBinding

class AuthActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityAuthBinding
    private lateinit var prefs: Prefs
    
    private var isSignUpMode = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        prefs = Prefs(this)
        // DEBUG: Force-set test credentials for easy login during development
        prefs.saveUsernameCredentials("test_user", "123456")
        setupClickListeners()
        updateUI()
    }
    
    /**
     Wire up click listeners for switching auth mode and submitting the form.
     */
    private fun setupClickListeners() {
        binding.btnToggleMode.setOnClickListener {
            // Navigate to dedicated SignUpActivity
            startActivity(Intent(this, SignUpActivity::class.java))
        }
        
        binding.btnSubmit.setOnClickListener {
            if (isSignUpMode) {
                handleSignUp()
            } else {
                handleSignIn()
            }
        }
    }
    
    /**
      Set initial screen copy and visibility for sign-in mode.
     */
    private fun updateUI() {
        binding.title.text = getString(R.string.auth_signin_title)
        binding.btnSubmit.text = getString(R.string.auth_signin)
        binding.btnToggleMode.text = getString(R.string.auth_switch_to_signup)
        binding.tilConfirmPassword.visibility = android.view.View.GONE
    }
    
    /**
      Handle sign-up: reads username/email + password and persists them
     via Prefs, then marks the user as logged in.
     */
    private fun handleSignUp() {
        val email = binding.editEmail.text.toString().trim()
        val password = binding.editPassword.text.toString().trim()
        val confirm = binding.editConfirmPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }
        if (password != confirm) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Save user credentials without validation
        prefs.saveUserCredentials(email, password)
        prefs.setUserLoggedIn(true)
        
        Toast.makeText(this, getString(R.string.auth_signup_success), Toast.LENGTH_SHORT).show()
        navigateToLoading()
    }
    
    /**
     Handle sign-in: validates the provided identifier (username/email)
     and password against values persisted in Prefs. If valid, sets
     session state and navigates into the app; otherwise shows an error.
     */
    private fun handleSignIn() {
        val email = binding.editEmail.text.toString().trim()
        val password = binding.editPassword.text.toString().trim()
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter username/email and password", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Remember me persists login flag
        val remember = binding.cbRemember.isChecked
        val valid = prefs.validateUserCredentials(email, password)
        if (valid) {
            prefs.setUserLoggedIn(remember)
            Toast.makeText(this, getString(R.string.auth_signin_success), Toast.LENGTH_SHORT).show()
            navigateToLoading()
        } else {
            Toast.makeText(this, "Invalid username/email or password", Toast.LENGTH_SHORT).show()
        }
    }
    
    
    /**
     * Navigate into the app after successful authentication.
     */
    private fun navigateToLoading() {
        // Go directly to MainActivity after authentication
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}


