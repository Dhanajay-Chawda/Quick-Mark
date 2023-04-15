package com.example.smart_attendence_system

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.smart_attendence_system.databinding.ActivityCreatClassBinding
import com.example.smart_attendence_system.databinding.ActivityLoginPageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Creat_Class : AppCompatActivity() {

    private lateinit var binding: ActivityCreatClassBinding

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_creat_class)


        binding = ActivityCreatClassBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button2.setOnClickListener{


            val className = binding.ClaasName.text.toString().trim()
            val section = binding.editTextTextPersonName2.text.toString().trim()
            val subject = binding.editTextTextPersonName3.text.toString().trim()

            if (className.isNotEmpty() && section.isNotEmpty() && subject.isNotEmpty()) {
                val user = firebaseAuth.currentUser
                if (user != null) {
                    val userId = user.uid

                    val classData = hashMapOf(
                        "class_name" to className,
                        "section" to section,
                        "subject" to subject
                    )

                    db.collection("users")
                        .document(userId)
                        .collection("classes")
                        .add(classData)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Data saved successfully", Toast.LENGTH_SHORT).show()
                            binding.ClaasName.setText("")
                            binding.editTextTextPersonName2.setText("")
                            binding.editTextTextPersonName3.setText("")
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


