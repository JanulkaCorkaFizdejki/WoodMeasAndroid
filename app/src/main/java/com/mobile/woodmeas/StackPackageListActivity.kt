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
import com.mobile.woodmeas.controller.StackPackagesListAdapter
import com.mobile.woodmeas.datamodel.MenuData
import com.mobile.woodmeas.datamodel.MenuItemsType
import com.mobile.woodmeas.datamodel.MenuType
import com.mobile.woodmeas.model.DatabaseManagerDao
import com.mobile.woodmeas.model.Settings
import com.mobile.woodmeas.model.StackPackages
import com.mobile.woodmeas.viewcontrollers.NavigationManager
import java.util.*
import kotlin.concurrent.thread

class StackPackageListActivity : AppCompatActivity(), AppActivityManager {
    private lateinit var constraintLayoutNoDataLayer: ConstraintLayout
    private lateinit var recyclerViewStackPackageListItem: RecyclerView
    private lateinit var imageButtonAddingAndSearchingAdd: ImageButton
    private lateinit var editTextAddingAndSearching: EditText
    private lateinit var imageButtonAddPackageFromNoData: ImageButton
    private lateinit var imageButtonAddingAndSearchingDelAll: ImageButton

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stack_package_list)
        NavigationManager.topNavigation(this, MenuData(MenuType.PACKAGES, MenuItemsType.STACK))

        constraintLayoutNoDataLayer             = findViewById(R.id.constraintLayoutNoDataLayer)
        recyclerViewStackPackageListItem        = findViewById(R.id.recyclerViewStackPackageListItem)
        imageButtonAddingAndSearchingAdd        = findViewById(R.id.imageButtonAddingAndSearchingAdd)
        editTextAddingAndSearching              = findViewById(R.id.editTextAddingAndSearching)
        imageButtonAddPackageFromNoData         = findViewById(R.id.imageButtonAddPackageFromNoData)
        imageButtonAddingAndSearchingDelAll     = findViewById(R.id.imageButtonAddingAndSearchingDelAll)

        recyclerViewStackPackageListItem.layoutManager = LinearLayoutManager(applicationContext)

        loadView()

        editTextAddingAndSearching.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

            override fun onTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (editTextAddingAndSearching.text.isNotEmpty()) {
                    val text = "${editTextAddingAndSearching.text}*"
                    thread {
                        DatabaseManagerDao.getDataBase(applicationContext)?.let {
                            it.stackPackageDaoFts().selectFromSearchText(text).let { list ->
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
                            stackDao().deleteAll()
                            stackPackagesDao().deleteAll()
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
                databaseManagerDao.stackPackagesDao().selectAll().let { stackPackages: List<StackPackages> ->
                    if (stackPackages.isEmpty()) {
                        this.runOnUiThread {
                            constraintLayoutNoDataLayer.visibility = View.VISIBLE
                            editTextAddingAndSearching.visibility = View.GONE
                            editTextAddingAndSearching.isClickable = false
                        }
                    }
                    else {
                        this.runOnUiThread {
                            recyclerViewStackPackageListItem.adapter = StackPackagesListAdapter(stackPackages)
                            constraintLayoutNoDataLayer.visibility = View.GONE
                            editTextAddingAndSearching.visibility = View.VISIBLE
                            editTextAddingAndSearching.isClickable = true
                        }
                    }
                }
            }
        }
    }

    override fun removeItem(item: Int) {
        thread {
            DatabaseManagerDao.getDataBase(applicationContext)?.let { databaseManagerDao ->
                databaseManagerDao.stackDao().deleteWithPackageId(item)
                databaseManagerDao.stackPackagesDao().deleteItem(item)
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
        val editTextPackages: EditText = viewAlertInflate.findViewById(R.id.editTextWoodPackageName)
        alertDialog.setView(viewAlertInflate)
        alertDialog.setNeutralButton(R.string.cancel) { _: DialogInterface, _: Int -> }

        alertDialog.setNegativeButton(R.string.create) { _: DialogInterface, _: Int ->
            if (editTextPackages.text.toString().isNotEmpty()) {
                val stackPackages =  StackPackages(0,
                    editTextPackages.text.toString(),
                    Date(),
                    null
                )
                thread {
                    DatabaseManagerDao.getDataBase(this)?.let {
                        it.stackPackagesDao().insert(stackPackages)
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

    private fun loadListFromSearch(stackPackages: List<StackPackages>) {
        this.runOnUiThread {
            recyclerViewStackPackageListItem.adapter = StackPackagesListAdapter(stackPackages)
        }
    }

    override fun goToActivity(id: Int) {
        thread {
            DatabaseManagerDao.getDataBase(applicationContext)?.let { databaseManagerDao ->
                val countItems = databaseManagerDao.stackDao().countWithPackageId(id)
                if (countItems > 0) {
                    this.runOnUiThread {
                        val intent = Intent(this, StackPackageDetailsActivity::class.java)
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