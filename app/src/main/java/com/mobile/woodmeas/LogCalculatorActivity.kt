package com.mobile.woodmeas


import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import com.mobile.woodmeas.datamodel.MenuData
import com.mobile.woodmeas.datamodel.MenuItemsType
import com.mobile.woodmeas.datamodel.MenuType
import com.mobile.woodmeas.math.Calculator
import com.mobile.woodmeas.model.*
import com.mobile.woodmeas.viewcontrollers.NavigationManager
import java.util.*
import kotlin.concurrent.thread


class LogCalculatorActivity : AppCompatActivity(), AppActivityManager, PackageManager {

    private var logPackages: WoodenLogPackages? = null

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_calculator)
        NavigationManager.topNavigation(this, MenuData(MenuType.CALCULATORS, MenuItemsType.LOG))
        super.setSpinnerTrees(this)
        super.calculationManager(this)

        loadView()
    }


    override fun onStart() {
        super.onStart()
        if (Settings.PackagesSelect.id > 0) {
            thread {
                DatabaseManagerDao.getDataBase(this)?.let { databaseManagerDao ->
                    databaseManagerDao.woodenLogPackagesDao().selectItem(Settings.PackagesSelect.id).let {
                        logPackages = it
                        Settings.PackagesSelect.id = 0
                        this.runOnUiThread {
                            packageIsActive(true)
                        }
                    }
                }
            }
        }
    }

    override fun loadView() {

        // Go Package List Select Activity
        findViewById<ImageButton>(R.id.imageButtonMeasResultUsePackage).let {imageButtonMeasResultUsePackage ->
            imageButtonMeasResultUsePackage.setOnClickListener {
                thread {
                    DatabaseManagerDao.getDataBase(this)?.let { databaseManagerDao ->
                        if  (databaseManagerDao.woodenLogPackagesDao().countAll() > 0){
                            this.runOnUiThread {
                                val intent = Intent(this, LogPackageListSelectActivity::class.java)
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
                                databaseManagerDao.woodenLogPackagesDao()
                                    .insert(WoodenLogPackages(0, editTextWoodPackages.text.toString(), Date()))

                                databaseManagerDao.woodenLogPackagesDao().selectLast().let {
                                    logPackages = it
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
                                databaseManagerDao.woodenLogPackagesDao()
                                    .insert(WoodenLogPackages(0, editTextWoodPackages.text.toString(), Date()))
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
                logPackages = null
                packageIsActive(false)
            }
        }

        findViewById<ImageButton>(R.id.imageButtonShowPackage).let { imageButtonShowPackage ->
            imageButtonShowPackage.setOnClickListener {
                thread {
                    logPackages?.let { logPackages ->
                        thread {
                            DatabaseManagerDao.getDataBase(this)?.let { databaseManagerDao ->
                                if (databaseManagerDao.woodenLogDao().countWithPackageId(logPackages.id) > 0) {
                                    this.runOnUiThread {
                                        val intent = Intent(this, LogPackageDetailsActivity::class.java)
                                        intent.putExtra(Settings.IntentsPutValues.PACKAGE_ID, logPackages.id)
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

    override fun removeItem(item: Int) {
        TODO("Not yet implemented")
    }

    @SuppressLint("CutPasteId", "UseSwitchCompatOrMaterialCode")
    override fun addItemToPackage() {
        val length          = findViewById<SeekBar>(R.id.seekBarMeasureLength).progress
        val diameter        = findViewById<SeekBar>(R.id.seekBarMeasureDiameter).progress
        val tree: Trees     = findViewById<Spinner>(R.id.spinnerTreeModuleTrees).selectedItem as Trees
        val switchBarkOn    = findViewById<Switch>(R.id.switchTreeModule)

        val treeForCalc: Trees? = if (switchBarkOn.isChecked) { tree } else null

        val calculateResult = Calculator.logFormat(length, diameter, treeForCalc).replace(",", ".")
        val resultCm = (calculateResult.toFloat() * 1000000.0F).toInt()

        thread {
            DatabaseManagerDao.getDataBase(this)?.let { databaseManagerDao ->
                logPackages?.id?.let {
                    databaseManagerDao.woodenLogDao().insert(
                        WoodenLog(
                            0,
                            it,
                            length,
                            diameter,
                            resultCm,
                            tree.id,
                            if (switchBarkOn.isChecked) 1 else 0,
                            Date()
                        )
                    )
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
            findViewById<TextView>(R.id.textViewMeasResultPackageName).text = logPackages?.name
        } else {
            findViewById<TextView>(R.id.textViewMeasResultPackageName).text = applicationContext.getString(R.string.absence)
        }
    }

}