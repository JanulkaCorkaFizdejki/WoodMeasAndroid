package com.mobile.woodmeas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobile.woodmeas.controller.WoodPackageListSelectAdapter
import com.mobile.woodmeas.model.DatabaseManagerDao
import kotlin.concurrent.thread

class LogPackageListSelectActivity : AppCompatActivity() {

    private lateinit var recyclerViewWooPackageListSelect: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_package_list_select)

        recyclerViewWooPackageListSelect = findViewById(R.id.recyclerViewWooPackageListSelect)

        thread {
            DatabaseManagerDao.getDataBase(this)?.let {
                it.woodenLogPackagesDao().selectAll().let { woodPackageList ->
                    this.runOnUiThread {
                            recyclerViewWooPackageListSelect
                                .layoutManager = LinearLayoutManager(applicationContext)

                            recyclerViewWooPackageListSelect
                                .adapter = WoodPackageListSelectAdapter(woodPackageList, this)

                    }
                }
            }
        }
    }
}