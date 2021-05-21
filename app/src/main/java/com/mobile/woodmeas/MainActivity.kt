package com.mobile.woodmeas

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobile.woodmeas.controller.MenuItemsAdapter
import com.mobile.woodmeas.datamodel.MenuType
import com.mobile.woodmeas.model.DatabaseManager
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    lateinit var imageViewWoodMain: ImageView
    lateinit var textViewMenuItemName: TextView
    lateinit var imageButtonMenuItemPackages: ImageButton
    lateinit var imageButtonMenuItemCalculators: ImageButton
    lateinit var imageViewMenuItemIcon: ImageView
    private var rotateAnimation: RotateAnimation? = null
    private lateinit var recyclerViewMenuItems: RecyclerView


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


//        recyclerViewMenuItems.setOnScrollChangeListener { view, i, i2, i3, i4 ->
//            val layoutManager =  recyclerViewMenuItems.layoutManager
//            if (layoutManager != null) {
//                val o = layoutManager.getPosition(view)
//                println("::::::$o")
//            }
//        }


        setCurrencyMenu(MenuType.CALCULATORS, null)

        thread { dbManager() }




//        itemVolumeCalculator.setOnClickListener {
//            val intent = Intent(this, VolumeCalculatorActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//            startActivity(intent)
//        }
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
            recyclerViewMenuItems.layoutManager = LinearLayoutManager(applicationContext)
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
            recyclerViewMenuItems.layoutManager = LinearLayoutManager(applicationContext)
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