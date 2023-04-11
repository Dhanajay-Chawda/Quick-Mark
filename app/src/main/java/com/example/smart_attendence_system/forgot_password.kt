package com.example.smart_attendence_system

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.example.smart_attendence_system.R
import com.example.smart_attendence_system.login_page
import com.google.firebase.auth.FirebaseAuth

class forgot_password : AppCompatActivity() {

    //Declaration
    private lateinit var btnReset: Button
    private lateinit var btnBack: Button
    private lateinit var edtEmail: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var mAuth: FirebaseAuth
    private var strEmail: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {

        //Hide the actionBar
        supportActionBar?.hide()


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        //Initialization
        btnBack = findViewById(R.id.btnForgotPasswordBack)
        btnReset = findViewById(R.id.btnReset)
        edtEmail = findViewById(R.id.edtForgotPasswordEmail)
        progressBar = findViewById(R.id.forgetPasswordProgressbar)


        mAuth = FirebaseAuth.getInstance()

        //Reset Button Listener
        btnReset.setOnClickListener {
            strEmail = edtEmail.text.toString().trim()
            if (!TextUtils.isEmpty(strEmail)) {
                resetPassword()
            } else {
                edtEmail.error = "Email field can't be empty"
            }
        }

        //Back Button Code
        btnBack.setOnClickListener { onBackPressed() }
    }


    private fun resetPassword() {
        progressBar.visibility = View.VISIBLE
        btnReset.visibility = View.INVISIBLE

        mAuth.sendPasswordResetEmail(strEmail)
            .addOnSuccessListener {
                Toast.makeText(
                    this@forgot_password,
                    "Reset Password link has been sent to your registered Email",
                    Toast.LENGTH_SHORT
                ).show()
                val intent = Intent(this@forgot_password, login_page::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this@forgot_password,
                    "Error :- ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
                progressBar.visibility = View.INVISIBLE
                btnReset.visibility = View.VISIBLE
            }
    }
}
