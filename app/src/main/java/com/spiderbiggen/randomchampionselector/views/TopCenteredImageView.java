package com.spiderbiggen.randomchampionselector.views;

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
        Matrix matrix = new Matrix();
        matrix.reset();
        Drawable drawable = getDrawable();
        if (drawable == null) return;
        float width = drawable.getIntrinsicWidth();
        float height = drawable.getIntrinsicHeight();
        float xScale = w / width;
        float yScale = h / height;
        float scale;
        if (xScale > yScale) {
            scale = xScale;
        } else {
            float dX = (yScale - xScale) * width;
            matrix.setTranslate(dX, 0);
            scale = yScale;
        }
        matrix.postScale(scale, scale);
        System.out.println(matrix);
        setImageMatrix(matrix);
    }
}
