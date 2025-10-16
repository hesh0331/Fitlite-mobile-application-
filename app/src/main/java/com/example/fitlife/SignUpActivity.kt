package com.example.fitlife

import android.os.Bundle
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val btnCreate = findViewById<com.google.android.material.button.MaterialButton>(R.id.btnCreateAccount)
        btnCreate.setOnClickListener {
            Toast.makeText(this, getString(R.string.auth_signup_success), Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}


