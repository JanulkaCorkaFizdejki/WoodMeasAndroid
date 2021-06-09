package com.mobile.woodmeas.controller

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.mobile.woodmeas.R
import com.mobile.woodmeas.model.*
import java.text.DateFormat

class PackageSelectPlankAdapter (
    private val plankPackages: List<PlankPackages>,
    private val appCompatActivity: AppCompatActivity) :
    RecyclerView.Adapter<PackageSelectViewHolder>(){
    private lateinit var context: Context

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): PackageSelectViewHolder {
        val layoutInflater : LayoutInflater = LayoutInflater.from(viewGroup.context)
        val packageSelectLayout: View = layoutInflater.inflate(R.layout.package_select_item, viewGroup, false)
        context = viewGroup.context
        return PackageSelectViewHolder(packageSelectLayout)
    }

    override fun onBindViewHolder(holder: PackageSelectViewHolder, position: Int) {
        holder.itemView.findViewById<ConstraintLayout>(R.id.constraintLayoutPackageSelect)
            .setOnClickListener {
                Settings.PackagesSelect.id = plankPackages[position].id
                appCompatActivity.onBackPressed()
            }

        holder.itemView.findViewById<TextView>(R.id.textViewPackageSelectItemName)
            .text = plankPackages[position].name

        holder.itemView.findViewById<TextView>(R.id.textViewPackageSelectItemDate).apply {
            plankPackages[position].addDate?.let { date->
                text = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(date)
            }
        }
    }

    override fun getItemCount(): Int = plankPackages.size

}

class PackageSelectLogAdapter (
    private val plankPackages: List<WoodenLogPackages>,
    private val appCompatActivity: AppCompatActivity) :
    RecyclerView.Adapter<PackageSelectViewHolder>(){
    private lateinit var context: Context

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): PackageSelectViewHolder {
        val layoutInflater : LayoutInflater = LayoutInflater.from(viewGroup.context)
        val packageSelectLayout: View = layoutInflater.inflate(R.layout.package_select_item, viewGroup, false)
        context = viewGroup.context
        return PackageSelectViewHolder(packageSelectLayout)
    }

    override fun onBindViewHolder(holder: PackageSelectViewHolder, position: Int) {
        holder.itemView.findViewById<ConstraintLayout>(R.id.constraintLayoutPackageSelect)
            .setOnClickListener {
                Settings.PackagesSelect.id = plankPackages[position].id
                appCompatActivity.onBackPressed()
            }

        holder.itemView.findViewById<TextView>(R.id.textViewPackageSelectItemName)
            .text = plankPackages[position].name

        holder.itemView.findViewById<TextView>(R.id.textViewPackageSelectItemDate).apply {
            plankPackages[position].addDate?.let { date->
                text = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(date)
            }
        }
    }

    override fun getItemCount(): Int = plankPackages.size

}

class PackageSelectStackAdapter (
    private val stackPackages: List<StackPackages>,
    private val appCompatActivity: AppCompatActivity) :
    RecyclerView.Adapter<PackageSelectViewHolder>(){
    private lateinit var context: Context

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): PackageSelectViewHolder {
        val layoutInflater : LayoutInflater = LayoutInflater.from(viewGroup.context)
        val packageSelectLayout: View = layoutInflater.inflate(R.layout.package_select_item, viewGroup, false)
        context = viewGroup.context
        return PackageSelectViewHolder(packageSelectLayout)
    }

    override fun onBindViewHolder(holder: PackageSelectViewHolder, position: Int) {
        holder.itemView.findViewById<ConstraintLayout>(R.id.constraintLayoutPackageSelect)
            .setOnClickListener {
                Settings.PackagesSelect.id = stackPackages[position].id
                appCompatActivity.onBackPressed()
            }

        holder.itemView.findViewById<TextView>(R.id.textViewPackageSelectItemName)
            .text = stackPackages[position].name

        holder.itemView.findViewById<TextView>(R.id.textViewPackageSelectItemDate).apply {
            stackPackages[position].addDate?.let { date->
                text = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(date)
            }
        }
    }

    override fun getItemCount(): Int = stackPackages.size

}

class PackageSelectViewHolder(view: View): RecyclerView.ViewHolder(view)