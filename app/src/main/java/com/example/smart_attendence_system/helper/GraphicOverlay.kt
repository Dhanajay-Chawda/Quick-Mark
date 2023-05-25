package com.example.smart_attendence_system.helper

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.camera.core.CameraSelector


/**
 * A view which renders a series of custom graphics to be overlaid on top of an associated preview
 * (i.e., the camera preview). The creator can add graphics objects, update the objects, and remove
 * them, triggering the appropriate drawing and invalidation within the view.
 */
class GraphicOverlay(context: Context?, attrs: AttributeSet?) :
    View(context, attrs) {
    private val lock = Any()
    private val graphics: MutableList<Graphic> = ArrayList()
    var previewWidth = 0
    var previewHeight = 0
    var isLensFacingFront = false

    /**
     * Base class for a custom graphics object to be rendered within the graphic overlay. Subclass
     * this and implement the [Graphic.draw] method to define the graphics element. Add
     * instances to the overlay using [GraphicOverlay.add].
     */
    abstract class Graphic(
        private val overlay: GraphicOverlay,
        private val imageWidth: Int,
        private val imageHeight: Int
    ) {
        /**
         * Draw the graphic on the supplied canvas. Drawing should use the following methods to convert
         * to view coordinates for the graphics that are drawn:
         *
         *
         *  1. [Graphic.translateX] and [Graphic.translateY] adjust the
         * coordinate from the image's coordinate system to the view coordinate system.
         *
         *
         * @param canvas drawing canvas
         */
        abstract fun draw(canvas: Canvas?)
        fun transform(rect: Rect): RectF {
            val scaleX = overlay.previewWidth / imageWidth.toFloat()
            val scaleY = overlay.previewHeight / imageHeight.toFloat()
            // If the front camera lens is being used, reverse the right/left coordinates
            val flippedLeft: Float
            flippedLeft =
                if (overlay.isLensFacingFront) (imageWidth - rect.right).toFloat() else rect.left.toFloat()
            val flippedRight: Float
            flippedRight =
                if (overlay.isLensFacingFront) (imageWidth - rect.left).toFloat() else rect.right.toFloat()

            // Scale all coordinates to match preview
            val scaledLeft = scaleX * flippedLeft
            val scaledTop = scaleY * rect.top
            val scaledRight = scaleX * flippedRight
            val scaledBottom = scaleY * rect.bottom
            return RectF(scaledLeft, scaledTop, scaledRight, scaledBottom)
        }

        /**
         * Adjusts the x coordinate from the image's coordinate system to the view coordinate system.
         */
        fun translateX(x: Float): Float {
            val scaleX = overlay.previewWidth / imageWidth.toFloat()
            val flippedX: Float
            flippedX = if (overlay.isLensFacingFront) {
                imageWidth - x
            } else {
                x
            }
            return flippedX * scaleX
        }

        /**
         * Adjusts the y coordinate from the image's coordinate system to the view coordinate system.
         */
        fun translateY(y: Float): Float {
            val scaleY = overlay.previewHeight / imageHeight.toFloat()
            return y * scaleY
        }

        fun postInvalidate() {
            overlay.postInvalidate()
        }
    }

    init {
        addOnLayoutChangeListener { view: View?, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int -> postInvalidate() }
    }

    /** Removes all graphics from the overlay.  */
    fun clear() {
        synchronized(lock) { graphics.clear() }
        postInvalidate()
    }

    /** Adds a graphic to the overlay.  */
    fun add(graphic: Graphic) {
        synchronized(lock) { graphics.add(graphic) }
        postInvalidate()
    }

    /** Removes a graphic from the overlay.  */
    fun remove(graphic: Graphic) {
        synchronized(lock) { graphics.remove(graphic) }
        postInvalidate()
    }

    /** Draws the overlay with its associated graphic objects.  */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        synchronized(lock) {
            for (graphic in graphics) {
                graphic.draw(canvas)
            }
        }
    }

    fun setPreviewProperties(previewWidth: Int, previewHeight: Int, lensFacing: Int) {
        this.previewWidth = previewWidth
        this.previewHeight = previewHeight
        isLensFacingFront = CameraSelector.LENS_FACING_FRONT == lensFacing
    }
}