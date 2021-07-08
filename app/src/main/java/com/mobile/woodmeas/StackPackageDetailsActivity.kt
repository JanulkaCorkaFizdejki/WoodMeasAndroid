package com.mobile.woodmeas

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobile.woodmeas.controller.PackageStackDetailsItemAdapter
import com.mobile.woodmeas.datamodel.MenuItemsType
import com.mobile.woodmeas.datamodel.UnitsMeasurement
import com.mobile.woodmeas.helpers.EmailManager
import com.mobile.woodmeas.helpers.FileManager
import com.mobile.woodmeas.helpers.PdfPrinter
import com.mobile.woodmeas.helpers.XlsPrinter
import com.mobile.woodmeas.model.DatabaseManagerDao
import com.mobile.woodmeas.model.Settings
import com.mobile.woodmeas.model.Trees
import com.mobile.woodmeas.viewcontrollers.CubicToMoney
import com.mobile.woodmeas.viewcontrollers.NavigationManager
import com.mobile.woodmeas.viewcontrollers.NoteManager
import java.io.File
import java.text.DateFormat
import kotlin.concurrent.thread

class StackPackageDetailsActivity : AppCompatActivity(), AppActivityManager {
    private lateinit var recyclerViewStackPackageDetailsList: RecyclerView
    private lateinit var textViewActivityStackPackageDetailsPackageName: TextView
    private lateinit var textViewActivityStackDetailsCreationDate: TextView
    private lateinit var textViewActivityStackDetailsUpdateDate: TextView
    private lateinit var textViewActivityStackPackageDetailsSum: TextView
    private var currentPackageId: Int = 0
    private var unitsMeasurement = UnitsMeasurement.CM

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stack_package_details)

        recyclerViewStackPackageDetailsList                     = findViewById(R.id.recyclerViewStackPackageDetailsList)
        textViewActivityStackPackageDetailsPackageName          = findViewById(R.id.textViewActivityStackPackageDetailsPackageName)
        textViewActivityStackDetailsCreationDate                = findViewById(R.id.textViewActivityStackDetailsCreationDate)
        textViewActivityStackDetailsUpdateDate                  = findViewById(R.id.textViewActivityStackDetailsUpdateDate)
        textViewActivityStackPackageDetailsSum                  = findViewById(R.id.textViewActivityStackPackageDetailsSum)
        recyclerViewStackPackageDetailsList.layoutManager       = LinearLayoutManager(this)

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

        NoteManager.set(this, currentPackageId, findViewById(R.id.linearLayoutPackageDetailsStackHeader))

        loadView()

        // Print bottom navigation button __________________________________________________________
        findViewById<ImageButton>(R.id.imageButtonBottomNavigationPrint).setOnClickListener {
            val directory = applicationContext.applicationInfo.dataDir + "/files/"
            if (!File(directory).isDirectory) {
                File(applicationContext.applicationInfo.dataDir, "files").apply { mkdir() }
            }
            FileManager.deleteRapportFiles(directory)
            thread {
                PdfPrinter.create(this, currentPackageId, directory, unitsMeasurement)?.let { pdf->
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
            FileManager.deleteRapportFiles(directory)
            thread {
                PdfPrinter.create(this, currentPackageId, directory, unitsMeasurement)?.let { pdf->
                    XlsPrinter.create(this, currentPackageId, directory, unitsMeasurement)?.let { xls->
                        val intentEmail = EmailManager.sendWithMultipleAttachments(applicationContext, listOf(pdf, xls))
                        val db = DatabaseManagerDao.getDataBase(this)
                        val settings = db?.settingsDbDao()?.select()
                        val appNoteFooter = resources.getString(R.string.sent_from_app)
                        if (settings != null) {
                            if (settings.addNoteToEmail > 0) {
                                val woodLogPackage = db.stackPackagesDao().selectItem(currentPackageId)
                                if (woodLogPackage.note != null) {
                                    intentEmail.putExtra(Intent.EXTRA_TEXT, EmailManager.getTextBody(woodLogPackage.note, appNoteFooter))
                                    intentEmail.putExtra(Intent.EXTRA_HTML_TEXT, EmailManager.getTextHtml(woodLogPackage.note, appNoteFooter))
                                }
                                else {
                                    intentEmail.putExtra(Intent.EXTRA_TEXT, EmailManager.getTextBodyFooter(appNoteFooter))
                                    intentEmail.putExtra(Intent.EXTRA_HTML_TEXT, EmailManager.getTextHtmlFooter(appNoteFooter))
                                }
                            }
                            else {
                                intentEmail.putExtra(Intent.EXTRA_TEXT, EmailManager.getTextBodyFooter(appNoteFooter))
                                intentEmail.putExtra(Intent.EXTRA_HTML_TEXT, EmailManager.getTextHtmlFooter(appNoteFooter))
                            }
                        }
                        startActivity(intentEmail)
                    }
                }
            }
        }
        // Cubic to Money __________________________________________________________________________
        findViewById<ImageButton>(R.id.imageButtomBottomNavigationCubicToMoney).setOnClickListener {
            val m3 = textViewActivityStackPackageDetailsSum.text.toString()
                .replace(",", ".")
                .toFloat()
            CubicToMoney.run(this, m3, unitsMeasurement)
        }
    }

    override fun loadView() {
        thread {
            DatabaseManagerDao.getDataBase(this)?.let { databaseManagerDao ->
                val stackList = databaseManagerDao.stackDao().selectWithPackageId(currentPackageId)

                if (stackList.isEmpty()) { this.runOnUiThread { this.onBackPressed() } }

                val treeList: List<Trees> = databaseManagerDao.treesDao().selectAll()

                unitsMeasurement = databaseManagerDao.settingsDbDao().select().getUnitMeasurement()

                databaseManagerDao.stackPackagesDao().selectItem(currentPackageId).let {
                    this.runOnUiThread {
                        if (unitsMeasurement == UnitsMeasurement.CM) {
                            findViewById<TextView>(R.id.textViewPackageDetailsHeaderUnitStack).text = resources.getText(R.string.m3_short)
                        }
                        else {
                            findViewById<TextView>(R.id.textViewPackageDetailsHeaderUnitStack).text = resources.getText(R.string.ft3_short)
                        }
                        textViewActivityStackPackageDetailsPackageName.text = it.name
                        it.addDate?.let { _ ->
                            textViewActivityStackDetailsCreationDate
                                .text = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(it.addDate)
                        }
                    }
                }
                val sum: Long = stackList.sumOf { it.cubicCm.toLong() }
                val sumFormat = if(unitsMeasurement == UnitsMeasurement.CM)
                    {"%.2f".format(sum.toFloat() / 100.00F).replace(".", ",")}
                        else {
                            UnitsMeasurement.convertToFootToString("%.2f".format(sum.toFloat() / 100.00F).replace(",", ".").toFloat())
                        }

                this.runOnUiThread {
                    stackList.maxByOrNull { it.id }?.let { woodenLog ->
                        woodenLog.addDate?.let { _ ->
                            textViewActivityStackDetailsUpdateDate
                                .text = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(woodenLog.addDate)
                        }
                    }
                    findViewById<TextView>(R.id.textViewUnitMeasurmentLog).text = unitsMeasurement.getNameUnitCubic(this)
                    textViewActivityStackPackageDetailsSum.text = sumFormat
                    recyclerViewStackPackageDetailsList.adapter = PackageStackDetailsItemAdapter(stackList, treeList, this, unitsMeasurement)
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

    override fun onBackPressed() {
        (this.getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager)?.hideSoftInputFromWindow(recyclerViewStackPackageDetailsList.windowToken, 0)
        super.onBackPressed()
    }

    override fun onStop() {
        (this.getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager)?.hideSoftInputFromWindow(recyclerViewStackPackageDetailsList.windowToken, 0)
        super.onStop()
    }

    override fun onDestroy() {
        (this.getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager)?.hideSoftInputFromWindow(recyclerViewStackPackageDetailsList.windowToken, 0)
        super.onDestroy()
    }
}