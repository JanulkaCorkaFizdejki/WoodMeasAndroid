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
import com.mobile.woodmeas.controller.PackagePlankDetailsItemAdapter
import com.mobile.woodmeas.datamodel.MenuItemsType
import com.mobile.woodmeas.helpers.EmailManager
import com.mobile.woodmeas.helpers.FileManager
import com.mobile.woodmeas.helpers.PdfPrinter
import com.mobile.woodmeas.helpers.XlsPrinter
import com.mobile.woodmeas.model.DatabaseManagerDao
import com.mobile.woodmeas.model.Settings
import com.mobile.woodmeas.model.Trees
import com.mobile.woodmeas.viewcontrollers.NavigationManager
import java.io.File
import java.text.DateFormat
import kotlin.concurrent.thread

class PlankPackageDetailsActivity : AppCompatActivity(), AppActivityManager {
    private lateinit var recyclerViewPlankPackageDetailsList: RecyclerView
    private lateinit var textViewActivityPlankPackageDetailsPackageName: TextView
    private lateinit var textViewActivityPlankDetailsCreationDate: TextView
    private lateinit var textViewActivityPlankDetailsUpdateDate: TextView
    private lateinit var textViewActivityPlankPackageDetailsSum: TextView
    private var currentPackageId: Int = 0

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plank_package_details)

        textViewActivityPlankPackageDetailsPackageName = findViewById(R.id.textViewActivityPlankPackageDetailsPackageName)
        textViewActivityPlankDetailsCreationDate = findViewById(R.id.textViewActivityPlankDetailsCreationDate)
        textViewActivityPlankDetailsUpdateDate = findViewById(R.id.textViewActivityPlankDetailsUpdateDate)
        textViewActivityPlankPackageDetailsSum = findViewById(R.id.textViewActivityPlankPackageDetailsSum)
        recyclerViewPlankPackageDetailsList = findViewById(R.id.recyclerViewPlankPackageDetailsList)
        recyclerViewPlankPackageDetailsList.layoutManager = LinearLayoutManager(this)

        NavigationManager.let {
            it.topNavigation(this, null)
            it.setTopNavigationBarForPackageDetails(this, MenuItemsType.PLANK)
        }

        intent.getIntExtra(Settings.IntentsPutValues.PACKAGE_ID, 0).let { packageId ->
            if (packageId < 1) {
                this.onBackPressed()
                return
            }
            currentPackageId = packageId
        }

        loadView()

        // Print bottom navigation button __________________________________________________________
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

        // Email bottom navigation button __________________________________________________________
        findViewById<ImageButton>(R.id.imageButtonBottomNavigationEmail).setOnClickListener {
            val directory = applicationContext.applicationInfo.dataDir + "/files/"
            if (!File(directory).isDirectory) {
                File(applicationContext.applicationInfo.dataDir, "files").apply { mkdir() }
            }
            FileManager.deletePdfPackagesWoodFiles(directory)
            thread {
                PdfPrinter.create(this, currentPackageId, directory)?.let { pdf->
                    XlsPrinter.create(this, currentPackageId, directory)?.let { xls->
                        val intentEmail = EmailManager.sendWithMultipleAttachments(applicationContext, listOf(pdf, xls))
                        startActivity(intentEmail)
                    }
                }
            }
        }

    }

    override fun loadView() {
        thread {
            DatabaseManagerDao.getDataBase(this)?.let { databaseManagerDao ->
                val plankList = databaseManagerDao.plankDao().selectWithPackageId(currentPackageId)

                if (plankList.isEmpty()) { this.runOnUiThread { this.onBackPressed() } }

                val treeList: List<Trees> = databaseManagerDao.treesDao().selectAll()
                databaseManagerDao.plankPackagesDao().selectItem(currentPackageId).let {
                    this.runOnUiThread {
                        textViewActivityPlankPackageDetailsPackageName.text = it.name
                        it.addDate?.let { _ ->
                            textViewActivityPlankDetailsCreationDate
                                .text = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(it.addDate)
                        }
                    }
                }

                val sum: Long = plankList.sumOf { it.cubicCm.toLong() }
                val sumFormat = "%.2f".format(sum.toFloat() / 1000000F).replace(".", ",")
                this.runOnUiThread {
                    plankList.maxByOrNull { it.id }?.let { plank ->
                        plank.addDate?.let { _ ->
                            textViewActivityPlankDetailsUpdateDate
                                .text = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(plank.addDate)
                        }
                    }
                    textViewActivityPlankPackageDetailsSum.text = sumFormat
                    recyclerViewPlankPackageDetailsList.adapter = PackagePlankDetailsItemAdapter(plankList, treeList, this)
                }
            }
        }
    }

    override fun removeItem(item: Int) {
        thread {
            DatabaseManagerDao.getDataBase(this)?.plankDao()?.deleteItem(item)
            loadView()
        }
    }
}