package com.example.smart_attendence_system

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.example.smart_attendence_system.databinding.ActivitySingupPageBinding
import com.google.firebase.auth.FirebaseAuth

class singup_page : AppCompatActivity() {

    private lateinit var binding: ActivitySingupPageBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {

        //Hide the actionBar
        supportActionBar?.hide()


        super.onCreate(savedInstanceState)

        binding = ActivitySingupPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()


        binding.button.setOnClickListener {

            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()
            val confirmPass = binding.confirmPassEt.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()) {

                if (pass == confirmPass) {

                    firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {

                        if (it.isSuccessful) {

                            firebaseAuth.currentUser?.sendEmailVerification()
                                ?.addOnSuccessListener {
                                    Toast.makeText(this, "Please Verify your email", Toast.LENGTH_SHORT).show()
                                }

                                ?.addOnFailureListener{
                                    Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
                                }

//                            val intent = Intent(this, SigninActivity::class.java)
//                            startActivity(intent)

                        }else{
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }

                    }

                }else{
                    Toast.makeText(this, "Password is not matching", Toast.LENGTH_SHORT).show()
                }

            }else{
                Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()
            }

        }


        val signin = findViewById<TextView>(R.id.textView)
        signin.setOnClickListener {
            // Create the intent to open the next activity
            val intent = Intent(this,login_page::class.java)

            // Start the next activity
            startActivity(intent)
        }

    }
}

