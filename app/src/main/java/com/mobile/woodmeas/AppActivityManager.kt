package com.mobile.woodmeas

import android.annotation.SuppressLint
import android.os.CountDownTimer
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.mobile.woodmeas.controller.TreeAdapter
import com.mobile.woodmeas.datamodel.Measure
import com.mobile.woodmeas.math.Calculator
import com.mobile.woodmeas.model.DatabaseManagerDao
import kotlin.concurrent.thread

interface AppActivityManager {
    fun loadView()
    fun removeItem(item: Int)
    fun goToActivity() {}
    fun goToActivity(id: Int) {}

    @SuppressLint("ResourceAsColor")
    fun setSpinnerTrees(appCompatActivity: AppCompatActivity) {
        thread {
            DatabaseManagerDao.getDataBase(appCompatActivity)?.let { databaseManagerDao ->
                val trees = databaseManagerDao.treesDao().selectAll()
                val treeArrayAdapter = TreeAdapter(appCompatActivity, R.layout.tree_layout_spinner, trees)
                appCompatActivity.runOnUiThread {
                    appCompatActivity.findViewById<Spinner>(R.id.spinnerTreeModuleTrees).apply {
                        adapter = treeArrayAdapter
                    }
                }
            }
        }

        appCompatActivity.findViewById<Switch>(R.id.switchTreeModuleBark).apply {
            setOnClickListener {
                if (this.isChecked) {
                    this.setTextColor(appCompatActivity.resources.getColor(R.color.wm_green_medium))
                }
                else {
                    this.setTextColor(appCompatActivity.resources.getColor(R.color.wm_gray_dark))
                }
            }
        }
    }

    fun calculationManager(appCompatActivity: AppCompatActivity) {

        var timer: CountDownTimer? = null

        fun timerCancel() {
            timer?.cancel()
            timer = null
        }

        fun timerStart(threshold: Int, imageButton: ImageButton, seekBar: SeekBar) {
            timer = object : CountDownTimer(100000, 10) {
                override fun onTick(p0: Long) {
                    if (!imageButton.isPressed) { timerCancel() }

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

        val textViewMeasResultM:TextView = appCompatActivity.findViewById(R.id.textViewMeasResultM)


        val measures: List<Measure> =
            if (appCompatActivity is PlankCalculatorActivity) {
                listOf(
                    Measure.LENGTH,
                    Measure.WIDTH,
                    Measure.THICKNESS
                ) } else listOf()

        val seekBarsValues = IntArray(measures.size)


        fun setResult() {
            if (seekBarsValues.any { it == 0 }) {
                textViewMeasResultM.text = 0.00.toString()
            }

            else {
                if (appCompatActivity is PlankCalculatorActivity) {
                    Calculator
                        .rectangular(seekBarsValues[0], seekBarsValues[1], seekBarsValues[2])
                        .let { result ->
                            "%.2f".format(result.toFloat() /  1000000.00F).let { resultFormat ->
                                textViewMeasResultM.text = resultFormat
                            }
                        }
                }
            }
        }

        measures.forEachIndexed { index, measure ->
            val textViewCm: TextView = appCompatActivity.findViewById(measure.getTextViewCmVal())
            val textViewM: TextView = appCompatActivity.findViewById(measure.getTextViewMVal())


            val seekBar: SeekBar = appCompatActivity.findViewById(measure.getSeekBar())
            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
                @SuppressLint("SetTextI18n")
                override fun onProgressChanged(p0: SeekBar?, progress: Int, p2: Boolean) {
                    seekBarsValues[index] = progress
                    textViewCm.text = progress.toString()
                    textViewM.text = "%.2f".format(progress.toFloat() /  100.00F)
                    setResult()
                }
                override fun onStartTrackingTouch(p0: SeekBar?) {}
                override fun onStopTrackingTouch(p0: SeekBar?) {}
            })

            // Plus Image Button
            appCompatActivity.findViewById<ImageButton>(measure.getImageButtonPlus()).apply {
                setOnClickListener {
                    if(seekBar.progress < seekBar.max) {
                        seekBar.progress = seekBar.progress + 1
                    }
                }

                setOnLongClickListener {
                    timerStart(0, it as ImageButton, seekBar)
                    return@setOnLongClickListener true
                }
            }


            // Minus Image Button
            appCompatActivity.findViewById<ImageButton>(measure.getImageButtonMinus()).apply {
                setOnClickListener {
                    if(seekBar.progress > 0) {
                        seekBar.progress = seekBar.progress - 1
                    }
                }

                setOnLongClickListener {
                    timerStart(seekBar.max, it as ImageButton, seekBar)
                    return@setOnLongClickListener true
                }
            }

        }
    }
}