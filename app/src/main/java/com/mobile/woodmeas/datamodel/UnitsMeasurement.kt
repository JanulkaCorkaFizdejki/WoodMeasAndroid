package com.mobile.woodmeas.datamodel

import android.content.Context
import com.mobile.woodmeas.R

enum class UnitsMeasurement {
    CM{
        override fun getNameUnit(context: Context): String {
            return context.getString(R.string.centimeters_short)
        }

        override fun getNameUnitCubic(context: Context): String {
            return context.getString(R.string.meters_short)
        }

    },

    IN{
        override fun getNameUnit(context: Context): String {
            return context.getString(R.string.inch_short)
        }

        override fun getNameUnitCubic(context: Context): String {
            return context.getString(R.string.foot_short)
        }
    };

    abstract fun getNameUnit(context: Context): String
    abstract fun getNameUnitCubic(context: Context): String

    companion object {
        fun convertToInchToString(cm: Int): String {
            return "%.2f".format(cm.toFloat() / 2.54F).replace(".", ",")
        }
        fun convertToInchToFloat(cm: Int): Float {
            return "%.2f".format(cm.toFloat() / 2.54F).replace(",", ".").toFloat()
        }
        fun convertToFootToString(m: Float): String {
            return "%.2f".format(m * 35.315F).replace(".", ",")
        }
        fun convertToFootToFloat(m: Float): Float {
            return "%.2f".format(m * 35.315F).replace(",", ".").toFloat()
        }
    }
}