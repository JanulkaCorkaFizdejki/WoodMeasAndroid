package com.mobile.woodmeas

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobile.woodmeas.controller.PackageSelectPlankAdapter
import com.mobile.woodmeas.datamodel.MenuData
import com.mobile.woodmeas.datamodel.MenuItemsType
import com.mobile.woodmeas.datamodel.MenuType
import com.mobile.woodmeas.model.DatabaseManagerDao
import com.mobile.woodmeas.viewcontrollers.NavigationManager
import kotlin.concurrent.thread

class PlankPackageListSelectActivity : AppCompatActivity() {
    private lateinit var recyclerViewPackageSelectPlank: RecyclerView

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plank_package_list_select)
        NavigationManager.topNavigation(this, MenuData(MenuType.PACKAGES, MenuItemsType.PLANK))

        recyclerViewPackageSelectPlank = findViewById(R.id.recyclerViewPackageSelectPlank)

        thread {
            DatabaseManagerDao.getDataBase(this)?.let { databaseManagerDao ->
                val list  = databaseManagerDao.plankPackagesDao().selectAll()
                this.runOnUiThread {
                    recyclerViewPackageSelectPlank.layoutManager = LinearLayoutManager(this)
                    recyclerViewPackageSelectPlank.adapter = PackageSelectPlankAdapter(list, this)
                }
            }
        }
    }
}