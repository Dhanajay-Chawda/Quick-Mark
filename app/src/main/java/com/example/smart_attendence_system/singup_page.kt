package com.example.smart_attendence_system

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.example.smart_attendence_system.databinding.ActivitySingupPageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class singup_page : AppCompatActivity() {

    private lateinit var binding: ActivitySingupPageBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {

        //Hide the actionBar
        supportActionBar?.hide()


        super.onCreate(savedInstanceState)

        binding = ActivitySingupPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()


        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()
            val confirmPass = binding.confirmPassEt.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()) {
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (pass == confirmPass) {
                    firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            firebaseAuth.currentUser?.sendEmailVerification()?.addOnCompleteListener { verificationTask ->
                                if (verificationTask.isSuccessful) {
                                    Toast.makeText(this, "Please verify your email", Toast.LENGTH_SHORT).show()

                                    val userId = firebaseAuth.currentUser?.uid
                                    val userRef = db.collection("users").document(userId!!)
                                    userRef.set(mapOf("email" to email)).addOnCompleteListener { emailSaveTask ->
                                        if (emailSaveTask.isSuccessful) {
                                            Toast.makeText(this, "com.example.smart_attendence_system.DataClass.User email saved successfully", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(this, "Error saving user email: ${emailSaveTask.exception?.message}", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                } else {
                                    Toast.makeText(this, "Email verification failed: ${verificationTask.exception?.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Toast.makeText(this, "com.example.smart_attendence_system.DataClass.User creation failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Password is not matching", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()
            }
        }



        val signin = findViewById<TextView>(R.id.textView69)
        signin.setOnClickListener {
            // Create the intent to open the next activity
            val intent = Intent(this,login_page::class.java)

            // Start the next activity
            startActivity(intent)
        }

    }
}

