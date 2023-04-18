package com.example.smart_attendence_system

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smart_attendence_system.databinding.ActivityAddStudentBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Add_Student : AppCompatActivity() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private lateinit var binding: ActivityAddStudentBinding





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_student)

        binding = ActivityAddStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val ourid: String? = intent.extras?.getString("ourid")
       Log.d("ouridLast", ourid.toString());

        binding.saveStudentButton.setOnClickListener{


            val studentNmae = binding.takeStudentName.text.toString().trim()
            val rollNo = binding.takeRollNo.text.toString().trim()

            if (studentNmae.isNotEmpty() && rollNo.isNotEmpty()) {
                val user = firebaseAuth.currentUser



                if (user != null) {
                    val userId = user.uid


                    val studentData = hashMapOf(
                        "Name" to studentNmae,
                        "ID" to rollNo,

                    )

                    db.collection("users")
                        .document(userId)
                        .collection("classes")
                        .document(ourid.toString())
                        .collection("students")
                        .add(studentData)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Data saved successfully", Toast.LENGTH_SHORT).show()
                            binding.takeStudentName.setText("")
                            binding.takeRollNo.setText("")
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to save data", Toast.LENGTH_SHORT).show()
                        }
                }
            } else {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
            }
        }




    }
}