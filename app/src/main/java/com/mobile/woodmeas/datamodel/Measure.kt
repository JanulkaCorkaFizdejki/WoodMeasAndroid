package com.mobile.woodmeas.datamodel

import com.mobile.woodmeas.R

enum class Measure {
    LENGTH {
        override fun getSeekBar(): Int = R.id.seekBarMeasureLength
        override fun getTextViewCmVal(): Int  = R.id.textViewMeasureLengthCmVal
        override fun getTextViewMVal(): Int  = R.id.textViewMeasureLengthMVal
        override fun getImageButtonPlus(): Int = R.id.imageButtonMeasureLengthPlus
        override fun getImageButtonMinus(): Int = R.id.imageButtonMeasureLengthMinus
    },

    WIDTH {
        override fun getSeekBar(): Int = R.id.seekBarMeasureWidth
        override fun getTextViewCmVal(): Int  = R.id.textViewMeasureWidthCmVal
        override fun getTextViewMVal(): Int  = R.id.textViewMeasureWidthMVal
        override fun getImageButtonPlus(): Int = R.id.imageButtonMeasureWidthPlus
        override fun getImageButtonMinus(): Int = R.id.imageButtonMeasureWidthMinus
    },

    DIAMETER {
        override fun getSeekBar(): Int = R.id.seekBarMeasureDiameter
        override fun getTextViewCmVal(): Int  = R.id.textViewMeasureDiameterCmVal
        override fun getTextViewMVal(): Int  = R.id.textViewMeasureDiameterMVal
        override fun getImageButtonPlus(): Int = R.id.imageButtonMeasureDiameterPlus
        override fun getImageButtonMinus(): Int = R.id.imageButtonMeasureDiameterMinus
    },

    THICKNESS {
        override fun getSeekBar(): Int = R.id.seekBarMeasureThickness
        override fun getTextViewCmVal(): Int  = R.id.textViewMeasureThicknessCmVal
        override fun getTextViewMVal(): Int  = R.id.textViewMeasureThicknessMVal
        override fun getImageButtonPlus(): Int = R.id.imageButtonMeasureThicknessPlus
        override fun getImageButtonMinus(): Int = R.id.imageButtonMeasureThicknessMinus
    };

    abstract fun getSeekBar(): Int
    abstract fun getTextViewCmVal(): Int
    abstract fun getTextViewMVal(): Int
    abstract fun getImageButtonPlus(): Int
    abstract fun getImageButtonMinus(): Int
}