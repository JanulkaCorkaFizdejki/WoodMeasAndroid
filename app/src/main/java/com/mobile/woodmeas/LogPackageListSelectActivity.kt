package com.mobile.woodmeas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobile.woodmeas.controller.PackageSelectLogAdapter
import com.mobile.woodmeas.model.DatabaseManagerDao
import kotlin.concurrent.thread

class LogPackageListSelectActivity : AppCompatActivity() {

    private lateinit var recyclerViewPackageSelectLog: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_package_list_select)

        recyclerViewPackageSelectLog = findViewById(R.id.recyclerViewPackageSelectLog)

        thread {
            DatabaseManagerDao.getDataBase(this)?.let { databaseManagerDao ->
                val list  = databaseManagerDao.woodenLogPackagesDao().selectAll()
                this.runOnUiThread {
                    recyclerViewPackageSelectLog.layoutManager = LinearLayoutManager(this)
                    recyclerViewPackageSelectLog.adapter = PackageSelectLogAdapter(list, this)
                }
            }
        }

    }
}