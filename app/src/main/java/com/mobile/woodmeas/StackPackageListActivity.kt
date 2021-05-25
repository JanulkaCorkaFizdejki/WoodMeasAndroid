package com.mobile.woodmeas

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.mobile.woodmeas.datamodel.MenuData
import com.mobile.woodmeas.datamodel.MenuItemsType
import com.mobile.woodmeas.datamodel.MenuType
import com.mobile.woodmeas.viewcontrollers.NavigationManager

class StackPackageListActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stack_package_list)
        NavigationManager.topNavigation(this, MenuData(MenuType.PACKAGES, MenuItemsType.STACK))
    }
}