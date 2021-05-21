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
import com.mobile.woodmeas.controller.WoodenLogListAdapter
import com.mobile.woodmeas.helpers.EmailManager
import com.mobile.woodmeas.helpers.FileManager
import com.mobile.woodmeas.helpers.PrintFormatter
import com.mobile.woodmeas.helpers.TimeDateFormatter
import com.mobile.woodmeas.model.DatabaseManagerDao
import com.mobile.woodmeas.model.Settings
import com.mobile.woodmeas.model.Trees
import com.mobile.woodmeas.model.WoodenLog
import java.io.File
import kotlin.concurrent.thread

class LogPackageDetailsActivity : AppCompatActivity(), AppActivityManager {
    private lateinit var textViewWoodPackageName: TextView
    private lateinit var textViewWoodPackageCreationDate: TextView
    private lateinit var textViewPackageLastUpdateDate: TextView
    private lateinit var recyclerViewWoodenLogList: RecyclerView
    private lateinit var textViewWoodenLogSum: TextView
    private lateinit var textViewCubicSum: TextView
    private var treeList: List<Trees> = listOf()
    private var currentWoodPackageId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_package_details)

        textViewWoodPackageName         = findViewById(R.id.textViewWoodPackageName)
        textViewWoodPackageCreationDate = findViewById(R.id.textViewWoodPackageCreationDate)
        recyclerViewWoodenLogList       = findViewById(R.id.recyclerViewWoodenLogList)
        textViewPackageLastUpdateDate   = findViewById(R.id.textViewPackageLastUpdateDate)
        textViewWoodenLogSum            = findViewById(R.id.textViewWoodenLogSum)
        textViewCubicSum                = findViewById(R.id.textViewCubicSum)

        intent.getIntExtra(Settings.IntentsPutValues.WOOD_PACKAGE_ID, -1).let { woodPackageId ->
            if (woodPackageId >= 0) {
                thread {
                    DatabaseManagerDao.getDataBase(this)?.let { databaseManagerDao ->
                        databaseManagerDao.woodPackagesDao().selectWithId(woodPackageId).let { woodPackages ->

                            treeList = databaseManagerDao.treesDao().selectAll()
                            currentWoodPackageId = woodPackages.id

                            this.runOnUiThread {
                                textViewWoodPackageName.text = woodPackages.name
                                textViewWoodPackageCreationDate.text = woodPackages.addDate?.toString()
                            }
                            databaseManagerDao.woodenLog().selectWithWoodPackageId(woodPackages.id).let { woodenLogList: List<WoodenLog> ->
                                this.runOnUiThread{
                                    woodenLogList.maxByOrNull { it.id }?.let { woodenLog ->
                                        woodenLog.addDate?.let {
                                            textViewPackageLastUpdateDate.text = it.toString()
                                        }
                                    }
                                    recyclerViewWoodenLogList.layoutManager = LinearLayoutManager(applicationContext)
                                    recyclerViewWoodenLogList
                                        .adapter = WoodenLogListAdapter(woodenLogList, treeList, this)
                                    setResult(woodenLogList)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setResult(woodenLogList: List<WoodenLog>) {
        val cubicSum = "%.2f".format(woodenLogList.sumOf { it.cubicCm } / 100.0F)
        textViewCubicSum.text = cubicSum
        textViewWoodenLogSum.text = woodenLogList.size.toString()
    }

    override fun loadView() {
        thread {
            DatabaseManagerDao.getDataBase(this)?.let { databaseManagerDao ->
                databaseManagerDao.woodenLog().selectWithWoodPackageId(currentWoodPackageId).let { woodenLogList: List<WoodenLog> ->
                    this.runOnUiThread{
                        woodenLogList.maxByOrNull { it.id }?.let { woodenLog ->
                            woodenLog.addDate?.let {
                                textViewPackageLastUpdateDate.text = it.toString()
                            }
                        }
                        recyclerViewWoodenLogList.layoutManager = LinearLayoutManager(applicationContext)
                        recyclerViewWoodenLogList
                            .adapter = WoodenLogListAdapter(woodenLogList, treeList, this)
                        setResult(woodenLogList)
                    }
                }
            }
        }
    }

    override fun removeItem(item: Int) {
        thread {
            DatabaseManagerDao.getDataBase(this)?.woodenLog()?.deleteItem(item)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun onClickPrintWoodPackage(view: View) {

        thread {
            DatabaseManagerDao.getDataBase(this)?.let { databaseManagerDao ->
                databaseManagerDao.woodenLog().selectWithWoodPackageId(currentWoodPackageId).let { woodenLogList: List<WoodenLog> ->
                    val woodPackages = databaseManagerDao.woodPackagesDao().selectWithId(currentWoodPackageId)
                    val bitmapLogo = BitmapFactory.decodeResource(applicationContext.resources, R.drawable.wood_meas_150)
                    val dateDocumentCreated = TimeDateFormatter.dateFromCalendarToString()

                    val directory = applicationContext.applicationInfo.dataDir + "/files/"

                    if (!File(directory).isDirectory) {
                        val file = File(applicationContext.applicationInfo.dataDir, "files")
                        file.mkdir()
                    }

                    val filePath = directory + PrintFormatter.setFileName(woodPackages.id)

                    FileManager.deletePdfPackagesWoodFiles(directory)

                    PrintFormatter.createPdfRapport(
                        filePath,
                        woodenLogList,
                        woodPackages,
                        treeList,
                        bitmapLogo,
                        dateDocumentCreated
                    )


                    val fileProvider = FileProvider.getUriForFile(applicationContext, "${BuildConfig.APPLICATION_ID}.provider", File(filePath))
                    val intent =   Intent(Intent.ACTION_VIEW).apply {
                        type = "application/pdf"
                        data = fileProvider
                    }

                    val intentChooser = Intent.createChooser(intent, "Open  file")

                    try {
                        startActivity(intentChooser)
                    } catch (ex: ActivityNotFoundException) {

                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun onClickSendByEmailWoodPackage(view: View) {
        thread {
            DatabaseManagerDao.getDataBase(this)?.let { databaseManagerDao ->
                databaseManagerDao.woodenLog().selectWithWoodPackageId(currentWoodPackageId).let { woodenLogList: List<WoodenLog> ->
                    val woodPackages = databaseManagerDao.woodPackagesDao().selectWithId(currentWoodPackageId)
                    val bitmapLogo = BitmapFactory.decodeResource(applicationContext.resources, R.drawable.wood_meas_150)
                    val dateDocumentCreated = TimeDateFormatter.dateFromCalendarToString()

                    val directory = applicationContext.applicationInfo.dataDir + "/files/"

                    if (!File(directory).isDirectory) {
                        val file = File(applicationContext.applicationInfo.dataDir, "files")
                        file.mkdir()
                    }


                    val fileNames = PrintFormatter.setFileName(woodPackages.id)

                    val pdfFilePath = directory + fileNames.first
                    val xlsFilePath = directory + fileNames.second

                    FileManager.deletePdfPackagesWoodFiles(directory)

                    PrintFormatter.createPdfRapport(
                        pdfFilePath,
                        woodenLogList,
                        woodPackages,
                        treeList,
                        bitmapLogo,
                        dateDocumentCreated
                    )


                    val fileExcel = File(xlsFilePath)
                    fileExcel.createNewFile()


                    PrintFormatter.createXlsRapport(
                        xlsFilePath,
                        woodenLogList,
                        woodPackages,
                        treeList
                    )


                    val intentEmail = EmailManager.sendWithMultipleAttachments(applicationContext, listOf(pdfFilePath, xlsFilePath))
                        startActivity(intentEmail)
                }
            }
        }
    }


}