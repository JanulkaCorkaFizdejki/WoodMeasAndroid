package com.mobile.woodmeas

import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobile.woodmeas.controller.WoodenPackagesListAdapter
import com.mobile.woodmeas.datamodel.MenuData
import com.mobile.woodmeas.datamodel.MenuItemsType
import com.mobile.woodmeas.datamodel.MenuType
import com.mobile.woodmeas.model.DatabaseManagerDao
import com.mobile.woodmeas.model.Settings
import com.mobile.woodmeas.model.WoodenLogPackages
import com.mobile.woodmeas.viewcontrollers.NavigationManager
import java.util.*
import kotlin.concurrent.thread

class LogPackageListActivity : AppCompatActivity(), AppActivityManager {
    private lateinit var constraintLayoutNoDataLayer: ConstraintLayout
    private lateinit var recyclerViewWoodenLogPackageListItem: RecyclerView
    private lateinit var imageButtonAddingAndSearchingAdd: ImageButton
    private lateinit var editTextAddingAndSearching: EditText
    private lateinit var imageButtonAddPackageFromNoData: ImageButton
    private lateinit var imageButtonAddingAndSearchingDelAll: ImageButton

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_package_list)
        NavigationManager.topNavigation(this, MenuData(MenuType.PACKAGES, MenuItemsType.LOG))

        constraintLayoutNoDataLayer             = findViewById(R.id.constraintLayoutNoDataLayer)
        recyclerViewWoodenLogPackageListItem    = findViewById(R.id.recyclerViewPlankPackageListItem)
        imageButtonAddingAndSearchingAdd        = findViewById(R.id.imageButtonAddingAndSearchingAdd)
        editTextAddingAndSearching              = findViewById(R.id.editTextAddingAndSearching)
        imageButtonAddPackageFromNoData         = findViewById(R.id.imageButtonAddPackageFromNoData)
        imageButtonAddingAndSearchingDelAll     = findViewById(R.id.imageButtonAddingAndSearchingDelAll)

        recyclerViewWoodenLogPackageListItem.layoutManager = LinearLayoutManager(applicationContext)


        loadView()


        editTextAddingAndSearching.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

            override fun onTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (editTextAddingAndSearching.text.isNotEmpty()) {
                    val text = "${editTextAddingAndSearching.text}*"
                    thread {
                        DatabaseManagerDao.getDataBase(applicationContext)?.let {
                            it.woodenLogPackagesDaoFts().selectFromSearchText(text).let { list ->
                                loadListFromSearch(list)
                            }
                        }
                    }
                }
                else { loadView() }
            }
            override fun afterTextChanged(p0: Editable?) { }
        })


        imageButtonAddingAndSearchingAdd.setOnClickListener {
            createNewPackages()
        }

        imageButtonAddPackageFromNoData.setOnClickListener { createNewPackages() }

        imageButtonAddingAndSearchingDelAll.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this)
            alertDialog.setTitle(R.string.delete_all_packages_question)
            alertDialog.setNeutralButton(R.string.cancel) {_: DialogInterface, _:Int ->}
            alertDialog.setPositiveButton(R.string.delete) {_: DialogInterface, _:Int ->
                thread {
                    DatabaseManagerDao.getDataBase(applicationContext)?.let { databaseManagerDao ->
                        databaseManagerDao.apply {
                            woodenLogDao().deleteAll()
                            woodenLogPackagesDao().deleteAll()
                        }
                        loadView()
                    }
                }
            }
            alertDialog.show()
        }
    }

    override fun loadView() {
        thread {
            DatabaseManagerDao.getDataBase(applicationContext)?.let { databaseManagerDao ->
                databaseManagerDao.woodenLogPackagesDao().selectAll().let { woodenLogPackages: List<WoodenLogPackages> ->
                    if (woodenLogPackages.isEmpty()) {
                        this.runOnUiThread {
                            constraintLayoutNoDataLayer.visibility = View.VISIBLE
                        }
                    }
                    else {
                        this.runOnUiThread {
                            recyclerViewWoodenLogPackageListItem.adapter = WoodenPackagesListAdapter(woodenLogPackages)
                            constraintLayoutNoDataLayer.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    override fun removeItem(item: Int) {
        thread {
            DatabaseManagerDao.getDataBase(applicationContext)?.let { databaseManagerDao ->
                databaseManagerDao.woodenLogDao().deleteItemWithPackages(item)
                databaseManagerDao.woodenLogPackagesDao().deleteItem(item)
                loadView()
                this.runOnUiThread {
                    editTextAddingAndSearching.clearFocus()
                    Toast.makeText(this, R.string.package_has_been_removed, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun createNewPackages() {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle(R.string.create_package)
        val viewAlertInflate: View = View.inflate(this, R.layout.add_wood_package, null)
        val editTextWoodPackages: EditText = viewAlertInflate.findViewById(R.id.editTextWoodPackageName)
        alertDialog.setView(viewAlertInflate)
        alertDialog.setNeutralButton(R.string.cancel) { _: DialogInterface, _: Int -> }

        alertDialog.setNegativeButton(R.string.create) { _: DialogInterface, _: Int ->
            if (editTextWoodPackages.text.toString().isNotEmpty()) {
                val woodPackages =  WoodenLogPackages(0,
                    editTextWoodPackages.text.toString(),
                    Date()
                )
                thread {
                    DatabaseManagerDao.getDataBase(this)?.let {
                        it.woodenLogPackagesDao().insert(woodPackages)
                        loadView()
                        this.runOnUiThread {
                            editTextAddingAndSearching.clearFocus()
                            Toast.makeText(this, R.string.created_package, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        }
        alertDialog.show()
    }

    private fun loadListFromSearch(woodenLogPackages: List<WoodenLogPackages>) {
        this.runOnUiThread {
            recyclerViewWoodenLogPackageListItem.adapter = WoodenPackagesListAdapter(woodenLogPackages)
        }
    }
    

    override fun goToActivity(id: Int) {
        thread {
            DatabaseManagerDao.getDataBase(applicationContext)?.let { databaseManagerDao ->
                val countItems = databaseManagerDao.woodenLogDao().count(id)
                if (countItems > 0) {
                    this.runOnUiThread {
                        val intent = Intent(this, LogPackageDetailsActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        intent.putExtra(Settings.IntentsPutValues.PACKAGE_ID, id)
                        startActivity(intent)
                    }
                }
                else {
                    this.runOnUiThread {
                        val alertDialog = AlertDialog.Builder(this)
                        alertDialog.setMessage(R.string.package_is_empty)
                        alertDialog.setPositiveButton("OK"){_: DialogInterface, _: Int ->}
                        alertDialog.show()
                    }
                }
            }
        }
    }
}