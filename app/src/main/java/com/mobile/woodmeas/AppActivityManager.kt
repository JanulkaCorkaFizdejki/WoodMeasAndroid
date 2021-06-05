package com.mobile.woodmeas

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.media.MediaPlayer
import android.os.CountDownTimer
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.mobile.woodmeas.controller.TreeAdapter
import com.mobile.woodmeas.datamodel.Measure
import com.mobile.woodmeas.math.Calculator
import com.mobile.woodmeas.model.DatabaseManagerDao
import com.mobile.woodmeas.model.Settings
import com.mobile.woodmeas.model.Trees
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

        Settings.PackagesSelect.id = 0

        val textViewMeasResultM:TextView = appCompatActivity.findViewById(R.id.textViewMeasResultM)

        val measures: List<Measure> =
            when (appCompatActivity) {
                is PlankCalculatorActivity -> {
                    listOf(
                        Measure.LENGTH,
                        Measure.WIDTH,
                        Measure.THICKNESS
                    ) }
                is LogCalculatorActivity -> {
                    listOf(
                        Measure.LENGTH,
                        Measure.DIAMETER
                    ) }
                else -> listOf()
            }

        val seekBarsValues = IntArray(measures.size)

        fun setResult() {
            if (seekBarsValues.any { it == 0 }) {
                textViewMeasResultM.text = appCompatActivity.getString(R.string.default_number_float)
            }

            else {
                if (appCompatActivity is PlankCalculatorActivity) {
                    Calculator
                        .rectangular(seekBarsValues[0], seekBarsValues[1], seekBarsValues[2])
                        .let { result ->
                            "%.2f".format(result.toFloat() /  1000000.00F).let { resultFormat ->
                                textViewMeasResultM.text = resultFormat.replace(".", ",")
                            }
                        }
                }
                else if (appCompatActivity is LogCalculatorActivity) {
                    val tree: Trees? =
                        if (appCompatActivity.findViewById<Switch>(R.id.switchTreeModuleBark).isChecked)
                            { appCompatActivity.findViewById<Spinner>(R.id.spinnerTreeModuleTrees).selectedItem as Trees }
                        else null

                    Calculator.logFormat(seekBarsValues[0], seekBarsValues[1], tree).let { result ->
                        textViewMeasResultM.text = result.replace(".", ",")
                    }
                }
            }
        }

        measures.forEachIndexed { index, measure ->
            val imageButtonPlus: ImageButton = appCompatActivity.findViewById(measure.getImageButtonPlus())
            val imageButtonMinus: ImageButton = appCompatActivity.findViewById(measure.getImageButtonMinus())

            val textViewCm: TextView = appCompatActivity.findViewById(measure.getTextViewCmVal())
            val textViewM: TextView = appCompatActivity.findViewById(measure.getTextViewMVal())

            val seekBar: SeekBar = appCompatActivity.findViewById(measure.getSeekBar())

            var timer: CountDownTimer? = null
            fun timerCancel() {
                timer?.cancel()
                timer = null
            }
            fun timerStart(threshold: Int, currentImageButton: ImageButton) {
                timer = object : CountDownTimer(100000, 10) {
                    override fun onTick(p0: Long) {

                        if (imageButtonPlus.isPressed && imageButtonMinus.isPressed) { timerCancel() }

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

            imageButtonPlus.apply {
                setOnClickListener {
                    if(seekBar.progress < seekBar.max) { seekBar.progress = seekBar.progress + 1 }
                }
                setOnLongClickListener {
                    timerStart(0, this)
                    return@setOnLongClickListener true
                }
            }

            imageButtonMinus.apply {
                setOnClickListener {
                    if(seekBar.progress > 0) { seekBar.progress = seekBar.progress - 1 }
                }
                setOnLongClickListener {
                    timerStart(seekBar.max, this)
                    return@setOnLongClickListener true
                }
            }
        }

        val constraintLayoutPackageWrapper: ConstraintLayout = appCompatActivity.findViewById(R.id.constraintLayoutPackageWrapper)


        appCompatActivity.findViewById<Button>(R.id.buttonAddToPackage).apply {
            setOnLongClickListener {
                val alphaConstraintLayout = (constraintLayoutPackageWrapper.alpha * 100).toInt()
                if (alphaConstraintLayout < 100) {
                    val alertDialog = AlertDialog.Builder(appCompatActivity)
                    alertDialog.setTitle(R.string.select_or_create_wood_package)
                    alertDialog.setPositiveButton(R.string.ok) {_:DialogInterface, _: Int ->}
                    alertDialog.show()
                    return@setOnLongClickListener true
                }

                if (seekBarsValues.any { it == 0 }) {
                    val alertDialog = AlertDialog.Builder(appCompatActivity)
                    alertDialog.setTitle(R.string.enter_all_dimensions)
                    alertDialog.setPositiveButton(R.string.ok) {_:DialogInterface, _: Int ->}
                    alertDialog.show()
                    return@setOnLongClickListener true
                }

                MediaPlayer.create(appCompatActivity, R.raw.bleep).start()

                if (appCompatActivity is PackageManager) {
                    appCompatActivity.addItemToPackage()
                }

                return@setOnLongClickListener true
            }
        }

        if (appCompatActivity is LogCalculatorActivity) {
            appCompatActivity.findViewById<Switch>(R.id.switchTreeModuleBark).setOnClickListener { setResult() }
            appCompatActivity.findViewById<Spinner>(R.id.spinnerTreeModuleTrees)
                .onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    setResult()
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
        }

    }
}