package com.mobile.woodmeas.datamodel

import com.mobile.woodmeas.model.Trees

enum class WoodCoefficients {
    S2 {
        override fun setResult(
            m3: Float,
            length: Int?,
            bark: Boolean,
            tree: Trees,
            cross: Boolean,
            customFactor: Float?
        ): Float {
            return if (customFactor != null) {
                "%.2f".format(m3 * customFactor * if (cross) 0.75F else 1.00F).replace(",", ".").toFloat()
            } else {
                return when (tree.id) {
                    3 -> {
                        val factor = when(length) {
                            in 0..199 -> if (bark) 0.65F else 0.75F
                            in 200..499 ->  if (bark) 0.62F else 0.72F
                            else -> if (bark) 0.60F else 0.72F
                        }
                        "%.2f".format(m3 * factor * if (cross) 0.75F else 1.00F).replace(",", ".").toFloat()
                    }
                    in 1..2 -> {
                        val factor = when(length) {
                            in 0..199 -> if (bark) 0.70F else 0.78F
                            in 200..299 ->  if (bark) 0.67F else 0.75F
                            else -> if (bark) 0.65F else 0.75F
                        }
                        "%.2f".format(m3 * factor * if (cross) 0.75F else 1.00F).replace(",", ".").toFloat()
                    }
                    in 6..7 -> {
                        "%.2f".format(m3 * if(bark) 0.70F else 0.75F * if (cross) 0.75F else 1.00F).replace(",", ".").toFloat()
                    }
                    else -> "%.2f".format(m3 * if(bark) 0.65F else 0.75F * if (cross) 0.75F else 1.00F).replace(",", ".").toFloat()
                }
            }
        }

        override fun getNameType(): String = "S2"
    },

    S3 {
        override fun setResult(
            m3: Float,
            length: Int?,
            bark: Boolean,
            tree: Trees,
            cross: Boolean,
            customFactor: Float?
        ): Float {
            return if (customFactor != null) {
                "%.2f".format(m3 * customFactor * if (cross) 0.75F else 1.00F).replace(",", ".").toFloat()
            } else {
                if (length != null) {
                    return if (length <= 399) {
                        "%.2f".format(m3 * 0.50 * if (cross) 0.75F else 1.00F).replace(",", ".").toFloat()
                    } else {
                        "%.2f".format(m3 * 0.40 * if (cross) 0.75F else 1.00F).replace(",", ".").toFloat()
                    }
                }
                else { 0.0F }
            }
        }

        override fun getNameType(): String = "S3"

    },

    S4 {
        override fun setResult(
            m3: Float,
            length: Int?,
            bark: Boolean,
            tree: Trees,
            cross: Boolean,
            customFactor: Float?
        ): Float {
            return if (customFactor != null) {
                "%.2f".format(m3 * customFactor * if (cross) 0.75F else 1.00F).replace(",", ".").toFloat()
            } else {
                return if (tree.type == 0 || tree.id == 3) {
                    val factor = if (bark) 0.65F else 0.75F
                    "%.2f".format(m3 * factor * if (cross) 0.75F else 1.00F).replace(",", ".").toFloat()
                } else {
                    val factor = if (bark) 0.70F else 0.75F
                    "%.2f".format(m3 * factor * if (cross) 0.75F else 1.00F).replace(",", ".").toFloat()
                }
            }
        }
        override fun getNameType(): String = "S4"
    },

    M1 {
        override fun setResult(
            m3: Float,
            length: Int?,
            bark: Boolean,
            tree: Trees,
            cross: Boolean,
            customFactor: Float?
        ): Float {
            return if (customFactor != null) {
                "%.2f".format(m3 * customFactor * if (cross) 0.75F else 1.00F).replace(",", ".").toFloat()
            } else {
                "%.2f".format(m3 * 0.40F * if (cross) 0.75F else 1.00F).replace(",", ".").toFloat()
            }
        }
        override fun getNameType(): String = "M1"

    },

    M2 {
        override fun setResult(
            m3: Float,
            length: Int?,
            bark: Boolean,
            tree: Trees,
            cross: Boolean,
            customFactor: Float?
        ): Float {
            return if (customFactor != null) {
                "%.2f".format(m3 * customFactor * if (cross) 0.75F else 1.00F).replace(",", ".").toFloat()
            } else {
                "%.2f".format(m3 * 0.25F * if (cross) 0.75F else 1.00F).replace(",", ".").toFloat()
            }
        }
        override fun getNameType(): String = "M2"

    };

    abstract fun setResult(m3: Float, length: Int?, bark: Boolean, tree: Trees, cross: Boolean, customFactor: Float?): Float
    abstract fun getNameType(): String
    
}