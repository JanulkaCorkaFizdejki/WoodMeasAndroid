package com.mobile.woodmeas.viewcontrollers

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.os.Build
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.mobile.woodmeas.R

object CubicToMoney {
    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("ResourceAsColor", "UseCompatLoadingForDrawables")
    fun run(context: Context, m3: Float) {
        var multiplier = 1

        val alertDialog = AlertDialog.Builder(context)
        val viewAlertInflate: View = View.inflate(context, R.layout.cubic_to_money_measurement_panel, null)

        val seekBar = viewAlertInflate.findViewById<SeekBar>(R.id.seekBarMeasureCubicToMoney)

        val baseMaxProgress = seekBar.max

        val textViewMeasureCubicToMoneyM3 = viewAlertInflate.findViewById<TextView>(R.id.textViewMeasureCubicToMoneyM3)
        val textViewMeasureCubicToMoneyResult = viewAlertInflate.findViewById<TextView>(R.id.textViewMeasureCubicToMoneyResult)
        val textViewMeasureCubicToMoneyProgress = viewAlertInflate.findViewById<TextView>(R.id.textViewMeasureCubicToMoneyProgress)
        val textViewMeasureCubicToMoneyMax = viewAlertInflate.findViewById<TextView>(R.id.textViewMeasureCubicToMoneyMax)

        val multiplierButtons: List<Button> = listOf(
            viewAlertInflate.findViewById(R.id.buttonMeasureCubicToMoneyPlus10),
            viewAlertInflate.findViewById(R.id.buttonMeasureCubicToMoneyPlus100),
            viewAlertInflate.findViewById(R.id.buttonMeasureCubicToMoneyPlus1000)
        )

        val plusButton = viewAlertInflate.findViewById<ImageButton>(R.id.imageButtonMeasureCubicToMoneyPlus)
        val minusButton = viewAlertInflate.findViewById<ImageButton>(R.id.imageButtonMeasureCubicToMoneyMinus)

        textViewMeasureCubicToMoneyM3.text = m3.toString().replace(".", ",")
        textViewMeasureCubicToMoneyMax.text = numDecimalFormat(baseMaxProgress.toString())

        seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, progress: Int, p2: Boolean) {
                val result = "%.2f".format(progress.toFloat() * multiplier.toFloat() * m3).replace(".", ",")
                textViewMeasureCubicToMoneyResult.text = numFloatFormat(result)
                textViewMeasureCubicToMoneyProgress.text = numDecimalFormat((progress * multiplier).toString())

            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })


        fun setButtonView(index: Int) {
            multiplier = if (multiplier == 1) { index
            } else { if (multiplier == index) { 1 } else { index } }

            multiplierButtons.forEach {
                it.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.button_white_smoke, null))
            }
            if (multiplier > 1) {
                when(multiplier) {
                    10 -> multiplierButtons[0].backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.button_blue, null))
                    100 -> multiplierButtons[1].backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.button_yellow, null))
                    1000 -> multiplierButtons[2].backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.button_red, null))
                }
            }

            textViewMeasureCubicToMoneyMax.text =  numDecimalFormat((multiplier * baseMaxProgress).toString())
            textViewMeasureCubicToMoneyProgress.text = numDecimalFormat((seekBar.progress * multiplier).toString())
            val result = "%.2f".format(seekBar.progress.toFloat() * multiplier.toFloat() * m3).replace(".", ",")
            textViewMeasureCubicToMoneyResult.text = numFloatFormat(result)
        }


        multiplierButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                val optIndex = when(index) {
                    0 -> 10
                    1 -> 100
                    else -> 1000
                }
                setButtonView(optIndex)
            }
        }

        var timer: CountDownTimer? = null
        fun timerCancel() {
            timer?.cancel()
            timer = null
        }
        fun timerStart(threshold: Int, currentImageButton: ImageButton) {
            timer = object : CountDownTimer(100000, 10) {
                override fun onTick(p0: Long) {

                    if (plusButton.isPressed && minusButton.isPressed) { timerCancel() }

                    if (!currentImageButton.isPressed) { timerCancel() }

                    if (threshold == 0) {
                        if (seekBar.progress == seekBar.max) { timerCancel() }
                        seekBar.progress = seekBar.progress + 1
                    }

                    else {
                        if (seekBar.progress == 0) { timerCancel() }
                        seekBar.progress = seekBar.progress - 1
                    }
                }
                override fun onFinish() {}
            }.start()
        }


        plusButton.setOnLongClickListener {
            timerStart(0, it as ImageButton)
            return@setOnLongClickListener  true
        }
        plusButton.setOnClickListener {
            if (seekBar.progress < seekBar.max) {
                seekBar.progress += 1
            }
        }

        minusButton.setOnLongClickListener {
            timerStart(baseMaxProgress, it as ImageButton)
            return@setOnLongClickListener  true
        }
        minusButton.setOnClickListener {
            if (seekBar.progress > 0) {
                seekBar.progress -= 1
            }
        }

        alertDialog.setView(viewAlertInflate)
        alertDialog.setNegativeButton(R.string.cancel){_:DialogInterface, _:Int ->}
        alertDialog.setIcon(context.resources.getDrawable(R.drawable.ic_tree_leafy_green_8, null))
        alertDialog.show()
    }

    private fun numFloatFormat(num: String): String {
        var optNum = num
        when (num.length) {
            in 7..9 -> {
                optNum = num.substring(0, num.length - 6) + " " + num.substring(num.length - 6, num.length)
            }
            in 10..12 -> {
                val subLast = num.substring(num.length - 6, num.length)
                val subCenter = num.substring(num.length - 9, num.length - 6)
                val subFirst = num.substring(0, num.length - 9)
                optNum = "$subFirst $subCenter $subLast"
            }
            in 13..15 -> {
                val subLast = num.substring(num.length - 6, num.length)
                val subCenter1 = num.substring(num.length - 9, num.length - 6)
                val subCenter2 = num.substring(num.length - 12, num.length - 9)
                val subFirst = num.substring(0, num.length - 12)
                optNum = "$subFirst $subCenter2 $subCenter1 $subLast"
            }
            in 16..18 -> {
                val subLast = num.substring(num.length - 6, num.length)
                val subCenter1 = num.substring(num.length - 9, num.length - 6)
                val subCenter2 = num.substring(num.length - 12, num.length - 9)
                val subCenter3 = num.substring(num.length - 15, num.length - 12)
                val subFirst = num.substring(0, num.length - 15)
                optNum = "$subFirst $subCenter3 $subCenter2 $subCenter1 $subLast"
            }
        }
        return optNum
    }

    private fun numDecimalFormat(num: String): String {
        var optNum = num
        when (num.length) {
            in 4..6 -> {
                optNum = num.substring(0, num.length - 3) + " " + num.substring(num.length - 3, num.length)
            }
            in 7..9 -> {
                val subLast = num.substring(num.length - 3, num.length)
                val subCenter = num.substring(num.length - 6, num.length - 3)
                val subFirst = num.substring(0, num.length - 6)
                optNum = "$subFirst $subCenter $subLast"
            }
            in 10..12 -> {
                val subLast = num.substring(num.length - 3, num.length)
                val subCenter1 = num.substring(num.length - 6, num.length - 3)
                val subCenter2 = num.substring(num.length - 9, num.length - 6)
                val subFirst = num.substring(0, num.length - 9)
                optNum = "$subFirst $subCenter2 $subCenter1 $subLast"
            }
            in 13..16 -> {
                val subLast = num.substring(num.length - 3, num.length)
                val subCenter1 = num.substring(num.length - 6, num.length - 3)
                val subCenter2 = num.substring(num.length - 9, num.length - 6)
                val subCenter3 = num.substring(num.length - 12, num.length - 9)
                val subFirst = num.substring(0, num.length - 12)
                optNum = "$subFirst $subCenter3 $subCenter2 $subCenter1 $subLast"
            }
        }
        return optNum
    }


}