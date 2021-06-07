package com.mobile.woodmeas

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobile.woodmeas.controller.PackageLogDetailsItemAdapter
import com.mobile.woodmeas.controller.PackagePlankDetailsItemAdapter
import com.mobile.woodmeas.controller.WoodenLogListAdapter
import com.mobile.woodmeas.datamodel.MenuItemsType
import com.mobile.woodmeas.helpers.EmailManager
import com.mobile.woodmeas.helpers.FileManager
import com.mobile.woodmeas.helpers.PrintFormatter
import com.mobile.woodmeas.helpers.TimeDateFormatter
import com.mobile.woodmeas.model.DatabaseManagerDao
import com.mobile.woodmeas.model.Settings
import com.mobile.woodmeas.model.Trees
import com.mobile.woodmeas.model.WoodenLog
import com.mobile.woodmeas.viewcontrollers.NavigationManager
import java.io.File
import java.text.DateFormat
import kotlin.concurrent.thread

class LogPackageDetailsActivity : AppCompatActivity(), AppActivityManager {
    private lateinit var recyclerViewLogPackageDetailsList: RecyclerView
    private lateinit var textViewActivityLogPackageDetailsPackageName: TextView
    private lateinit var textViewActivityLogDetailsCreationDate: TextView
    private lateinit var textViewActivityLogDetailsUpdateDate: TextView
    private lateinit var textViewActivityLogPackageDetailsSum: TextView
    private var currentPackageId: Int = 0

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_package_details)

        recyclerViewLogPackageDetailsList               = findViewById(R.id.recyclerViewLogPackageDetailsList)
        textViewActivityLogPackageDetailsPackageName    = findViewById(R.id.textViewActivityLogPackageDetailsPackageName)
        textViewActivityLogDetailsCreationDate          = findViewById(R.id.textViewActivityLogDetailsCreationDate)
        textViewActivityLogDetailsUpdateDate            = findViewById(R.id.textViewActivityLogDetailsUpdateDate)
        textViewActivityLogPackageDetailsSum            = findViewById(R.id.textViewActivityLogPackageDetailsSum)
        recyclerViewLogPackageDetailsList.layoutManager = LinearLayoutManager(this)

        NavigationManager.let {
            it.topNavigation(this, null)
            it.setTopNavigationBarForPackageDetails(this, MenuItemsType.LOG)
        }

        intent.getIntExtra(Settings.IntentsPutValues.PACKAGE_ID, 0).let { packageId ->
            if (packageId < 1) {
                this.onBackPressed()
                return
            }
            currentPackageId = packageId
            println("$currentPackageId")
        }
        loadView()
    }



    override fun loadView() {
        thread {
            DatabaseManagerDao.getDataBase(this)?.let { databaseManagerDao ->
                val woodenLogList = databaseManagerDao.woodenLogDao().selectWithWoodPackageId(currentPackageId)

                if (woodenLogList.isEmpty()) { this.runOnUiThread { this.onBackPressed() } }

                val treeList: List<Trees> = databaseManagerDao.treesDao().selectAll()

                databaseManagerDao.woodenLogPackagesDao().selectItem(currentPackageId).let {
                    this.runOnUiThread {
                        textViewActivityLogPackageDetailsPackageName.text = it.name
                        it.addDate?.let { _ ->
                            textViewActivityLogDetailsCreationDate
                                .text = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(it.addDate)
                        }
                    }
                }
                val sum: Long = woodenLogList.sumOf { it.cubicCm.toLong() }
                val sumFormat = "%.2f".format(sum.toFloat() / 1000000F).replace(".", ",")

                this.runOnUiThread {
                    woodenLogList.maxByOrNull { it.id }?.let { woodenLog ->
                        woodenLog.addDate?.let { _ ->
                            textViewActivityLogDetailsUpdateDate
                                .text = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(woodenLog.addDate)
                        }
                    }
                    textViewActivityLogPackageDetailsSum.text = sumFormat
                    recyclerViewLogPackageDetailsList.adapter = PackageLogDetailsItemAdapter(woodenLogList, treeList, this)
                }
            }
        }
    }

    override fun removeItem(item: Int) {
        thread {
            DatabaseManagerDao.getDataBase(this)?.woodenLogDao()?.deleteItem(item)
            loadView()
        }
    }



}