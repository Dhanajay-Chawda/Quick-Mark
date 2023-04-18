package com.example.smart_attendence_system

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView

class Class_Info : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_class_info)


        val ourid: String? = intent.extras?.getString("id")
        Log.d("ourid2", ourid.toString());

        val student_list = findViewById<Button>(R.id.studentListButton)
        student_list.setOnClickListener {
            // Create the intent to open the next activity
            val intent = Intent(this, Student_List ::class.java)

            // Add the ourid string data as an extra to the intent
            intent.putExtra("ourid", ourid)

            // Start the next activity
            startActivity(intent)
        }


    }
}