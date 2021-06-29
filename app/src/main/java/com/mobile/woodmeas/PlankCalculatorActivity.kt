package com.mobile.woodmeas

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import com.mobile.woodmeas.datamodel.MenuData
import com.mobile.woodmeas.datamodel.MenuItemsType
import com.mobile.woodmeas.datamodel.MenuType
import com.mobile.woodmeas.datamodel.UnitsMeasurement
import com.mobile.woodmeas.math.Calculator
import com.mobile.woodmeas.model.*
import com.mobile.woodmeas.viewcontrollers.NavigationManager
import java.util.*
import kotlin.concurrent.thread

class PlankCalculatorActivity : AppCompatActivity(), AppActivityManager, PackageManager {
    private var plankPackages: PlankPackages? = null
    private var unitsMeasurement = UnitsMeasurement.CM
    private lateinit var textViewMultiplierPanelVal: TextView
    private var multiplierVal = 1
    private lateinit var imageButtonMultiplierPanelMinus: ImageButton
    private lateinit var imageButtonMultiplierPanelPlus: ImageButton
    private lateinit var seekBarMultiplier: SeekBar

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plank_calculator)
        NavigationManager.topNavigation(this, MenuData(MenuType.CALCULATORS, MenuItemsType.PLANK))
        super.setSpinnerTrees(this)
        textViewMultiplierPanelVal = findViewById(R.id.textViewMultiplierPanelVal)
        imageButtonMultiplierPanelMinus = findViewById(R.id.imageButtonMultiplierPanelMinus)
        imageButtonMultiplierPanelPlus = findViewById(R.id.imageButtonMultiplierPanelPlus)
        seekBarMultiplier = findViewById(R.id.seekBarMultiplierPanel)

        thread {
            DatabaseManagerDao.getDataBase(this)?.let { databaseManagerDao ->
                unitsMeasurement  = databaseManagerDao.settingsDbDao().select().getUnitMeasurement()
            }
            this.runOnUiThread { super.calculationManager(this, unitsMeasurement) }
        }
        loadView()

        seekBarMultiplier.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, progress: Int, p2: Boolean) {
                multiplierVal = progress + 1
                val multiplierValTextFormat = "x$multiplierVal"
                textViewMultiplierPanelVal.text = multiplierValTextFormat
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

        imageButtonMultiplierPanelPlus.setOnClickListener {
            if (seekBarMultiplier.progress < seekBarMultiplier.max) {
                seekBarMultiplier.progress = seekBarMultiplier.progress + 1
            }
        }

        imageButtonMultiplierPanelPlus.setOnLongClickListener {
            var timer: CountDownTimer? = null
            fun timerCancel() {
                timer?.cancel()
                timer = null
            }
            timer = object : CountDownTimer(1000000, 10) {
                override fun onTick(p0: Long) {
                    if (imageButtonMultiplierPanelMinus.isPressed && imageButtonMultiplierPanelPlus.isPressed) {
                        timerCancel()
                    }

                    if (!imageButtonMultiplierPanelPlus.isPressed) { timerCancel() }

                    if (seekBarMultiplier.progress < seekBarMultiplier.max) {
                        seekBarMultiplier.progress = seekBarMultiplier.progress + 1
                    }
                    else {timerCancel()}
                }
                override fun onFinish() {}
            }.start()
            return@setOnLongClickListener true
        }

        imageButtonMultiplierPanelMinus.setOnClickListener {
            if (seekBarMultiplier.progress > 0) {
                seekBarMultiplier.progress = seekBarMultiplier.progress - 1
            }
        }

        imageButtonMultiplierPanelMinus.setOnLongClickListener {
            var timer: CountDownTimer? = null
            fun timerCancel() {
                timer?.cancel()
                timer = null
            }
            timer = object : CountDownTimer(1000000, 10) {
                override fun onTick(p0: Long) {
                    if (imageButtonMultiplierPanelMinus.isPressed && imageButtonMultiplierPanelPlus.isPressed) {
                        timerCancel()
                    }

                    if (!imageButtonMultiplierPanelMinus.isPressed) { timerCancel() }

                    if (seekBarMultiplier.progress > 0) {
                        seekBarMultiplier.progress = seekBarMultiplier.progress - 1
                    }
                    else { timerCancel() }
                }
                override fun onFinish() {}
            }.start()
            return@setOnLongClickListener true
        }
    }

    override fun onStart() {
        super.onStart()
        if (Settings.PackagesSelect.id > 0) {
            thread {
            DatabaseManagerDao.getDataBase(this)?.let { databaseManagerDao ->
                databaseManagerDao.plankPackagesDao().selectItem(Settings.PackagesSelect.id).let {
                    plankPackages = it
                    Settings.PackagesSelect.id = 0
                    this.runOnUiThread { packageIsActive(true) }
                    }
                }
            }
        }
    }


    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun loadView() {

        (findViewById<Switch>(R.id.switchTreeModule).parent as ConstraintLayout).visibility = View.GONE

        // Go Package List Select Activity
       findViewById<ImageButton>(R.id.imageButtonMeasResultUsePackage).let {imageButtonMeasResultUsePackage ->
           imageButtonMeasResultUsePackage.setOnClickListener {
               thread {
                   DatabaseManagerDao.getDataBase(this)?.let { databaseManagerDao ->
                       if  (databaseManagerDao.plankPackagesDao().countAll() > 0){
                           this.runOnUiThread {
                               val intent = Intent(this, PlankPackageListSelectActivity::class.java)
                               intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                               startActivity(intent)
                           }
                       } else {
                           this.runOnUiThread {
                               val alertDialog = AlertDialog.Builder(this)
                               alertDialog.setTitle(R.string.no_packages)
                               alertDialog.setNegativeButton(R.string.ok) {_: DialogInterface, _:Int ->}
                               alertDialog.show()
                           }
                       }
                   }
               }
           }
       }

       findViewById<ImageButton>(R.id.imageButtonMeasResultAddPackage).let { imageButtonMeasResultUsePackage ->
           imageButtonMeasResultUsePackage.setOnClickListener {
               val alertDialog = AlertDialog.Builder(this)
               alertDialog.setTitle(R.string.create_package)
               val viewAlertInflate: View = View.inflate(this, R.layout.add_wood_package, null)
               val editTextWoodPackages: EditText = viewAlertInflate.findViewById(R.id.editTextWoodPackageName)
               alertDialog.setView(viewAlertInflate)

               alertDialog.setPositiveButton(R.string.create_and_use) {_: DialogInterface, _: Int ->
                    if (editTextWoodPackages.text.toString().isNotEmpty()) {
                        thread {
                            DatabaseManagerDao.getDataBase(this)?.let { databaseManagerDao ->
                                databaseManagerDao.plankPackagesDao()
                                    .insert(PlankPackages(0, editTextWoodPackages.text.toString(), Date(), null))

                                databaseManagerDao.plankPackagesDao().selectLast().let {
                                    plankPackages = it
                                    this.runOnUiThread {
                                        packageIsActive(true)
                                        Toast.makeText(this, R.string.created_package, Toast.LENGTH_SHORT)
                                            .show()
                                    }
                                }
                            }
                        }
                    }
               }

               alertDialog.setNegativeButton(R.string.create) {_: DialogInterface, _: Int ->
                   if (editTextWoodPackages.text.toString().isNotEmpty()) {
                       thread {
                           DatabaseManagerDao.getDataBase(this)?.let { databaseManagerDao ->
                               databaseManagerDao.plankPackagesDao()
                                   .insert(PlankPackages(0, editTextWoodPackages.text.toString(), Date(), null))
                               this.runOnUiThread {
                                   Toast.makeText(this, R.string.created_package, Toast.LENGTH_SHORT)
                                       .show()
                               }
                           }
                       }
                   }
               }
               alertDialog.setNeutralButton(R.string.cancel) {_: DialogInterface, _: Int -> }
               alertDialog.show()

           }
       }

       findViewById<ImageButton>(R.id.imageButtonMeasResultDeletePackage).apply {
           setOnClickListener {
               plankPackages = null
               packageIsActive(false)
           }
       }

        findViewById<ImageButton>(R.id.imageButtonShowPackage).let { imageButtonShowPackage ->
            imageButtonShowPackage.setOnClickListener {
                thread {
                    plankPackages?.let { plankPackages ->
                        thread {
                            DatabaseManagerDao.getDataBase(this)?.let { databaseManagerDao ->
                                if (databaseManagerDao.plankDao().countWithPackageId(plankPackages.id) > 0) {
                                    this.runOnUiThread {
                                        val intent = Intent(this, PlankPackageDetailsActivity::class.java)
                                        intent.putExtra(Settings.IntentsPutValues.PACKAGE_ID, plankPackages.id)
                                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                        startActivity(intent)
                                    }
                                }
                                else {
                                    this.runOnUiThread {
                                        val alertDialog = AlertDialog.Builder(this)
                                        alertDialog.setTitle(R.string.package_is_empty)
                                        alertDialog.setNegativeButton(R.string.ok) {_:DialogInterface, _:Int ->}
                                        alertDialog.show()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun removeItem(item: Int) {}

    override fun addItemToPackage() {
        val length = findViewById<SeekBar>(R.id.seekBarMeasureLength).progress
        val width = findViewById<SeekBar>(R.id.seekBarMeasureWidth).progress
        val height = findViewById<SeekBar>(R.id.seekBarMeasureThickness).progress
        val tree: Trees = findViewById<Spinner>(R.id.spinnerTreeModuleTrees).selectedItem as Trees

        thread {
            DatabaseManagerDao.getDataBase(this)?.let { databaseManagerDao ->
                plankPackages?.id?.let { plankPackagesId ->
                    for (i in 1..multiplierVal) {
                        databaseManagerDao.plankDao().insert(
                            Plank(
                                0,
                                plankPackagesId,
                                length,
                                width,
                                height,
                                Calculator.rectangular(length, width, height),
                                tree.id,
                                Date()
                            )
                        )
                    }
                    this.runOnUiThread {
                        Toast.makeText(this, R.string.added_to_package, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun packageIsActive(on: Boolean) {
        findViewById<ConstraintLayout>(R.id.constraintLayoutPackageWrapper)
            .alpha = if (on) 1.0F else 0.5F
        findViewById<ImageButton>(R.id.imageButtonShowPackage).isEnabled = on
        if (on) {
            findViewById<TextView>(R.id.textViewMeasResultPackageName).text = plankPackages?.name
        } else {
            findViewById<TextView>(R.id.textViewMeasResultPackageName).text = applicationContext.getString(R.string.absence)
        }
    }

}