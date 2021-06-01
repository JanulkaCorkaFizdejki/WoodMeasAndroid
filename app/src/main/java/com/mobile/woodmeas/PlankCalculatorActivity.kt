package com.mobile.woodmeas

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Switch
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import com.mobile.woodmeas.datamodel.MenuData
import com.mobile.woodmeas.datamodel.MenuItemsType
import com.mobile.woodmeas.datamodel.MenuType
import com.mobile.woodmeas.viewcontrollers.NavigationManager

class PlankCalculatorActivity : AppCompatActivity(), AppActivityManager, PackageManager {

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plank_calculator)
        NavigationManager.topNavigation(this, MenuData(MenuType.CALCULATORS, MenuItemsType.PLANK))
        super.setSpinnerTrees(this)
        super.calculationManager(this)
        loadView()
    }

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun loadView() {

        val switchBark = findViewById<Switch>(R.id.switchTreeModuleBark)
        switchBark.visibility = View.GONE

        val constraintLayoutPackageWrapper: ConstraintLayout = findViewById(R.id.constraintLayoutPackageWrapper)

       findViewById<ImageButton>(R.id.imageButtonMeasResultAddPackage).apply {
           setOnClickListener {
               constraintLayoutPackageWrapper.alpha = 1.0F
           }
       }

       findViewById<ImageButton>(R.id.imageButtonMeasResultUsePackage).let { imageButtonMeasResultUsePackage ->
           imageButtonMeasResultUsePackage.setOnClickListener {
               val alertDialog = AlertDialog.Builder(this)
               alertDialog.setTitle(R.string.create_package)
               val viewAlertInflate: View = View.inflate(this, R.layout.add_wood_package, null)
               val editTextWoodPackages: EditText = viewAlertInflate.findViewById(R.id.editTextWoodPackageName)
               alertDialog.setView(viewAlertInflate)

               alertDialog.setPositiveButton(R.string.create_and_use) {_: DialogInterface, _: Int ->
                    if (editTextWoodPackages.text.toString().isNotEmpty()) {

                    }
               }

               alertDialog.setNegativeButton(R.string.create) {_: DialogInterface, _: Int ->
                   if (editTextWoodPackages.text.toString().isNotEmpty()) {

                   }
               }

               alertDialog.setNeutralButton(R.string.cancel) {_: DialogInterface, _: Int -> }
               alertDialog.show()

           }
       }

       findViewById<ImageButton>(R.id.imageButtonMeasResultDeletePackage).apply {
           setOnClickListener {
               constraintLayoutPackageWrapper.alpha = 0.5F
           }
       }
    }

    override fun removeItem(item: Int) {}

    override fun addItemToPackage() {
        Toast.makeText(this, R.string.added_to_package, Toast.LENGTH_SHORT).show()
    }
}