package com.mobile.woodmeas

import android.annotation.SuppressLint
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import com.mobile.woodmeas.controller.TreeAdapter
import com.mobile.woodmeas.model.DatabaseManagerDao
import kotlin.concurrent.thread

interface AppActivityManager {
    fun loadView()
    fun removeItem(item: Int)
    fun goToActivity() {}
    fun goToActivity(id: Int) {}

    @SuppressLint("ResourceAsColor")
    fun setSpinnerTrees(appCompatActivity: AppCompatActivity) {
        thread {
            DatabaseManagerDao.getDataBase(appCompatActivity)?.let { databaseManagerDao ->
                val trees = databaseManagerDao.treesDao().selectAll()
                val treeArrayAdapter = TreeAdapter(appCompatActivity, R.layout.tree_layout_spinner, trees)
                appCompatActivity.runOnUiThread {
                    appCompatActivity.findViewById<Spinner>(R.id.spinnerTreeModuleTrees).apply {
                        adapter = treeArrayAdapter
                    }
                }
            }
        }

        appCompatActivity.findViewById<Switch>(R.id.switchTreeModuleBark).apply {
            setOnClickListener {
                if (this.isChecked) {
                    this.setTextColor(appCompatActivity.resources.getColor(R.color.wm_green_medium))
                }
                else {
                    this.setTextColor(appCompatActivity.resources.getColor(R.color.wm_gray_dark))
                }
            }
        }
    }
}