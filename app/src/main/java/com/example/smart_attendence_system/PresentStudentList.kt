package com.example.smart_attendence_system

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smart_attendence_system.Adapter.MyAdapter3
import com.example.smart_attendence_system.Adapter.MyAdapter4
import com.example.smart_attendence_system.DataClass.User3
import com.example.smart_attendence_system.DataClass.User4
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PresentStudentList : AppCompatActivity() {


    private lateinit var recyclerView: RecyclerView
    private val firebaseAuth = FirebaseAuth.getInstance()
    private lateinit var userList: ArrayList<User4>
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_present_student_list)



        val dateid: String? = intent.extras?.getString("id69")
        val ourid: String? = intent.extras?.getString("ourid")

        recyclerView = findViewById(R.id.RecyclerView4)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val user = firebaseAuth.currentUser
        val userId = user?.uid
        db = FirebaseFirestore.getInstance()
        userList = arrayListOf()
        if (userId != null) {
            db.collection("users")
                .document(userId)
                .collection("classes")
                .document(ourid.toString())
                .collection("attendance")
                .document(dateid.toString())
                .collection("present_student")
                .get()
                .addOnSuccessListener { documents->
                    if (!documents.isEmpty){
//                        Log.d("mydebug3",documents.size().toString());
                        for(data in documents){
//                            Log.d("mydebug2",data.id)
                            var user: User4? = data.toObject(User4::class.java)
//                            user?.name = data.id.toString()
                            if (user!= null) {
                                userList.add(user)
                            }
                        }
                        //Log.d("main activity userlist",userList[0].s.toString());
                        recyclerView.adapter = MyAdapter4(userList)

                    }
                }
                .addOnFailureListener{
                    Toast.makeText(this,it.toString(), Toast.LENGTH_SHORT).show()
                }
        }


    }
}