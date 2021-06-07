package com.mobile.woodmeas.viewcontrollers

import android.annotation.SuppressLint
import android.os.Build
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.mobile.woodmeas.R
import com.mobile.woodmeas.datamodel.MenuData
import com.mobile.woodmeas.datamodel.MenuItemsType
import com.mobile.woodmeas.datamodel.MenuType

object NavigationManager {
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("ResourceType", "UseCompatLoadingForDrawables")
    fun topNavigation(appCompatActivity: AppCompatActivity, menuData: MenuData? = null) {
        appCompatActivity.findViewById<ImageButton>(R.id.imageBackButtonTopBarNav).apply {
            setOnClickListener {
                appCompatActivity.onBackPressed()
            }
        }

        menuData?.let { mData ->
            if (mData.menuType == MenuType.CALCULATORS) {
                appCompatActivity.findViewById<ImageView>(R.id.imageViewTopBarNavTitle).apply {
                    setImageDrawable(appCompatActivity.resources.getDrawable(R.drawable.ic_menu_item_calculator_14, null))
                }

                val menuItemNames = appCompatActivity.resources.getStringArray(R.array.menu_calculators)
                appCompatActivity.findViewById<TextView>(R.id.textViewTopBarNavTitle).apply {
                    text = when(mData.menuItemsType) {
                        MenuItemsType.LOG -> menuItemNames[0]
                        MenuItemsType.PLANK -> menuItemNames[1]
                        else -> menuItemNames[2]
                    }
                }
            }
            else {
                appCompatActivity.findViewById<ImageView>(R.id.imageViewTopBarNavTitle).apply {
                    setImageDrawable(appCompatActivity.resources.getDrawable(R.drawable.ic_menu_item_stack_14, null))
                }

                val menuItemNames = appCompatActivity.resources.getStringArray(R.array.menu_packages)
                appCompatActivity.findViewById<TextView>(R.id.textViewTopBarNavTitle).apply {
                    text = when(mData.menuItemsType) {
                        MenuItemsType.LOG -> menuItemNames[0]
                        MenuItemsType.PLANK -> menuItemNames[1]
                        else -> menuItemNames[2]
                    }
                }
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun setTopNavigationBarForPackageDetails(appCompatActivity: AppCompatActivity, menuItemsType: MenuItemsType) {
        appCompatActivity.findViewById<TextView>(R.id.textViewTopBarNavTitle).let {
            when(menuItemsType) {
                MenuItemsType.LOG -> it.text = appCompatActivity.getText(R.string.log_package)
                MenuItemsType.PLANK -> it.text = appCompatActivity.getText(R.string.plank_package)
                else -> it.text = appCompatActivity.getText(R.string.stack_package)
            }
        }

        appCompatActivity.findViewById<ImageView>(R.id.imageViewTopBarNavTitle)
            .setImageDrawable(appCompatActivity.resources.getDrawable(R.drawable.ic_menu_item_stack_14, null))
    }
}