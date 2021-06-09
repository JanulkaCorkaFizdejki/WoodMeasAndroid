package com.mobile.woodmeas

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobile.woodmeas.controller.PackageStackDetailsItemAdapter
import com.mobile.woodmeas.datamodel.MenuItemsType
import com.mobile.woodmeas.model.DatabaseManagerDao
import com.mobile.woodmeas.model.Settings
import com.mobile.woodmeas.model.Trees
import com.mobile.woodmeas.viewcontrollers.NavigationManager
import java.text.DateFormat
import kotlin.concurrent.thread

class StackPackageDetailsActivity : AppCompatActivity(), AppActivityManager {
    private lateinit var recyclerViewStackPackageDetailsList: RecyclerView
    private lateinit var textViewActivityLogPackageDetailsPackageName: TextView
    private lateinit var textViewActivityLogDetailsCreationDate: TextView
    private lateinit var textViewActivityLogDetailsUpdateDate: TextView
    private lateinit var textViewActivityLogPackageDetailsSum: TextView
    private var currentPackageId: Int = 0

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stack_package_details)

        recyclerViewStackPackageDetailsList                 = findViewById(R.id.recyclerViewStackPackageDetailsList)
        textViewActivityLogPackageDetailsPackageName        = findViewById(R.id.textViewActivityStackPackageDetailsPackageName)
        textViewActivityLogDetailsCreationDate              = findViewById(R.id.textViewActivityStackDetailsCreationDate)
        textViewActivityLogDetailsUpdateDate                = findViewById(R.id.textViewActivityStackDetailsUpdateDate)
        textViewActivityLogPackageDetailsSum                = findViewById(R.id.textViewActivityStackPackageDetailsSum)
        recyclerViewStackPackageDetailsList.layoutManager   = LinearLayoutManager(this)

        NavigationManager.let {
            it.topNavigation(this, null)
            it.setTopNavigationBarForPackageDetails(this, MenuItemsType.STACK)
        }

        intent.getIntExtra(Settings.IntentsPutValues.PACKAGE_ID, 0).let { packageId ->
            if (packageId < 1) {
                this.onBackPressed()
                return
            }
            currentPackageId = packageId
        }

        loadView()
    }

    override fun loadView() {
        thread {
            DatabaseManagerDao.getDataBase(this)?.let { databaseManagerDao ->
                val stackList = databaseManagerDao.stackDao().selectWithPackageId(currentPackageId)

                if (stackList.isEmpty()) { this.runOnUiThread { this.onBackPressed() } }

                val treeList: List<Trees> = databaseManagerDao.treesDao().selectAll()

                databaseManagerDao.stackPackagesDao().selectItem(currentPackageId).let {
                    this.runOnUiThread {
                        textViewActivityLogPackageDetailsPackageName.text = it.name
                        it.addDate?.let { _ ->
                            textViewActivityLogDetailsCreationDate
                                .text = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(it.addDate)
                        }
                    }
                }
                val sum: Long = stackList.sumOf { it.cubicCm.toLong() }
                val sumFormat = "%.2f".format(sum.toFloat() / 1000000F).replace(".", ",")

                this.runOnUiThread {
                    stackList.maxByOrNull { it.id }?.let { woodenLog ->
                        woodenLog.addDate?.let { _ ->
                            textViewActivityLogDetailsUpdateDate
                                .text = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(woodenLog.addDate)
                        }
                    }
                    textViewActivityLogPackageDetailsSum.text = sumFormat
                    recyclerViewStackPackageDetailsList.adapter = PackageStackDetailsItemAdapter(stackList, treeList, this)
                }
            }
        }
    }

    override fun removeItem(item: Int) {
        TODO("Not yet implemented")
    }
}