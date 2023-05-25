package com.example.smart_attendence_system.helper


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.text.TextUtils
import android.util.Log
import com.example.smart_attendence_system.R
//import com.example.mlseriesdemonstrator.helpers.vision.obscure.ObscureType
import com.example.smart_attendence_system.helper.GraphicOverlay.Graphic
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceContour
import com.google.mlkit.vision.face.FaceLandmark
import com.google.mlkit.vision.face.FaceLandmark.LandmarkType
import java.util.Locale


/**
 * Graphic instance for rendering face position, contour, and landmarks within the associated
 * graphic overlay view.
 */
class FaceGraphic @JvmOverloads constructor(
    overlay: GraphicOverlay,
    face: Face,
    isDrowsy: Boolean,
    width: Int,
    height: Int,
    age: Int = -1,
    gender: Int = -1
) :
    Graphic(overlay, width, height) {
    private val facePositionPaint: Paint
    private val idPaints: Array<Paint?>
    private val boxPaints: Array<Paint?>
    private val labelPaints: Array<Paint?>
    private val smileyBitmap: Bitmap
    private val isDrowsy: Boolean
    var faceBoundingBox: RectF?
    private val face: Face
    var age: Int
    var gender: Int
    var name: String? = null
    var obscureType: ObscureType = ObscureType.NONE

    init {
        smileyBitmap = BitmapFactory.decodeResource(overlay.resources, R.drawable.smiley)
        this.isDrowsy = isDrowsy
        this.face = face
        this.age = age
        this.gender = gender
        faceBoundingBox = transform(face.boundingBox)
        val selectedColor = Color.WHITE
        facePositionPaint = Paint()
        facePositionPaint.color = selectedColor
        val numColors = COLORS.size
        idPaints = arrayOfNulls(numColors)
        boxPaints = arrayOfNulls(numColors)
        labelPaints = arrayOfNulls(numColors)
        for (i in 0 until numColors) {
            idPaints[i] = Paint()
            idPaints[i]!!.color = COLORS[i][0]
            idPaints[i]!!.textSize = ID_TEXT_SIZE
            boxPaints[i] = Paint()
            boxPaints[i]!!.color = COLORS[i][1]
            boxPaints[i]!!.style = Paint.Style.STROKE
            boxPaints[i]!!.strokeWidth = BOX_STROKE_WIDTH
            labelPaints[i] = Paint()
            labelPaints[i]!!.color = COLORS[i][1]
            labelPaints[i]!!.style = Paint.Style.FILL
        }
    }

    /** Draws the face annotations for position on the supplied canvas.  */
    override fun draw(canvas: Canvas?) {
        if (faceBoundingBox == null) {
            return
        }

        // Draws a circle at the position of the detected face, with the face's track id below.
        val x = faceBoundingBox!!.centerX()
        val y = faceBoundingBox!!.centerY()
        canvas!!.drawCircle(x, y, FACE_POSITION_RADIUS, facePositionPaint)

        // Calculate positions.
        val left = faceBoundingBox!!.left
        val top = faceBoundingBox!!.top
        val right = faceBoundingBox!!.right
        val bottom = faceBoundingBox!!.bottom
        val lineHeight = ID_TEXT_SIZE + BOX_STROKE_WIDTH
        var yLabelOffset: Float = if (face.trackingId == null) 0F else -lineHeight

        // Decide color based on face ID
        val colorID = if (face.trackingId == null) 0 else Math.abs(face.trackingId!! % NUM_COLORS)

        // Calculate width and height of label box
        var textWidth = idPaints[colorID]!!.measureText("ID: " + face.trackingId)
        if (face.smilingProbability != null) {
            yLabelOffset -= lineHeight
            textWidth = Math.max(
                textWidth,
                idPaints[colorID]!!
                    .measureText(String.format(Locale.US, "Smiling: %.2f", face.smilingProbability))
            )
        }
        /// add drowsy
        yLabelOffset -= lineHeight
        textWidth = Math.max(
            textWidth,
            idPaints[colorID]!!
                .measureText(String.format(Locale.US, "Drowsy: %s", isDrowsy))
        )
        if (face.leftEyeOpenProbability != null) {
            yLabelOffset -= lineHeight
            textWidth = Math.max(
                textWidth,
                idPaints[colorID]!!.measureText(
                    String.format(
                        Locale.US, "Left eye open: %.2f", face.leftEyeOpenProbability
                    )
                )
            )
        }
        if (face.rightEyeOpenProbability != null) {
            yLabelOffset -= lineHeight
            textWidth = Math.max(
                textWidth,
                idPaints[colorID]!!.measureText(
                    String.format(
                        Locale.US, "Right eye open: %.2f", face.rightEyeOpenProbability
                    )
                )
            )
        }
        yLabelOffset = yLabelOffset - 3 * lineHeight
        textWidth = Math.max(
            textWidth,
            idPaints[colorID]!!
                .measureText(String.format(Locale.US, "EulerX: %.2f", face.headEulerAngleX))
        )
        textWidth = Math.max(
            textWidth,
            idPaints[colorID]!!
                .measureText(String.format(Locale.US, "EulerY: %.2f", face.headEulerAngleY))
        )
        textWidth = Math.max(
            textWidth,
            idPaints[colorID]!!
                .measureText(String.format(Locale.US, "EulerZ: %.2f", face.headEulerAngleZ))
        )
        // Draw labels
        canvas.drawRect(
            left - BOX_STROKE_WIDTH,
            top + yLabelOffset,
            left + textWidth + 2 * BOX_STROKE_WIDTH,
            top,
            labelPaints[colorID]!!
        )
        yLabelOffset += ID_TEXT_SIZE
        canvas.drawRect(left, top, right, bottom, boxPaints[colorID]!!)
        if (face.trackingId != null) {
            canvas.drawText(
                "ID: " + face.trackingId, left, top + yLabelOffset,
                idPaints[colorID]!!
            )
            yLabelOffset += lineHeight
        }
        if (obscureType === ObscureType.SMILEY) {
            canvas.drawBitmap(smileyBitmap, null, faceBoundingBox!!, null)
        } else if (obscureType === ObscureType.TRANSLUCENT) {
            val translucentPaint = Paint()
            translucentPaint.color = Color.WHITE
            translucentPaint.alpha = 240 // 0 - 255
            translucentPaint.style = Paint.Style.FILL_AND_STROKE
            val faceOval = RectF(faceBoundingBox)
            if (!face.allContours.isEmpty()) {
                var minX = Float.MAX_VALUE
                var maxX = Float.MIN_VALUE
                for (faceContour in face.allContours) {
                    if (faceContour.faceContourType == FaceContour.FACE) {
                        for (point in faceContour.points) {
                            minX = Math.min(minX, point.x)
                            maxX = Math.max(maxX, point.x)
                        }
                    }
                }
                faceOval.left = translateX(minX)
                faceOval.right = translateX(maxX)
            }
            canvas.drawOval(faceOval, translucentPaint)
        } else if (obscureType === ObscureType.NONE) {
            // Draws all face contours.
            for (contour in face.allContours) {
                for (point in contour.points) {
                    canvas.drawCircle(
                        translateX(point.x),
                        translateY(point.y),
                        FACE_POSITION_RADIUS,
                        facePositionPaint
                    )
                }
            }
        }

        // Draws smiling and left/right eye open probabilities.
        if (face.smilingProbability != null) {
            canvas.drawText(
                "Smiling: " + String.format(Locale.US, "%.2f", face.smilingProbability),
                left,
                top + yLabelOffset,
                idPaints[colorID]!!
            )
            yLabelOffset += lineHeight
        }
        if (face.leftEyeOpenProbability != null) {
            canvas.drawText(
                "Drowsy: " + String.format(Locale.US, "%s", isDrowsy),
                left,
                top + yLabelOffset,
                idPaints[colorID]!!
            )
            yLabelOffset += lineHeight
        }
        val leftEye = face.getLandmark(FaceLandmark.LEFT_EYE)
        if (face.leftEyeOpenProbability != null) {
            canvas.drawText(
                "Left eye open: " + String.format(Locale.US, "%.2f", face.leftEyeOpenProbability),
                left,
                top + yLabelOffset,
                idPaints[colorID]!!
            )
            yLabelOffset += lineHeight
        }
        if (leftEye != null) {
            val leftEyeLeft = translateX(leftEye.position.x) - idPaints[colorID]!!
                .measureText("Left Eye") / 2.0f
            canvas.drawRect(
                leftEyeLeft - BOX_STROKE_WIDTH,
                translateY(leftEye.position.y) + ID_Y_OFFSET - ID_TEXT_SIZE,
                leftEyeLeft + idPaints[colorID]!!.measureText("Left Eye") + BOX_STROKE_WIDTH,
                translateY(leftEye.position.y) + ID_Y_OFFSET + BOX_STROKE_WIDTH,
                labelPaints[colorID]!!
            )
            canvas.drawText(
                "Left Eye",
                leftEyeLeft,
                translateY(leftEye.position.y) + ID_Y_OFFSET,
                idPaints[colorID]!!
            )
        }
        val rightEye = face.getLandmark(FaceLandmark.RIGHT_EYE)
        if (face.rightEyeOpenProbability != null) {
            canvas.drawText(
                "Right eye open: " + String.format(Locale.US, "%.2f", face.rightEyeOpenProbability),
                left,
                top + yLabelOffset,
                idPaints[colorID]!!
            )
            yLabelOffset += lineHeight
        }
        if (rightEye != null) {
            val rightEyeLeft = translateX(rightEye.position.x) - idPaints[colorID]!!
                .measureText("Right Eye") / 2.0f
            canvas.drawRect(
                rightEyeLeft - BOX_STROKE_WIDTH,
                translateY(rightEye.position.y) + ID_Y_OFFSET - ID_TEXT_SIZE,
                rightEyeLeft + idPaints[colorID]!!.measureText("Right Eye") + BOX_STROKE_WIDTH,
                translateY(rightEye.position.y) + ID_Y_OFFSET + BOX_STROKE_WIDTH,
                labelPaints[colorID]!!
            )
            canvas.drawText(
                "Right Eye",
                rightEyeLeft,
                translateY(rightEye.position.y) + ID_Y_OFFSET,
                idPaints[colorID]!!
            )
        }

//    canvas.drawText(
//        "EulerX: " + face.getHeadEulerAngleX(), left, top + yLabelOffset, idPaints[colorID]);
//    yLabelOffset += lineHeight;
//    canvas.drawText(
//        "EulerY: " + face.getHeadEulerAngleY(), left, top + yLabelOffset, idPaints[colorID]);
//    yLabelOffset += lineHeight;
//    canvas.drawText(
//        "EulerZ: " + face.getHeadEulerAngleZ(), left, top + yLabelOffset, idPaints[colorID]);
//    yLabelOffset += lineHeight;
        if (age > -1) {
            canvas.drawText(
                "Age: $age", left, top + yLabelOffset, idPaints[colorID]!!
            )
        }
        yLabelOffset += lineHeight
        Log.d("xx", "gender $gender")
        if (gender != -1) {
            canvas.drawText(
                "G: " + if (gender == 0) "Male" else "Female", left, top + yLabelOffset,
                idPaints[colorID]!!
            )
        }
        yLabelOffset += lineHeight
        if (!TextUtils.isEmpty(name)) {
            canvas.drawText("Name: $name", left, top + yLabelOffset, idPaints[colorID]!!)
        }
        yLabelOffset += lineHeight

        // Draw facial landmarks
        drawFaceLandmark(canvas, FaceLandmark.LEFT_EYE)
        drawFaceLandmark(canvas, FaceLandmark.RIGHT_EYE)
        drawFaceLandmark(canvas, FaceLandmark.LEFT_CHEEK)
        drawFaceLandmark(canvas, FaceLandmark.RIGHT_CHEEK)
    }

    private fun drawFaceLandmark(canvas: Canvas?, @LandmarkType landmarkType: Int) {
        val faceLandmark = face.getLandmark(landmarkType)
        if (faceLandmark != null) {
            canvas!!.drawCircle(
                translateX(faceLandmark.position.x),
                translateY(faceLandmark.position.y),
                FACE_POSITION_RADIUS,
                facePositionPaint
            )
        }
    }

    companion object {
        private const val FACE_POSITION_RADIUS = 8.0f
        private const val ID_TEXT_SIZE = 30.0f
        private const val ID_Y_OFFSET = 40.0f
        private const val BOX_STROKE_WIDTH = 5.0f
        private const val NUM_COLORS = 10
        private val COLORS = arrayOf(
            intArrayOf(Color.BLACK, Color.WHITE),
            intArrayOf(Color.WHITE, Color.MAGENTA),
            intArrayOf(
                Color.BLACK, Color.LTGRAY
            ),
            intArrayOf(Color.WHITE, Color.RED),
            intArrayOf(Color.WHITE, Color.BLUE),
            intArrayOf(
                Color.WHITE, Color.DKGRAY
            ),
            intArrayOf(Color.BLACK, Color.CYAN),
            intArrayOf(Color.BLACK, Color.YELLOW),
            intArrayOf(
                Color.WHITE, Color.BLACK
            ),
            intArrayOf(Color.BLACK, Color.GREEN)
        )
    }

    enum class ObscureType {
        NONE, SMILEY, TRANSLUCENT
    }
}