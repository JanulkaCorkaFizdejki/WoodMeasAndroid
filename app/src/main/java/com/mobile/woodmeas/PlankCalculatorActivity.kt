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

class PlankCalculatorActivity : AppCompatActivity(), AppActivityManager, PackageManager {
    private var plankPackages: PlankPackages? = null

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plank_calculator)
        NavigationManager.topNavigation(this, MenuData(MenuType.CALCULATORS, MenuItemsType.PLANK))
        super.setSpinnerTrees(this)
        super.calculationManager(this)
        loadView()
    }

    override fun onStart() {
        super.onStart()
        if (Settings.PackagesSelect.id > 0) {
            thread {
            DatabaseManagerDao.getDataBase(this)?.let { databaseManagerDao ->
                databaseManagerDao.plankPackagesDao().selectItem(Settings.PackagesSelect.id).let {
                    plankPackages = it
                    Settings.PackagesSelect.id = 0
                    this.runOnUiThread {
                        packageIsActive(true)
                        }
                    }
                }
            }
        }
    }


    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun loadView() {

        val switchBark = findViewById<Switch>(R.id.switchTreeModuleBark)
        switchBark.visibility = View.GONE

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
                                    .insert(PlankPackages(0, editTextWoodPackages.text.toString(), Date()))

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
                                   .insert(PlankPackages(0, editTextWoodPackages.text.toString(), Date()))
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