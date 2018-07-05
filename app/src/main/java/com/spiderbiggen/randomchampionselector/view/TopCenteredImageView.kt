package com.spiderbiggen.randomchampionselector.view

import android.content.Context
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.ImageView

/**
 * Created on 6-3-2018.
 *
 * @author Stefan Breetveld
 */

class TopCenteredImageView : android.support.v7.widget.AppCompatImageView {

    private var viewWidth: Int = 0
    private var viewHeight: Int = 0

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        super.setScaleType(ImageView.ScaleType.MATRIX)
    }

    override fun getScaleType(): ImageView.ScaleType {
        return ImageView.ScaleType.MATRIX
    }

    /**
     * Ignore all ScaleTypes.
     *
     * @param scaleType ignored.
     */
    override fun setScaleType(scaleType: ImageView.ScaleType) {
        // Do nothing
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        this.viewWidth = w
        this.viewHeight = h
        calculateAndSetImageMatrix()
    }

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
