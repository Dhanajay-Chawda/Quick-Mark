package com.example.smart_attendence_system.helper


import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Rect
import android.text.Editable
import android.util.Log
import android.util.Pair
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import com.example.smart_attendence_system.FaceRecognitionActivity
import com.example.smart_attendence_system.DataClass.Person
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import java.nio.ByteBuffer


class FaceRecognitionProcessor(
    faceNetModelInterpreter: Interpreter,
    private val graphicOverlay: GraphicOverlay,
    private val callback: FaceRecognitionCallback?,
    facefromdb: MutableList<Person?>
) :
    VisionBaseProcessor<List<Face>>() {
    //inner class Person(var name: String, var faceVector: FloatArray)
    interface FaceRecognitionCallback {
        fun onFaceRecognised(face: Face?, probability: Float, name: String?)
        fun onFaceDetected(face: Face?, faceBitmap: Bitmap?, vect: FloatArray?)
    }

    private val detector: FaceDetector
    private val faceNetModelInterpreter: Interpreter
    private val faceNetImageProcessor: ImageProcessor
    var activity: FaceRecognitionActivity? = null
    var recognisedFaceList: MutableList<Person?> = ArrayList()

    init {
        if(recognisedFaceList.isEmpty()){
            recognisedFaceList = facefromdb
            Log.d("mytag", "Working!!!!!! -- ${facefromdb.size}")
        }
//        for(tmp in recognisedFaceList) {
//            Log.d("mytag", "output array: ${tmp!!.name}")
//        }
        // initialize processors
        this.faceNetModelInterpreter = faceNetModelInterpreter
        faceNetImageProcessor = ImageProcessor.Builder()
            .add(
                ResizeOp(
                    FACENET_INPUT_IMAGE_SIZE,
                    FACENET_INPUT_IMAGE_SIZE,
                    ResizeOp.ResizeMethod.BILINEAR
                )
            )
            .add(NormalizeOp(0f, 255f))
            .build()
        val faceDetectorOptions = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE) // to ensure we don't count and analyse same person again
            .enableTracking()
            .build()
        detector = FaceDetection.getClient(faceDetectorOptions)
    }

    @OptIn(markerClass = arrayOf(ExperimentalGetImage::class))
    override fun detectInImage(
        imageProxy: ImageProxy?,
        bitmap: Bitmap?,
        rotationDegrees: Int
    ): Task<List<Face>>? {
        val inputImage = InputImage.fromMediaImage(imageProxy!!.image!!, rotationDegrees)

        // In order to correctly display the face bounds, the orientation of the analyzed
        // image and that of the viewfinder have to match. Which is why the dimensions of
        // the analyzed image are reversed if its rotation information is 90 or 270.
        val reverseDimens = rotationDegrees == 90 || rotationDegrees == 270
        val width: Int
        val height: Int
        if (reverseDimens) {
            width = imageProxy.height
            height = imageProxy.width
        } else {
            width = imageProxy.width
            height = imageProxy.height
        }
        return detector.process(inputImage)
            .addOnSuccessListener(OnSuccessListener<List<Face>> { faces ->
                graphicOverlay.clear()
                for (face in faces) {
                    val faceGraphic = FaceGraphic(graphicOverlay, face, false, width, height)
                    Log.d("mytag", "face found, id: " + face.trackingId)
                    //                            if (activity != null) {
                    //                                activity.setTestImage(cropToBBox(bitmap, face.getBoundingBox(), rotation));
                    //                            }
                    // now we have a face, so we can use that to analyse age and gender
                    val faceBitmap = cropToBBox(bitmap, face.boundingBox, rotationDegrees)
                    if (faceBitmap == null) {
                        Log.d("GraphicOverlay", "Face bitmap null")
                        return@OnSuccessListener
                    }
                    val tensorImage: TensorImage = TensorImage.fromBitmap(faceBitmap)
                    val faceNetByteBuffer: ByteBuffer =
                        faceNetImageProcessor.process(tensorImage).getBuffer()
                    val faceOutputArray = Array(1) {
                        FloatArray(
                            192
                        )
                    }
                    faceNetModelInterpreter.run(faceNetByteBuffer, faceOutputArray)
//                    var tmp:String = "";
//                    for(i in faceOutputArray[0]){
//                        tmp.plus(i.toString());
//                    }

                    if (callback != null) {
                        callback.onFaceDetected(face, faceBitmap, faceOutputArray[0])
                        if (!recognisedFaceList.isEmpty()) {
                            val result = findNearestFace(faceOutputArray[0])
                            // if distance is within confidence
                            if (result!!.second < 1.0f) {
                                faceGraphic.name = result.first
                                callback.onFaceRecognised(face, result.second, result.first)
                            }
                        }
                    }
                    graphicOverlay.add(faceGraphic)
                }
            })
            .addOnFailureListener {
            }
    }

    // looks for the nearest vector in the dataset (using L2 norm)
    // and returns the pair <name, distance>
    private fun findNearestFace(vector: FloatArray): Pair<String, Float>? {
        var ret: Pair<String, Float>? = null
        for (person in recognisedFaceList) {
            val name = person!!.name
            val knownVector = person.faceVector
            var distance = 0f
            for (i in vector.indices) {
                val diff = vector[i] - knownVector[i]
                distance += diff * diff
            }
            distance = Math.sqrt(distance.toDouble()).toFloat()
            if (ret == null || distance < ret.second) {
                ret = Pair(name, distance)
            }
        }
        return ret
    }

    override fun stop() {
        detector.close()
    }

    private fun cropToBBox(image: Bitmap?, boundingBox: Rect, rotation: Int): Bitmap? {
        var image = image
        val shift = 0
        if (rotation != 0) {
            val matrix = Matrix()
            matrix.postRotate(rotation.toFloat())
            image = Bitmap.createBitmap(image!!, 0, 0, image.width, image.height, matrix, true)
        }
        return if (boundingBox.top >= 0 && boundingBox.bottom <= image!!.width && boundingBox.top + boundingBox.height() <= image.height && boundingBox.left >= 0 && boundingBox.left + boundingBox.width() <= image.width) {
            Bitmap.createBitmap(
                image,
                boundingBox.left,
                boundingBox.top + shift,
                boundingBox.width(),
                boundingBox.height()
            )
        } else null
    }

    // Register a name against the vector
    fun registerFace(input: Editable, tempVector: FloatArray?) {
        recognisedFaceList.add(Person(input.toString(), tempVector!!))
    }

    companion object {
        private const val TAG = "FaceRecognitionProcessor"

        // Input image size for our facenet model
        private const val FACENET_INPUT_IMAGE_SIZE = 112
    }
}