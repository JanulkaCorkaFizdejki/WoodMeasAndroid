package com.mobile.woodmeas

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobile.woodmeas.controller.PackageStackDetailsItemAdapter
import com.mobile.woodmeas.datamodel.MenuItemsType
import com.mobile.woodmeas.helpers.FileManager
import com.mobile.woodmeas.helpers.PdfPrinter
import com.mobile.woodmeas.model.DatabaseManagerDao
import com.mobile.woodmeas.model.Settings
import com.mobile.woodmeas.model.Trees
import com.mobile.woodmeas.viewcontrollers.NavigationManager
import java.io.File
import java.text.DateFormat
import kotlin.concurrent.thread

class StackPackageDetailsActivity : AppCompatActivity(), AppActivityManager {
    private lateinit var recyclerViewStackPackageDetailsList: RecyclerView
    private lateinit var textViewActivityLogPackageDetailsPackageName: TextView
    private lateinit var textViewActivityLogDetailsCreationDate: TextView
    private lateinit var textViewActivityLogDetailsUpdateDate: TextView
    private lateinit var textViewActivityLogPackageDetailsSum: TextView
    private var currentPackageId: Int = 0

    @RequiresApi(Build.VERSION_CODES.N)
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

        // Bottom email and print nav ______________________________________________________________
        findViewById<ImageButton>(R.id.imageButtonBottomNavigationPrint).setOnClickListener {
            val directory = applicationContext.applicationInfo.dataDir + "/files/"
            if (!File(directory).isDirectory) {
                File(applicationContext.applicationInfo.dataDir, "files").apply { mkdir() }
            }
            FileManager.deletePdfPackagesWoodFiles(directory)
            thread {
                PdfPrinter.create(this, currentPackageId, directory)?.let { pdf->
                    val fileProvider = FileProvider.getUriForFile(applicationContext, "${BuildConfig.APPLICATION_ID}.provider", File(pdf))
                    val intent =   Intent(Intent.ACTION_VIEW).apply {
                        type = "application/pdf"
                        data = fileProvider
                    }
                    try { startActivity(Intent.createChooser(intent, "Open  file"))
                    } catch (ex: ActivityNotFoundException) { }
                }
            }
        }
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
        thread {
            DatabaseManagerDao.getDataBase(this)?.stackDao()?.deleteItem(item)
            loadView()
        }
    }
}