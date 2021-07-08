package com.mobile.woodmeas

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobile.woodmeas.controller.MenuItemsAdapter
import com.mobile.woodmeas.datamodel.MenuType
import com.mobile.woodmeas.helpers.PdfPrinter
import com.mobile.woodmeas.math.Calculator
import com.mobile.woodmeas.model.DatabaseManager
import com.mobile.woodmeas.viewcontrollers.OnSwipeTouchListener
import java.util.*
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    private lateinit var imageViewWoodMain: ImageView
    private lateinit var textViewMenuItemName: TextView
    private lateinit var imageButtonMenuItemPackages: ImageButton
    private lateinit var imageButtonMenuItemCalculators: ImageButton
    private lateinit var imageViewMenuItemIcon: ImageView
    private var rotateAnimation: RotateAnimation? = null
    private lateinit var recyclerViewMenuItems: RecyclerView


    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageViewWoodMain = findViewById(R.id.imageViewWoodMain)
        imageButtonMenuItemPackages = findViewById(R.id.imageButtonMenuItemPackages)
        imageButtonMenuItemCalculators = findViewById(R.id.imageButtonMenuItemCalculators)
        textViewMenuItemName = findViewById(R.id.textViewMenuItemName)
        imageViewMenuItemIcon = findViewById(R.id.imageViewMenuItemIcon)
        recyclerViewMenuItems = findViewById(R.id.recyclerViewMenuItems)

        recyclerViewMenuItems.layoutManager = LinearLayoutManager(applicationContext)

        println("****************************")
        println(Resources.getSystem().displayMetrics.widthPixels)


        Resources.getSystem().displayMetrics.heightPixels.also {
            if (it < 900) {
                recyclerViewMenuItems.layoutParams.height = 100
            }
        }

        setCurrencyMenu(MenuType.CALCULATORS, null)


        thread { dbManager() }

    }

    override fun onStart() {
        super.onStart()
        rotateAnimation = RotateAnimation(0F, 360f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f).apply {
            interpolator = LinearInterpolator()
            duration = 5000
            repeatCount = Animation.INFINITE
        }
        imageViewWoodMain.startAnimation(rotateAnimation)
    }

    override fun onBackPressed() {
        imageViewWoodMain.clearAnimation()
        rotateAnimation = null
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun onClickMenuButton(view: View) {
        if (view !is ImageButton) return

        when(view.context.resources.getResourceEntryName(view.id)) {
            "imageButtonMenuItemPackages"       -> setCurrencyMenu(MenuType.PACKAGES, view)
            "imageButtonMenuItemCalculators"    -> setCurrencyMenu(MenuType.CALCULATORS, view)
            "imageButtonMenuItemInfo" -> {
                Intent(this, InfoActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                }.let {
                    startActivity(it)
                }
            }
            "imageButtonMenuItemSettings" -> {
                Intent(this, SettingsActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                }.let {
                    startActivity(it)
                }
            }
            else -> return
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setCurrencyMenu (menuType: MenuType, sender: ImageButton?) {
        if (menuType == MenuType.PACKAGES) {
            textViewMenuItemName.text = applicationContext.resources.getString(R.string.menu_name_packages)
            imageViewMenuItemIcon.setImageDrawable(applicationContext.getDrawable(R.drawable.ic_menu_item_stack_14))
            sender?.apply {
                setImageDrawable(applicationContext.getDrawable(R.drawable.ic_menu_item_stack_fill))
            }?.let {
                imageButtonMenuItemCalculators.setImageDrawable(applicationContext.getDrawable(R.drawable.ic_menu_item_calculator))
            }

            val menuItemNames = applicationContext.resources.getStringArray(R.array.menu_packages).toList()
            recyclerViewMenuItems.adapter = MenuItemsAdapter(menuItemNames, menuType)
        }
        else {
            textViewMenuItemName.text = applicationContext.resources.getString(R.string.menu_name_calculators)
            imageViewMenuItemIcon.setImageDrawable(applicationContext.getDrawable(R.drawable.ic_menu_item_calculator_14))
            sender?.apply {
                setImageDrawable(applicationContext.getDrawable(R.drawable.ic_menu_item_calculator_fill))
            }?.let {
                imageButtonMenuItemPackages.setImageDrawable(applicationContext.getDrawable(R.drawable.ic_menu_item_stack))
            }

            val menuItemNames = applicationContext.resources.getStringArray(R.array.menu_calculators).toList()
            recyclerViewMenuItems.adapter = MenuItemsAdapter(menuItemNames, menuType)
        }
    }


    private fun dbManager() {
        DatabaseManager.doesDatabaseExist(applicationContext).let {
            if(!it) {
                DatabaseManager.copy(applicationContext, applicationContext.applicationInfo.dataDir).let { copyResult ->
                    if (copyResult) {
                        println("Kopiowanie powiodło się")
                    }
                    else {
                        println("nIC SIĘ KURWA NIE UDAŁO")
                    }
                }
            }
        }
    }
}