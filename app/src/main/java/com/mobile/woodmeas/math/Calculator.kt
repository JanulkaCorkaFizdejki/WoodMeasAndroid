package com.mobile.woodmeas.math

import com.mobile.woodmeas.model.Trees

object Calculator {
    fun calculate(length: Int, width: Int, tree: Trees?): String {
        if (tree == null) {
            val v = 3.14F * (width * width).toFloat() * (length.toFloat() / 100.0F) / 40000.0F
            return "%.2f".format(v)
        }

        else {
            val widthMinusBark = setWidthMinusBark(width, tree)
            return if (widthMinusBark > 0) {
                val v = 3.14F * (widthMinusBark * widthMinusBark).toFloat() * (length.toFloat() / 100.0F) / 40000.0F
                return "%.2f".format(v)
            } else {
                "0.00"
            }
        }
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