package com.example.smart_attendence_system

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.smart_attendence_system.forgot_password
import com.example.smart_attendence_system.databinding.ActivityLoginPageBinding
import com.google.firebase.auth.FirebaseAuth

class login_page : AppCompatActivity() {

    private lateinit var binding: ActivityLoginPageBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var backPressedTime: Long = 0
    private val backPressedInterval: Long = 2000 // Time interval for double press in milliseconds



    override fun onCreate(savedInstanceState: Bundle?) {


        //Hide the actionBar
        supportActionBar?.hide()


        super.onCreate(savedInstanceState)
        binding = ActivityLoginPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()


        binding.textView.setOnClickListener {
            val intent = Intent(this, login_page::class.java)
            startActivity(intent)
        }


        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {

                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {

                    if (it.isSuccessful) {

                        val verification = firebaseAuth.currentUser?.isEmailVerified
                        if (verification == true) {

                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, "Please verify your email", Toast.LENGTH_SHORT)
                                .show()
                        }


                    } else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()

                    }
                }
            } else {
                Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()

            }
        }

        val myTextView = findViewById<TextView>(R.id.textView2)
        myTextView.setOnClickListener {
            // Create the intent to open the next activity
            val intent = Intent(this, forgot_password ::class.java)

            // Start the next activity
            startActivity(intent)
        }

        val signup = findViewById<TextView>(R.id.textView)
        signup.setOnClickListener {
            // Create the intent to open the next activity
            val intent = Intent(this, singup_page ::class.java)

            // Start the next activity
            startActivity(intent)
        }

    }



    override fun onStart() {
        super.onStart()

        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            val isEmailVerified = currentUser.isEmailVerified
            if (isEmailVerified) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Please verify your email", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onBackPressed() {
        if (backPressedTime + backPressedInterval > System.currentTimeMillis()) {
            super.onBackPressed()
            finishAffinity() // Close the app completely
        } else {
            Toast.makeText(
                this,
                "Press back again to exit",
                Toast.LENGTH_SHORT
            ).show()
        }
        backPressedTime = System.currentTimeMillis()
    }
    }