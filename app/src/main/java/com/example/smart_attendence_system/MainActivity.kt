package com.example.smart_attendence_system

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {

        //Hide the actionBar
        supportActionBar?.hide()


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        val logout = findViewById<FloatingActionButton>(R.id.logout)
        logout.setOnClickListener {

            auth.signOut()

            // Create the intent to open the next activity
            val intent = Intent(this,login_page::class.java)

            // Start the next activity
            startActivity(intent)
            Toast.makeText(this,"Logout  successful", Toast.LENGTH_SHORT).show()



        }

    }
}