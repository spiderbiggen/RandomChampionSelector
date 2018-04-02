package com.spiderbiggen.randomchampionselector.ui.views;

/* The following code was written by Matthew Wiggins
 * and is released under the APACHE 2.0 license
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.spiderbiggen.randomchampionselector.R;


public class SeekBarPreference extends DialogPreference implements SeekBar.OnSeekBarChangeListener {

    private SeekBar mSeekBar;
    private TextView mSplashText;
    private TextView mValueText;
    private Context mContext;

    private String mDialogMessage, mSuffix;
    private int mMin, mDefault, mMax, mValue;

    // Constructor :
    public SeekBarPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;

        // Get string value for dialogMessage :
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SeekBarPreference, defStyleAttr, defStyleRes);
        mDialogMessage = array.getString(R.styleable.SeekBarPreferenceTheme_dialogMessage);

        // Get string value for suffix (text attribute in xml file) :
        mSuffix = array.getString(R.styleable.SeekBarPreferenceTheme_suffix);

        // Get default and max seekbar values :
        mMin = array.getInt(R.styleable.SeekBarPreferenceTheme_min, 0);
        mMax = array.getInt(R.styleable.SeekBarPreferenceTheme_min, 100);
        mDefault = array.getInt(R.styleable.SeekBarPreferenceTheme_defaultValue, mMin);
        if (mMin > mMax)
            throw new IllegalArgumentException("Minimum(" + mMin + ") value is larger than maximum(" + mMax + ")");
        if (mMin > mDefault)
            throw new IllegalArgumentException("Minimum(" + mMin + ") value is larger than default value(" + mDefault + ")");

        mValue = shouldPersist() ? getPersistedInt(mDefault) : mDefault;
        setSummary(createSummary());
        array.recycle();
    }


    // DialogPreference methods :
    @Override
    protected View onCreateDialogView() {
        LinearLayout.LayoutParams params;
        LinearLayout layout = new LinearLayout(mContext);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(6, 6, 6, 6);

        mSplashText = new TextView(mContext);
        mSplashText.setPadding(30, 10, 30, 10);
        if (mDialogMessage != null)
            mSplashText.setText(mDialogMessage);
        layout.addView(mSplashText);

        mValueText = new TextView(mContext);
        mValueText.setGravity(Gravity.CENTER_HORIZONTAL);
        mValueText.setTextSize(32);
        params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.addView(mValueText, params);

        mSeekBar = new SeekBar(mContext);
        mSeekBar.setOnSeekBarChangeListener(this);
        layout.addView(mSeekBar, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        mValue = shouldPersist() ? getPersistedInt(mDefault) : mDefault;

        mSeekBar.setMax(mMax - mMin);
        updateProgress();
        setSummary(createSummary());
        return layout;
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);
        mSeekBar.setMax(mMax - mMin);
        updateProgress();
    }

    @Override
    protected void onSetInitialValue(boolean restore, Object defaultValue) {
        super.onSetInitialValue(restore, defaultValue);
        mValue = restore && shouldPersist() ? getPersistedInt(mDefault) : (Integer) defaultValue;
    }

    @Override
    public void onProgressChanged(SeekBar seek, int value, boolean fromTouch) {
        String t = String.valueOf(mMin + value);
        mValueText.setText(mSuffix == null ? t : t + mSuffix);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seek) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seek) {
    }

    public int getMin() {
        return mMin;
    }

    public void setMin(int Min) {
        this.mMin = Min;
    }

    public int getMax() {
        return mMax;
    }

    public void setMax(int max) {
        mMax = max;
    }

    public int getValue() {
        return mValue;
    }

    public void setValue(int value) {
        if (value < mMin) value = mMin;
        if (value > mMax) value = mMax;
        mValue = value;
        updateProgress();
    }

    private void updateProgress() {
        if (mSeekBar != null)
            mSeekBar.setProgress(mValue - mMin);
    }

    public String createSummary() {
        return mSuffix == null ? String.valueOf(mValue) : mValue + mSuffix;
    }


    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult && shouldPersist()) {
            mValue = mMin + mSeekBar.getProgress();
            persistInt(mValue);
            callChangeListener(mValue);
        }
    }
}