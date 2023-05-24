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


class Class_Info : AppCompatActivity() {

    var info_image: ImageView? = null
    var getusermedia : ActivityResultLauncher<PickVisualMediaRequest>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_class_info)

        val ourid: String? = intent.extras?.getString("id")
        //Log.d("ourid2", ourid.toString());

        val student_list = findViewById<CardView>(R.id.studentListButton)
        student_list.setOnClickListener {
            // Create the intent to open the next activity
            val intent = Intent(this, Student_List ::class.java)

            // Add the ourid string data as an extra to the intent
            intent.putExtra("ourid", ourid)

            // Start the next activity
            startActivity(intent)
        }

        info_image = findViewById(R.id.ivUser)

        val cameraon = findViewById<CardView>(R.id.btnTakePicture)
        cameraon.setOnClickListener {
            // Create the intent to open the next activity
//            val intent = Intent(this, MainActivity2 ::class.java)
            if (Class_Info.checkAndRequestPermissions(this@Class_Info)) {
                chooseImage(this@Class_Info)
            }

            // Start the next activity
//            startActivity(intent)
        }

        getusermedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            if (uri != null) {
                info_image?.setImageURI(uri)
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }



        // Initialize the imageView (Link the imageView with front-end component ImageView)
//        imageView = findViewById(R.id.ivUser)
//        if (Class_Info.checkAndRequestPermissions(this@Class_Info)) {
//            chooseImage(this@Class_Info)
//        }


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
//                val pickPhoto = Intent(
//                    Intent.ACTION_PICK,
//                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//                )
//                startActivityForResult(pickPhoto, 1)

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
//            if (WExtstorePermission != PackageManager.PERMISSION_GRANTED) {
//                listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
//            }
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
                        this@Class_Info,
                        Manifest.permission.CAMERA
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    Toast.makeText(
                        applicationContext,
                        "FlagUp Requires Access to Camera.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
//                else if(ContextCompat.checkSelfPermission(this@Class_Info,Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
//                    Toast.makeText(
//                        applicationContext,
//                        "FlagUp Requires Access to Storage.",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
                else {
                    chooseImage(this@Class_Info)
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
                    info_image!!.setImageBitmap(selectedImage)
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
                            info_image!!.setImageBitmap(BitmapFactory.decodeFile(picturePath))
                            cursor.close()
                        }
                    }
                }
            }
        }
    }


}

