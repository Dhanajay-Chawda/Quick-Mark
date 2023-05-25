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
import android.widget.Button
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
import com.example.smart_attendence_system.databinding.ActivityAddStudentBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File

class Add_Student : AppCompatActivity() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private lateinit var binding: ActivityAddStudentBinding

    var info_image2: ImageView? = null
    var getusermedia : ActivityResultLauncher<PickVisualMediaRequest>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_student)

        supportActionBar?.hide()

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


                            // Create an intent to go back to the Student_List activity
                            val intent = Intent(this, Student_List::class.java)
                            intent.putExtra("ourid", ourid) // Pass any relevant data to reload the Student_List activity

                            // Start the Student_List activity and finish the current activity
                            startActivity(intent)
                            finish()


                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to save data", Toast.LENGTH_SHORT).show()
                        }
                }
            } else {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
            }
        }


        info_image2 = findViewById(R.id.imageView2)
        val cameraon2 = findViewById<Button>(R.id.btnTakePicture2)
        cameraon2.setOnClickListener {
            if (Class_Info.checkAndRequestPermissions(this@Add_Student)) {
                chooseImage(this@Add_Student)
            }
        }

        getusermedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            if (uri != null) {
                info_image2?.setImageURI(uri)
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }


    }


    // function to let's the user to choose image from camera or gallery
    private fun chooseImage(context: Context) {
        val optionsMenu = arrayOf<CharSequence>(
            "Take Photo",
            "Choose from Gallery",
            "Exit"
        ) // create a menuOption Array
        // create a dialog for showing the optionsMenu
        val builder = AlertDialog.Builder(context)
        // set the items in builder
        builder.setItems(
            optionsMenu
        ) { dialogInterface, i ->
            if (optionsMenu[i] == "Take Photo") {
                // Open the camera and get the photo
                val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(takePicture, 0)
            } else if (optionsMenu[i] == "Choose from Gallery") {
                // choose from  external storage
                getusermedia?.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

            } else if (optionsMenu[i] == "Exit") {
                dialogInterface.dismiss()
            }
        }
        builder.show()
    }


    // function to check permission
    companion object {
        val REQUEST_ID_MULTIPLE_PERMISSIONS = 101

        fun checkAndRequestPermissions(context: Context): Boolean {
            val WExtstorePermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            val cameraPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            )
            val listPermissionsNeeded: MutableList<String> = ArrayList()
            if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.CAMERA)
            }
            if (listPermissionsNeeded.isNotEmpty()) {
                Log.e("mymessage", "checkAndRequestPermissions: working!!:"+listPermissionsNeeded.toTypedArray().get(0), )
                ActivityCompat.requestPermissions(
                    context as Activity,
                    listPermissionsNeeded.toTypedArray(),
                    REQUEST_ID_MULTIPLE_PERMISSIONS
                )
                return false
            }
            return true
        }
    }




    // Handled permission Result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            Class_Info.REQUEST_ID_MULTIPLE_PERMISSIONS -> {
                if (ContextCompat.checkSelfPermission(
                        this@Add_Student,
                        Manifest.permission.CAMERA
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    Toast.makeText(
                        applicationContext,
                        "FlagUp Requires Access to Camera.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else {
                    chooseImage(this@Add_Student)
                }
            }
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_CANCELED) {
            when (requestCode) {
                0 -> if (resultCode == RESULT_OK && data != null) {
                    val selectedImage = data.extras!!["data"] as Bitmap?
                    info_image2!!.setImageBitmap(selectedImage)
                }
                1 -> if (resultCode == RESULT_OK && data != null) {
                    val selectedImage = data.data
                    val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                    if (selectedImage != null) {
                        val cursor =
                            contentResolver.query(selectedImage, filePathColumn, null, null, null)
                        if (cursor != null) {
                            cursor.moveToFirst()
                            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                            val picturePath = cursor.getString(columnIndex)
                            info_image2!!.setImageBitmap(BitmapFactory.decodeFile(picturePath))
                            cursor.close()
                        }
                    }
                }
            }
        }
    }




}