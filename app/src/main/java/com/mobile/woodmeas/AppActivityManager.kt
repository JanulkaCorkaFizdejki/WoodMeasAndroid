package com.mobile.woodmeas

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.media.MediaPlayer
import android.os.Build
import android.os.CountDownTimer
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.mobile.woodmeas.controller.TreeAdapter
import com.mobile.woodmeas.controller.WoodCoefficientsAdapter
import com.mobile.woodmeas.datamodel.Measure
import com.mobile.woodmeas.datamodel.UnitsMeasurement
import com.mobile.woodmeas.datamodel.WoodCoefficients
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
    }

    fun setSpinnerWoodCoefficients(appCompatActivity: AppCompatActivity) {
        val listWoodCoefficients:MutableList<WoodCoefficients> = mutableListOf()
        WoodCoefficients.values().forEach {
            listWoodCoefficients.add(it)
        }
        val woodCoefficientsAdapter = WoodCoefficientsAdapter(appCompatActivity, R.layout.wood_coeffictients_layout_spinner, listWoodCoefficients)
        appCompatActivity.findViewById<Spinner>(R.id.spinnerWoodCoefficients).adapter = woodCoefficientsAdapter
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("UseSwitchCompatOrMaterialCode", "ResourceAsColor",
        "UseCompatLoadingForDrawables", "CutPasteId"
    )
    fun calculationManager(appCompatActivity: AppCompatActivity, unitsMeasurement: UnitsMeasurement) {

        Settings.PackagesSelect.id = 0

        val textViewMeasResultM:TextView = appCompatActivity.findViewById(R.id.textViewMeasResultM)

        if (unitsMeasurement == UnitsMeasurement.IN) {
            appCompatActivity.findViewById<TextView>(R.id.textViewMeasResultMUnit).text = unitsMeasurement.getNameUnitCubic(appCompatActivity)
        }

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
                is StackCalculatorActivity -> {
                    listOf(
                        Measure.LENGTH,
                        Measure.WIDTH,
                        Measure.THICKNESS
                    )
                }
                else -> listOf()
            }

        val seekBarsValues = IntArray(measures.size)

        @SuppressLint("SetTextI18n")
        fun setResult() {
            if (seekBarsValues.any { it == 0 }) {
                textViewMeasResultM.text = appCompatActivity.getString(R.string.default_number_float)
            }

            else {
                when (appCompatActivity) {
                    is PlankCalculatorActivity -> {
                        Calculator
                            .rectangular(seekBarsValues[0], seekBarsValues[1], seekBarsValues[2])
                            .let { result ->
                                "%.2f".format(result.toFloat() /  1000000.00F).let { resultFormat ->
                                    val textFormat = if(unitsMeasurement == UnitsMeasurement.CM) {
                                        resultFormat.replace(".", ",")
                                    } else {
                                        UnitsMeasurement.convertToFootToString(resultFormat.replace(",", ".").toFloat())
                                    }
                                    textViewMeasResultM.text = textFormat
                                }
                            }
                    }
                    is LogCalculatorActivity -> {
                        val tree: Trees? =
                            if (appCompatActivity.findViewById<Switch>(R.id.switchTreeModule).isChecked) { appCompatActivity.findViewById<Spinner>(R.id.spinnerTreeModuleTrees).selectedItem as Trees } else null

                        if (appCompatActivity.getLogMeasUpperMethod()) {
                            Calculator.thicknessUpperMethod(seekBarsValues[0].toDouble() / 100.0, seekBarsValues[1]).let { result ->
                                if (unitsMeasurement == UnitsMeasurement.CM) {
                                    val resultFormat = "%.2f".format(result).replace(".", ",")
                                    textViewMeasResultM.text = resultFormat
                                }
                                else {
                                    val resultToFloat = "%.2f".format(result).replace(",", ".").toFloat()
                                    textViewMeasResultM.text = UnitsMeasurement.convertToFootToString(resultToFloat)
                                }
                            }
                        }
                        else {
                            Calculator.logFormat(seekBarsValues[0], seekBarsValues[1], tree).let { result ->
                                if (unitsMeasurement == UnitsMeasurement.CM) {
                                    textViewMeasResultM.text = result.replace(".", ",")
                                }
                                else {
                                    textViewMeasResultM.text  = UnitsMeasurement.convertToFootToString(result.replace(",", ".").toFloat())
                                }
                            }
                        }
                    }
                    is StackCalculatorActivity -> {
                        val m3 = "%.2f".format(Calculator
                            .rectangularToLong(seekBarsValues[0], seekBarsValues[1], seekBarsValues[2]).toFloat() / 1000000.00F)
                            .replace(",", ".").toFloat()

                        val woodCoefficients = appCompatActivity.findViewById<Spinner>(R.id.spinnerWoodCoefficients).selectedItem as WoodCoefficients
                        val switchCrossStackModule = appCompatActivity.findViewById<Switch>(R.id.switchCrossStackModule).isChecked
                        val barkOn = appCompatActivity.findViewById<Switch>(R.id.switchTreeModule).isChecked
                        val tree = appCompatActivity.findViewById<Spinner>(R.id.spinnerTreeModuleTrees).selectedItem as Trees
                        val customFactor: Float? = if  (appCompatActivity.findViewById<Switch>(R.id.switchCustomFactorOnOff).isChecked) {
                            val progress = appCompatActivity.findViewById<SeekBar>(R.id.seekBarCustomFactor).progress
                            when(appCompatActivity.findViewById<SeekBar>(R.id.seekBarCustomFactor).progress) {
                                0 -> 0.01F
                                in 0..10 ->  "0.0$progress".toFloat()
                                100 -> 1.00F
                                else -> "0.$progress".toFloat()
                            }
                        } else null

                        val result = woodCoefficients.setResult(m3, seekBarsValues[0], barkOn, tree, switchCrossStackModule, customFactor)
                        val resultFormat = if(unitsMeasurement == UnitsMeasurement.CM)
                            { "%.2f".format(result).replace(".", ",") }
                            else { UnitsMeasurement.convertToFootToString(result) }
                        textViewMeasResultM.text = resultFormat
                    }
                }
            }
        }

        measures.forEachIndexed { index, measure ->
            val imageButtonPlus: ImageButton = appCompatActivity.findViewById(measure.getImageButtonPlus())
            val imageButtonMinus: ImageButton = appCompatActivity.findViewById(measure.getImageButtonMinus())

            val textViewCm: TextView = appCompatActivity.findViewById(measure.getTextViewCmVal())
            val textViewM: TextView = appCompatActivity.findViewById(measure.getTextViewMVal())

            if (unitsMeasurement == UnitsMeasurement.IN) {
                textViewCm.text = appCompatActivity.resources.getText(R.string.default_number_float)
                appCompatActivity.findViewById<TextView>(measure.getUnitCm()).text = unitsMeasurement.getNameUnit(appCompatActivity)
                appCompatActivity.findViewById<TextView>(measure.getUnitM()).text = unitsMeasurement.getNameUnitCubic(appCompatActivity)
            }

            val seekBar: SeekBar = appCompatActivity.findViewById(measure.getSeekBar())

            var timer: CountDownTimer? = null
            fun timerCancel() {
                timer?.cancel()
                timer = null
            }
            fun timerStart(threshold: Int, currentImageButton: ImageButton, countdownInterval: Long = 10) {
                timer = object : CountDownTimer(1000000, countdownInterval) {
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
                    val textFormatCm = if(unitsMeasurement == UnitsMeasurement.CM) {
                        "$progress"
                    } else { UnitsMeasurement.convertToInchToString(progress) }

                    textViewCm.text = textFormatCm

                    val textFormatM = if(unitsMeasurement == UnitsMeasurement.CM) {
                        "%.2f".format(progress.toFloat() /  100.00F).replace(".", ",")
                    } else {
                        UnitsMeasurement
                            .convertToFootToString("%.2f"
                                .format(progress.toFloat() /  100.00F)
                                .replace(",", ".")
                                .toFloat())
                    }

                    textViewM.text = textFormatM
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
                    if (measure == Measure.LENGTH) {
                        timerStart(0, this, 1)
                    } else {timerStart(0, this)}

                    return@setOnLongClickListener true
                }
            }

            imageButtonMinus.apply {
                setOnClickListener {
                    if(seekBar.progress > 0) { seekBar.progress = seekBar.progress - 1 }
                }
                setOnLongClickListener {
                    if (measure == Measure.LENGTH) {
                        timerStart(seekBar.max, this, 1)
                    } else {timerStart(seekBar.max, this)}
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

        if (appCompatActivity is LogCalculatorActivity || appCompatActivity is StackCalculatorActivity) {
            appCompatActivity.findViewById<Switch>(R.id.switchTreeModule).let { switch ->
                switch.setOnCheckedChangeListener { _, boolean ->
                    appCompatActivity.findViewById<ImageView>(R.id.imageViewTreePanelOnOffIco)
                        .setImageDrawable(
                            if (boolean)
                                appCompatActivity.getDrawable(R.drawable.ic_bark_on_10_green)
                            else appCompatActivity.getDrawable(R.drawable.ic_bark_off_10)
                        )
                    setResult()
                }
            }


                appCompatActivity.findViewById<Spinner>(R.id.spinnerTreeModuleTrees)
                    .onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        setResult()
                    }
                    override fun onNothingSelected(p0: AdapterView<*>?) {}
                }

        }

        // Set stack cross panel
        if (appCompatActivity is StackCalculatorActivity) {
            appCompatActivity.findViewById<Spinner>(R.id.spinnerWoodCoefficients)
                .onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    setResult()
                }
                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }

            appCompatActivity.findViewById<Switch>(R.id.switchCrossStackModule).let { switch ->
                switch.setOnCheckedChangeListener { _, boolean ->
                    appCompatActivity.findViewById<ImageView>(R.id.imageViewStackCrossPanelOnOffIco)
                        .setImageDrawable(
                            if (boolean)
                                appCompatActivity.getDrawable(R.drawable.ic_cross_on_10_green)
                            else appCompatActivity.getDrawable(R.drawable.ic_cross_off_10)
                        )
                   setResult()
                }
            }

            appCompatActivity.findViewById<SeekBar>(R.id.seekBarCustomFactor).isEnabled = false
            val textViewCustomFactorValue = appCompatActivity.findViewById<TextView>(R.id.textViewCustomFactorValue)

            appCompatActivity.findViewById<Switch>(R.id.switchCustomFactorOnOff).setOnCheckedChangeListener { _, boolean ->
                if (boolean) {
                    textViewCustomFactorValue.alpha = 1.0F
                    appCompatActivity.findViewById<SeekBar>(R.id.seekBarCustomFactor).isEnabled = true
                    appCompatActivity.findViewById<Spinner>(R.id.spinnerWoodCoefficients).apply {
                        isEnabled = false
                        alpha = 0.5F
                    }
                } else {
                    textViewCustomFactorValue.alpha = 0.5F
                    appCompatActivity.findViewById<SeekBar>(R.id.seekBarCustomFactor).isEnabled = false
                    appCompatActivity.findViewById<Spinner>(R.id.spinnerWoodCoefficients).apply {
                        isEnabled = true
                        alpha = 1.0F
                    }
                }
                setResult()
            }

            appCompatActivity.findViewById<SeekBar>(R.id.seekBarCustomFactor).setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
                override fun onProgressChanged(p0: SeekBar?, progress: Int, p2: Boolean) {
                    val customFactorValue = when (progress) {
                        0 ->  "0,01"
                        in 1..10 -> "0,0$progress"
                        100 ->  "1,00"
                        else -> "0,$progress"
                    }
                    textViewCustomFactorValue.text = customFactorValue
                    setResult()
                }
                override fun onStartTrackingTouch(p0: SeekBar?) {}
                override fun onStopTrackingTouch(p0: SeekBar?) {}
            })
        }
    }
}