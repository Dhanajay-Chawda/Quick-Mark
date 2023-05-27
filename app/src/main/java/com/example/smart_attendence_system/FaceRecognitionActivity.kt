package com.example.smart_attendence_system

import android.content.ContentValues.TAG
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.example.smart_attendence_system.helper.MLVideoHelperActivity
import com.example.smart_attendence_system.helper.VisionBaseProcessor
import com.example.smart_attendence_system.helper.FaceRecognitionProcessor
import com.google.firebase.firestore.FirebaseFirestore
import com.google.mlkit.vision.face.Face
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import java.io.IOException

import java.text.SimpleDateFormat
import java.util.*


class FaceRecognitionActivity : MLVideoHelperActivity(),
    FaceRecognitionProcessor.FaceRecognitionCallback {
    private var faceNetInterpreter: Interpreter? = null
    private var faceRecognitionProcessor: FaceRecognitionProcessor? = null
    private var face: com.google.mlkit.vision.face.Face? = null
    private var faceBitmap: Bitmap? = null
    private lateinit var faceVector: FloatArray

    @RequiresApi(Build.VERSION_CODES.M)
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        makeAddFaceVisible()
    }

     override fun setProcessor(): FaceRecognitionProcessor {
        try {
            faceNetInterpreter =
                Interpreter(
                    FileUtil.loadMappedFile(this, "mobile_face_net.tflite"),
                    Interpreter.Options()
                )
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val RecognitionProcessor = FaceRecognitionProcessor(
            faceNetInterpreter!!,
            graphicOverlay!!,
            this
        )
        RecognitionProcessor.activity = this
         faceRecognitionProcessor = RecognitionProcessor
        return faceRecognitionProcessor as FaceRecognitionProcessor
    }

    fun setTestImage(cropToBBox: Bitmap?) {
        if (cropToBBox == null) {
            return
        }
        runOnUiThread {
            (findViewById<ImageView>(R.id.testImageView)).setImageBitmap(
                cropToBBox
            )
        }
    }

//    override fun onFaceRecognised(face: Face?, probability: Float, name: String?) {}
override fun onFaceRecognised(face: Face?, probability: Float, name: String?) {
    // Handle face recognition and attendance marking here
    if (face != null && probability > 0.5) {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = sdf.format(Date())
        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        val firestore = FirebaseFirestore.getInstance()

        // Check if the attendance has already been marked for the current date
        val attendanceRef = firestore.collection("users")
            .document("TfLv9kxZF8TDPoSAzTYHu0HQC0v1")
            .collection("classes")
            .document("puIln15rxEQ7lksQ8DK3")
            .collection("attendance")
            .document(currentDate)
            .collection("present_student")

        attendanceRef
            .whereEqualTo("name", name)
            .whereEqualTo("date", currentDate)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    // Attendance has not been marked for the current date and student
                    // Save the attendance record to Firestore
                    val attendanceRecord = hashMapOf(
                        "name" to name,
                        "date" to currentDate,
                        "time" to currentTime
                    )

                    attendanceRef.add(attendanceRecord)
                        .addOnSuccessListener { documentReference ->
                            // Attendance record saved successfully
                            // You can perform any further actions or display a success message here
                            Log.d(TAG, "Attendance record saved successfully.")
                            Toast.makeText(this, "Attendance marked successfully.", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            // An error occurred while saving the attendance record
                            // Handle the error appropriately
                            Log.e(TAG, "Error saving attendance record: $e")
                            Toast.makeText(this, "Failed to mark attendance.", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    // Attendance has already been marked for the current date and student
                    // Display a message indicating that attendance has already been marked
                    Toast.makeText(this, "Attendance has already been marked for today.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                // An error occurred while checking attendance
                // Handle the error appropriately
                Log.e(TAG, "Error checking attendance: $e")
                Toast.makeText(this, "Failed to check attendance.", Toast.LENGTH_SHORT).show()
            }
    }
}



    override fun onFaceDetected(face: Face?, faceBitmap: Bitmap?, vect: FloatArray?) {
        this.face = face
        this.faceBitmap = faceBitmap
        this.faceVector = vect!!
    }

    override fun onAddFaceClicked(view: View?) {
        super.onAddFaceClicked(view)
        if (face == null || faceBitmap == null) {
            return
        }

        val tempFace: Face? = face
        val tempBitmap: Bitmap? = faceBitmap
        val tempVector = faceVector

        val inflater = LayoutInflater.from(this)
        val dialogView: View = inflater.inflate(R.layout.add_face_dialog, null)
        (dialogView.findViewById<View>(R.id.dlg_image) as ImageView).setImageBitmap(tempBitmap)

        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        builder.setPositiveButton("Save") { dialog, which ->
            val input = (dialogView.findViewById<View>(R.id.dlg_input) as EditText).editableText
            if (input.isNotEmpty()) {
                faceRecognitionProcessor?.registerFace(input, tempVector)
                val name = input.toString()
                val faceRecord = hashMapOf(
                    "name" to name,
                    "embedding" to tempVector.toString()
                )

                // Save the face record to Firestore
                val embeddingRef = FirebaseFirestore.getInstance().collection("users")
                    .document("TfLv9kxZF8TDPoSAzTYHu0HQC0v1")
                    .collection("classes")
                    .document("puIln15rxEQ7lksQ8DK3")
                    .collection("embedding")



                embeddingRef.add(faceRecord)
                    .addOnSuccessListener { documentReference ->
                        // Face record saved successfully
                        val faceRecordId = documentReference.id
                        Log.d(TAG, "Face record saved successfully. ID: $faceRecordId")

                        // Perform any additional actions or display a success message
                        Toast.makeText(this, "Face added successfully.", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        // An error occurred while saving the face record
                        // Handle the error appropriately
                        Log.e(TAG, "Error saving face record: $e")
                        Toast.makeText(this, "Failed to add face.", Toast.LENGTH_SHORT).show()
                    }

            }
        }

        builder.show()
    }

}