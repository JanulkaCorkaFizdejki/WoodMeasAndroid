package com.mobile.woodmeas.math

import com.mobile.woodmeas.model.Trees
import kotlin.math.pow
import kotlin.math.sqrt

object Calculator {

    fun rectangular(length: Int, width: Int, height: Int ): Int = length * width * height

    fun rectangularToLong(length: Int, width: Int, height: Int ): Long = length.toLong() * width.toLong() * height.toLong()

    fun logFormat(length: Int, width: Int, tree: Trees?): String {
        if (tree == null) {
            val v = Math.PI * (width * width).toLong()* (length.toLong() / 100.0) / 40000.0
            return "%.2f".format(v)
        }

        else {
            val widthMinusBark = setWidthMinusBark(width, tree)
            return if (widthMinusBark > 0) {
                val v = Math.PI * (widthMinusBark * widthMinusBark).toLong() * (length.toLong() / 100.0) / 40000.0
                return "%.2f".format(v)
            } else {
                "0.00"
            }
        }
    }

    fun thicknessUpperMethod(lengthM: Double, diameterUpper: Int): Double {
        val diameterUpperToDouble = diameterUpper.toDouble()
        val mod1Cov:Double = 6.2 + (74.0 * lengthM.pow(-3))
        val mod2Cov:Double = (0.48 / sqrt(lengthM)) - 0.12
        val mod3Cov:Double = diameterUpperToDouble - 22.0 + (0.3 * lengthM)
        // z
        val logConvergence:Double = (mod1Cov + (mod2Cov * mod3Cov)) * 0.1
        // ___________________________________________
        return (((logConvergence * 0.5) + diameterUpper.toLong()).pow(2) * lengthM * Math.PI) / 40000.0
    }

    private fun setWidthMinusBark(width: Int, tree: Trees): Int {
        return when (width) {
            in 0..24    ->    width - tree.dim1
            in 25..34   ->    width - tree.dim2
            in 35..49   ->    width - tree.dim3
            else        ->    width - tree.dim4
        }
    }
}