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
import com.mobile.woodmeas.datamodel.UnitsMeasurement
import com.mobile.woodmeas.math.Calculator
import com.mobile.woodmeas.model.*
import com.mobile.woodmeas.model.Stack
import com.mobile.woodmeas.viewcontrollers.NavigationManager
import java.util.*
import kotlin.concurrent.thread

class StackCalculatorActivity : AppCompatActivity(), AppActivityManager, PackageManager {
    private var stackPackages: StackPackages? = null
    private var unitsMeasurement = UnitsMeasurement.CM

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stack_calculator)
        NavigationManager.topNavigation(this, MenuData(MenuType.CALCULATORS, MenuItemsType.STACK))

        findViewById<SeekBar>(R.id.seekBarMeasureWidth).max = resources.getInteger(R.integer.max_log_width_long)
        findViewById<SeekBar>(R.id.seekBarMeasureThickness).max = resources.getInteger(R.integer.max_log_width_long)

        super.setSpinnerTrees(this)
        super.setSpinnerWoodCoefficients(this)
        thread {
            DatabaseManagerDao.getDataBase(this)?.let { databaseManagerDao ->
                unitsMeasurement  = databaseManagerDao.settingsDbDao().select().getUnitMeasurement()
            }
            this.runOnUiThread { super.calculationManager(this, unitsMeasurement) }
        }
        loadView()
    }

    override fun onStart() {
        super.onStart()
        if (Settings.PackagesSelect.id > 0) {
            thread {
                DatabaseManagerDao.getDataBase(this)?.let { databaseManagerDao ->
                    databaseManagerDao.stackPackagesDao().selectItem(Settings.PackagesSelect.id).let {
                        stackPackages = it
                        Settings.PackagesSelect.id = 0
                        this.runOnUiThread { packageIsActive(true) }
                    }
                }
            }
        }
    }

    override fun loadView() {

        // Go Package List Select Activity
        findViewById<ImageButton>(R.id.imageButtonMeasResultUsePackage).let { imageButtonMeasResultUsePackage ->
            imageButtonMeasResultUsePackage.setOnClickListener {
                thread {
                    DatabaseManagerDao.getDataBase(this)?.let { databaseManagerDao ->
                        if  (databaseManagerDao.stackPackagesDao().countAll() > 0){
                            this.runOnUiThread {
                                val intent = Intent(this, StackPackageListSelectActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                startActivity(intent)
                            }
                        } else {
                            this.runOnUiThread {
                                val alertDialog = AlertDialog.Builder(this)
                                alertDialog.setTitle(R.string.no_packages)
                                alertDialog.setNegativeButton(R.string.ok) { _: DialogInterface, _:Int ->}
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
                                databaseManagerDao.stackPackagesDao()
                                    .insert(StackPackages(0, editTextWoodPackages.text.toString(), Date()))

                                databaseManagerDao.stackPackagesDao().selectLast().let {
                                    stackPackages = it
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
                                databaseManagerDao.stackPackagesDao()
                                    .insert(StackPackages(0, editTextWoodPackages.text.toString(), Date()))
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
                stackPackages = null
                packageIsActive(false)
            }
        }

        findViewById<ImageButton>(R.id.imageButtonShowPackage).let { imageButtonShowPackage ->
            imageButtonShowPackage.setOnClickListener {
                thread {
                    stackPackages?.let { plankPackages ->
                        thread {
                            DatabaseManagerDao.getDataBase(this)?.let { databaseManagerDao ->
                                if (databaseManagerDao.stackDao().countWithPackageId(plankPackages.id) > 0) {
                                    this.runOnUiThread {
                                        val intent = Intent(this, StackPackageDetailsActivity::class.java)
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

    override fun removeItem(item: Int) {
        TODO("Not yet implemented")
    }

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun addItemToPackage() {
        val length = findViewById<SeekBar>(R.id.seekBarMeasureLength).progress
        val width = findViewById<SeekBar>(R.id.seekBarMeasureWidth).progress
        val height = findViewById<SeekBar>(R.id.seekBarMeasureThickness).progress
        val tree: Trees = findViewById<Spinner>(R.id.spinnerTreeModuleTrees).selectedItem as Trees
        val switchCrossStackModule = findViewById<Switch>(R.id.switchCrossStackModule)
        val cross = if (switchCrossStackModule.isChecked) 1 else 0
        val calculateResult = Calculator.stackFormat(length, width, height, null, switchCrossStackModule.isChecked).replace(",", ".")
        val resultCm = (calculateResult.toFloat() * 1000000.0F).toInt()

        thread {
            DatabaseManagerDao.getDataBase(this)?.let { databaseManagerDao ->
                stackPackages?.id?.let {
                    databaseManagerDao.stackDao().insert(
                        Stack(
                            0,
                            it,
                            length,
                            width,
                            height,
                            resultCm,
                            cross,
                            tree.id,
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
            findViewById<TextView>(R.id.textViewMeasResultPackageName).text = stackPackages?.name
        } else {
            findViewById<TextView>(R.id.textViewMeasResultPackageName).text = applicationContext.getString(R.string.absence)
        }
    }
}