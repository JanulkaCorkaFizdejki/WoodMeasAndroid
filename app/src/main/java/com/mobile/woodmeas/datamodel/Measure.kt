package com.mobile.woodmeas.datamodel

import com.mobile.woodmeas.R

enum class Measure {
    LENGTH {
        override fun getSeekBar(): Int = R.id.seekBarMeasureLength
        override fun getTextViewCmVal(): Int  = R.id.textViewMeasureLengthCmVal
        override fun getTextViewMVal(): Int  = R.id.textViewMeasureLengthMVal
        override fun getImageButtonPlus(): Int = R.id.imageButtonMeasureLengthPlus
        override fun getImageButtonMinus(): Int = R.id.imageButtonMeasureLengthMinus
        override fun getUnitCm(): Int = R.id.textViewMeasureLengthCm
        override fun getUnitM(): Int = R.id.textViewMeasureLengthM

    },

    WIDTH {
        override fun getSeekBar(): Int = R.id.seekBarMeasureWidth
        override fun getTextViewCmVal(): Int  = R.id.textViewMeasureWidthCmVal
        override fun getTextViewMVal(): Int  = R.id.textViewMeasureWidthMVal
        override fun getImageButtonPlus(): Int = R.id.imageButtonMeasureWidthPlus
        override fun getImageButtonMinus(): Int = R.id.imageButtonMeasureWidthMinus
        override fun getUnitCm(): Int = R.id.textViewMeasureWidthCm
        override fun getUnitM(): Int = R.id.textViewMeasureWidthM
    },

    DIAMETER {
        override fun getSeekBar(): Int = R.id.seekBarMeasureDiameter
        override fun getTextViewCmVal(): Int  = R.id.textViewMeasureDiameterCmVal
        override fun getTextViewMVal(): Int  = R.id.textViewMeasureDiameterMVal
        override fun getImageButtonPlus(): Int = R.id.imageButtonMeasureDiameterPlus
        override fun getImageButtonMinus(): Int = R.id.imageButtonMeasureDiameterMinus
        override fun getUnitCm(): Int = R.id.textViewMeasureDiameterCm
        override fun getUnitM(): Int = R.id.textViewMeasureDiameterM
    },

    THICKNESS {
        override fun getSeekBar(): Int = R.id.seekBarMeasureThickness
        override fun getTextViewCmVal(): Int  = R.id.textViewMeasureThicknessCmVal
        override fun getTextViewMVal(): Int  = R.id.textViewMeasureThicknessMVal
        override fun getImageButtonPlus(): Int = R.id.imageButtonMeasureThicknessPlus
        override fun getImageButtonMinus(): Int = R.id.imageButtonMeasureThicknessMinus
        override fun getUnitCm(): Int = R.id.textViewMeasureThicknessCm
        override fun getUnitM(): Int = R.id.textViewMeasureThicknessM
    };

    abstract fun getSeekBar(): Int
    abstract fun getTextViewCmVal(): Int
    abstract fun getTextViewMVal(): Int
    abstract fun getImageButtonPlus(): Int
    abstract fun getImageButtonMinus(): Int
    abstract fun getUnitCm(): Int
    abstract fun getUnitM(): Int
}