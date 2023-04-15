package com.example.smart_attendence_system

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class Student_List : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_list)

        val add_student = findViewById<Button>(R.id.addStudent)
        add_student.setOnClickListener {
            // Create the intent to open the next activity
            val intent = Intent(this, Add_Student ::class.java)

            // Start the next activity
            startActivity(intent)
        }


    }
}