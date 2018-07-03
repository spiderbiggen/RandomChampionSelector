package com.spiderbiggen.randomchampionselector.view;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/**
 * Created on 6-3-2018.
 *
 * @author Stefan Breetveld
 */

public class TopCenteredImageView extends android.support.v7.widget.AppCompatImageView {

    private int width;
    private int height;

    public TopCenteredImageView(Context context) {
        super(context);
        init();
    }

    public TopCenteredImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TopCenteredImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        super.setScaleType(ScaleType.MATRIX);
    }

    @Override
    public ScaleType getScaleType() {
        return ScaleType.MATRIX;
    }

    /**
     * Ignore all ScaleTypes.
     *
     * @param scaleType ignored.
     */
    @Override
    public void setScaleType(ScaleType scaleType) {
        // Do nothing
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.width = w;
        this.height = h;
        calculateAndSetImageMatrix();
    }

    private void calculateAndSetImageMatrix() {
        Matrix matrix = new Matrix();
        matrix.reset();
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }
        float width = drawable.getIntrinsicWidth();
        float height = drawable.getIntrinsicHeight();
        float xScale = this.width / width;
        float yScale = this.height / height;
        float scale;
        if (xScale > yScale) {
            scale = xScale;
        } else {
            float dX = -0.5f * (yScale - xScale) * width;
            matrix.setTranslate(dX, 0);
            scale = yScale;
        }
        matrix.postScale(scale, scale);
        setImageMatrix(matrix);
    }

    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        super.setImageDrawable(drawable);
        calculateAndSetImageMatrix();
    }
}
