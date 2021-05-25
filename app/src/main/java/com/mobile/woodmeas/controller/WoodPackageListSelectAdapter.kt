package com.mobile.woodmeas.controller

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.mobile.woodmeas.R
import com.mobile.woodmeas.model.Settings
import com.mobile.woodmeas.model.WoodenLogPackages

class WoodPackageListSelectAdapter (private val woodPackagesList: List<WoodenLogPackages>, private val appCompatActivity: AppCompatActivity):
    RecyclerView.Adapter<WoodPackageListSelectViewHolder>() {
    private lateinit var context: Context

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): WoodPackageListSelectViewHolder {
        val layoutInflater : LayoutInflater = LayoutInflater.from(viewGroup.context)
        val woodPackageItem: View = layoutInflater.inflate(R.layout.wood_package_select_list_item, viewGroup, false)
        context = viewGroup.context
        return WoodPackageListSelectViewHolder(woodPackageItem)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: WoodPackageListSelectViewHolder, position: Int) {
        val textViewId: TextView = holder.itemView.findViewById(R.id.textViewWPSListId)
        textViewId.text = (position + 1).toString() + "."

        val textViewName: TextView = holder.itemView.findViewById(R.id.textViewWPSListName)
        textViewName.text = woodPackagesList[position].name

        val textViewDateTime: TextView = holder.itemView.findViewById(R.id.textViewWPSListDateTime)
        textViewDateTime.text = woodPackagesList[position].addDate?.toString()

        val constraintLayoutWPSListItem: ConstraintLayout = holder.itemView.findViewById(R.id.constraintLayoutWPSListItem)
        constraintLayoutWPSListItem.setOnClickListener {
            Settings.VolumeCalculatorView.woodPackageFromSelectIndex = woodPackagesList[position].id
            appCompatActivity.onBackPressed()
        }
    }

    override fun getItemCount(): Int = woodPackagesList.size

}

class WoodPackageListSelectViewHolder(view: View): RecyclerView.ViewHolder(view)