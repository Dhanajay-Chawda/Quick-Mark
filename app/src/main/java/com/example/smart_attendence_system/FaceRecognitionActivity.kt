package com.example.smart_attendence_system


import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.example.smart_attendence_system.helper.MLVideoHelperActivity
import com.example.smart_attendence_system.helper.VisionBaseProcessor
import com.example.smart_attendence_system.helper.FaceRecognitionProcessor
import com.google.mlkit.vision.face.Face
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import java.io.IOException


class FaceRecognitionActivity : MLVideoHelperActivity(),
    FaceRecognitionProcessor.FaceRecognitionCallback {
    private var faceNetInterpreter: Interpreter? = null
    private var faceRecognitionProcessor: FaceRecognitionProcessor? = null
    private var face: Face? = null
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

    override fun onFaceRecognised(face: Face?, probability: Float, name: String?) {}
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
        builder.setPositiveButton(
            "Save"
        ) { dialog, which ->
            val input =
                (dialogView.findViewById<View>(R.id.dlg_input) as EditText).editableText
            if (input.length > 0) {
                faceRecognitionProcessor?.registerFace(input, tempVector)
            }
        }
        builder.show()
    }
}