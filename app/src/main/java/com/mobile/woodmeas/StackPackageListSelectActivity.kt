package com.mobile.woodmeas

import android.annotation.SuppressLint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobile.woodmeas.controller.PackageSelectPlankAdapter
import com.mobile.woodmeas.controller.PackageSelectStackAdapter
import com.mobile.woodmeas.datamodel.MenuData
import com.mobile.woodmeas.datamodel.MenuItemsType
import com.mobile.woodmeas.datamodel.MenuType
import com.mobile.woodmeas.model.DatabaseManagerDao
import com.mobile.woodmeas.model.PlankPackages
import com.mobile.woodmeas.model.StackPackages
import com.mobile.woodmeas.viewcontrollers.NavigationManager
import kotlin.concurrent.thread

class StackPackageListSelectActivity : AppCompatActivity() {
    private lateinit var editTextAddingAndSearching: EditText
    private lateinit var recyclerViewPackageSelectStack: RecyclerView

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stack_package_list_select)

        editTextAddingAndSearching              = findViewById(R.id.editTextAddingAndSearching)

        findViewById<ImageButton>(R.id.imageBackButtonTopBarNav).setOnClickListener { this.onBackPressed() }
        findViewById<ImageView>(R.id.imageViewTopBarNavTitle).apply {
            setImageDrawable(resources.getDrawable(R.drawable.ic_product_14_white, null))
        }
        findViewById<TextView>(R.id.textViewTopBarNavTitle).let {
            val name = resources.getString(R.string.select) + " " + resources.getString(R.string.stack_package)
            it.text = name
        }

        recyclerViewPackageSelectStack = findViewById(R.id.recyclerViewPackageSelectStack)
        recyclerViewPackageSelectStack.layoutManager = LinearLayoutManager(this)

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

    }

    private fun loadView(){
        thread {
            DatabaseManagerDao.getDataBase(this)?.let { databaseManagerDao ->
                val list  = databaseManagerDao.stackPackagesDao().selectAll()
                this.runOnUiThread {
                    (editTextAddingAndSearching.parent as? LinearLayout)?.let { linearLayout ->
                        linearLayout.visibility = if (list.size > 10) View.VISIBLE else View.GONE
                    }
                    recyclerViewPackageSelectStack.adapter = PackageSelectStackAdapter(list, this)
                }
            }
        }
    }

    private fun loadListFromSearch(list: List<StackPackages>) {
        this.runOnUiThread {
            recyclerViewPackageSelectStack.adapter = PackageSelectStackAdapter(list, this)
        }
    }
}