package com.example.smart_attendence_system

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smart_attendence_system.Adapter.MyAdapter2
import com.example.smart_attendence_system.DataClass.User2
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Student_List : AppCompatActivity() {


    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var recyclerView: RecyclerView
    private lateinit var userList: ArrayList<User2>


    private val firebaseAuth = FirebaseAuth.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_list)

        val ourid = intent?.getStringExtra("ourid")
        Log.d("NEWstring", ourid.toString())


        val add_student = findViewById<Button>(R.id.addStudent)
        add_student.setOnClickListener {
            // Create the intent to open the next activity
            val intent = Intent(this, FaceRecognitionActivity ::class.java)

            intent.putExtra("ourid2", ourid)

            // Start the next activity
            startActivity(intent)
        }



        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()


        recyclerView = findViewById(R.id.RecyclerView2)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val user = firebaseAuth.currentUser
        val userId = user?.uid
        userList = arrayListOf()
        if (userId != null) {
            db.collection("users")
                .document(userId)
                .collection("classes")
                .document(ourid.toString())
                .collection("embedding")
                .get()
                .addOnSuccessListener {

                    if (!it.isEmpty){
//                        Log.d("main activity userlist",it.documents.toString());
                        for(data in it.documents){

                            var user: User2? = data.toObject(User2::class.java)
                            
                            if (user!= null) {
                                userList.add(user)
                            }
                        }
                        //Log.d("main activity userlist",userList[0].s.toString());
                        recyclerView.adapter = MyAdapter2(userList)

                    }
                }
                .addOnFailureListener{
                    Toast.makeText(this,it.toString(), Toast.LENGTH_SHORT).show()
                }
        }


    }
}