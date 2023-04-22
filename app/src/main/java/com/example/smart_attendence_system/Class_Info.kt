package com.example.smart_attendence_system

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView

class Class_Info : AppCompatActivity() {


    // Declare the ImageView first.
    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_class_info)


        val ourid: String? = intent.extras?.getString("id")
        Log.d("ourid2", ourid.toString());

        val student_list = findViewById<CardView>(R.id.studentListButton)
        student_list.setOnClickListener {
            // Create the intent to open the next activity
            val intent = Intent(this, Student_List ::class.java)

            // Add the ourid string data as an extra to the intent
            intent.putExtra("ourid", ourid)

            // Start the next activity
            startActivity(intent)
        }



        val cameraon = findViewById<CardView>(R.id.contributeCard)
        cameraon.setOnClickListener {
            // Create the intent to open the next activity
            val intent = Intent(this, MainActivity2 ::class.java)

            // Start the next activity
            startActivity(intent)
        }

    }
}