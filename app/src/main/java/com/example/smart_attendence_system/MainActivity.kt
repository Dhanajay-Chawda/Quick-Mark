package com.example.smart_attendence_system

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.smart_attendence_system.DataClass.User
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.app.AlertDialog
import com.example.smart_attendence_system.Adapter.MyAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var userEmailTextView: TextView

    private lateinit var recyclerView: RecyclerView
    private lateinit var userList: ArrayList<User>

    private val firebaseAuth = FirebaseAuth.getInstance()

    private var backPressedTime: Long = 0
    private val backPressedInterval: Long = 2000 // Time interval for double press in milliseconds

    private lateinit var logoutDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {

        //Hide the actionBar
        supportActionBar?.hide()


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        userEmailTextView = findViewById(R.id.user_email_textview)

        // Retrieve user's email from Firestore and display it in the TextView
        val currentUser = auth.currentUser
        if (currentUser != null) {
            db.collection("users").document(currentUser.uid).get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val userEmail = documentSnapshot.getString("email")
                        userEmailTextView.text = userEmail
                    }
                }
        }


        val logout = findViewById<ImageButton>(R.id.logout)
        logout.setOnClickListener {
            showLogoutDialog()
        }

        val creat = findViewById<Button>(R.id.Creat_Class)
        creat.setOnClickListener {
            // Create the intent to open the next activity
            val intent = Intent(this, Creat_Class::class.java)

            // Start the next activity
            startActivity(intent)
        }

        recyclerView = findViewById(R.id.RecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val user = firebaseAuth.currentUser
        val userId = user?.uid
        userList = arrayListOf()
        if (userId != null) {
            db.collection("users")
                .document(userId)
                .collection("classes")
                .get()
                .addOnSuccessListener {

                    if (!it.isEmpty) {
                        for (data in it.documents) {
                            Log.d("main activity userlist", data.id.toString())
                            var usr: User? = data.toObject(User::class.java)
                            usr?.classid = data.id.toString()
                            if (usr != null) {
                                userList.add(usr)
                            }
                        }
                        recyclerView.adapter = MyAdapter(userList)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun showLogoutDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Logout")
        builder.setMessage("Are you sure you want to logout?")

        builder.setPositiveButton("Yes") { _, _ ->
            logout()
        }

        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }

        logoutDialog = builder.create()
        logoutDialog.show()
    }

    private fun logout() {
        auth.signOut()

        // Create the intent to open the next activity
        val intent = Intent(this, login_page::class.java)

        // Start the next activity
        startActivity(intent)
        Toast.makeText(this, "Logout successful", Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        if (backPressedTime + backPressedInterval > System.currentTimeMillis()) {
            super.onBackPressed()
            finishAffinity() // Close the app completely
        } else {
            Toast.makeText(
                this,
                "Press back again to exit",
                Toast.LENGTH_SHORT
            ).show()
        }
        backPressedTime = System.currentTimeMillis()
    }

    override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser
        if (currentUser != null) {
            val isEmailVerified = currentUser.isEmailVerified
            if (!isEmailVerified) {
                auth.signOut()
                val intent = Intent(this, login_page::class.java)
                startActivity(intent)
                Toast.makeText(this, "Please verify your email", Toast.LENGTH_SHORT).show()
            }
        } else {
            val intent = Intent(this, login_page::class.java)
            startActivity(intent)
        }
    }
}
