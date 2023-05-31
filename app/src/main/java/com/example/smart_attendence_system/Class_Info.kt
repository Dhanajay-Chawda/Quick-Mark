package com.example.smart_attendence_system

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smart_attendence_system.Adapter.MyAdapter3
import com.example.smart_attendence_system.DataClass.User3
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class Class_Info : AppCompatActivity() {

    var info_image: ImageView? = null
    var getusermedia : ActivityResultLauncher<PickVisualMediaRequest>? = null


    private lateinit var recyclerView: RecyclerView
    private val firebaseAuth = FirebaseAuth.getInstance()
    private lateinit var userList: ArrayList<User3>
    private lateinit var db: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_class_info)

        val ourid: String? = intent.extras?.getString("id")
        //Log.d("ourid2", ourid.toString());

        //Hide the actionBar
        supportActionBar?.hide()


        val student_list = findViewById<CardView>(R.id.studentListButton)
        student_list.setOnClickListener {
            // Create the intent to open the next activity
            val intent = Intent(this, Student_List::class.java)

            // Add the ourid string data as an extra to the intent
            intent.putExtra("ourid", ourid)

            // Start the next activity
            startActivity(intent)
        }

        val magicbutton = findViewById<CardView>(R.id.btnTakePicture)
        magicbutton.setOnClickListener {
            val intent = Intent(this, FaceRecognitionActivity::class.java)
            intent.putExtra("ourid2", ourid)
            startActivity(intent)
        }


        recyclerView = findViewById(R.id.RecyclerView3)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val user = firebaseAuth.currentUser
        val userId = user?.uid
        db = FirebaseFirestore.getInstance()
        userList = arrayListOf()
//        Log.d("mydebug3","${userId} ${ourid}")
        if (userId != null) {
            db.collection("users")
                .document(userId)
                .collection("classes")
                .document(ourid.toString())
                .collection("attendance")
                .get()
                .addOnSuccessListener {documents->
                    Log.d("mydebug3","success!!${documents.documents}:${documents.size()}")

                    if (!documents.isEmpty) {
                        Log.d("mydebug3",documents.toString());
                        for (data in documents) {
//                            Log.d("mydebug",data.id.toString())
                            var user: User3? = data.toObject(User3::class.java)
                            user?.presentid = data.id.toString()
                            if (user != null) {
                                userList.add(user)
                            }
                        }
                        //Log.d("main activity userlist",userList[0].s.toString());
                        recyclerView.adapter = MyAdapter3(userList, ourid)

                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
                }
        }


    }
}

