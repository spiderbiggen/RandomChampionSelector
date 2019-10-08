package com.spiderbiggen.randomchampionselector

import android.content.Context
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

/**
 * An [AppCompatImageView] that keeps images horizontally centered and the top at the top of the view
 *
 * @author Stefan Breetveld
 */
class TopCenteredImageView : AppCompatImageView {

    private var viewWidth: Int = 0
    private var viewHeight: Int = 0

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

    init {
        super.setScaleType(ScaleType.MATRIX)
    }

    override fun getScaleType(): ScaleType {
        return ScaleType.MATRIX
    }

    /**
     * Ignore all ScaleTypes.
     *
     * @param scaleType ignored.
     */
    override fun setScaleType(scaleType: ScaleType) {
        // Do nothing
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        this.viewWidth = w
        this.viewHeight = h
        calculateAndSetImageMatrix()
    }

    /**
     * Calculate the [Matrix] for the image so that the top is stuck at the top of the view
     * and the image is horizontally centered. Keep the image as large as possible.
     */
    private fun calculateAndSetImageMatrix() {
        val matrix = Matrix()
        matrix.reset()
        val drawable = drawable ?: return
        val width = drawable.intrinsicWidth.toFloat()
        val height = drawable.intrinsicHeight.toFloat()
        val xScale = this.viewWidth / width
        val yScale = this.viewHeight / height
        val scale: Float
        scale = if (xScale > yScale) {
            xScale
        } else {
            val dX = -0.5f * (yScale - xScale) * width
            matrix.setTranslate(dX, 0f)
            yScale
        }
        matrix.postScale(scale, scale)
        imageMatrix = matrix
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        calculateAndSetImageMatrix()
    }
}
