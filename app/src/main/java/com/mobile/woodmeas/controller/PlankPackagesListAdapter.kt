package com.mobile.woodmeas.controller

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.mobile.woodmeas.AppActivityManager
import com.mobile.woodmeas.R
import com.mobile.woodmeas.model.Plank
import com.mobile.woodmeas.model.PlankPackages
import com.mobile.woodmeas.model.WoodenLogPackages
import java.text.DateFormat

class PlankPackagesListAdapter (private val plankPackagesList: List<PlankPackages>) :
    RecyclerView.Adapter<WoodenPackagesListAdapterViewHolder>(){

    private lateinit var context: Context

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): WoodenPackagesListAdapterViewHolder {
        val layoutInflater : LayoutInflater = LayoutInflater.from(viewGroup.context)
        val packageItem: View = layoutInflater.inflate(R.layout.wooden_log_package_list_item, viewGroup, false)
        context = viewGroup.context
        return WoodenPackagesListAdapterViewHolder(packageItem)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: WoodenPackagesListAdapterViewHolder, position: Int) {
        holder.itemView.findViewById<TextView>(R.id.textView101).apply {
            text = (position + 1).toString() + "."
        }

        holder.itemView.findViewById<TextView>(R.id.textView102).apply {
            text = plankPackagesList[position].name
        }

        plankPackagesList[position].addDate?.let { addDate ->
            holder.itemView.findViewById<TextView>(R.id.textView100).apply {
                text = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(addDate)
            }
        }

        holder.itemView.findViewById<ImageButton>(R.id.imageButtonWoodenLogPackageListItemDetails).apply {
           setOnClickListener { (context as AppActivityManager).goToActivity(plankPackagesList[position].id) }
        }

        holder.itemView.findViewById<ImageButton>(R.id.imageButtonWoodenLogPackageListItemDeleteItem).apply {
            setOnClickListener {
                val constraintLayoutLogPackageItemListWrapper = holder.itemView.findViewById<ConstraintLayout>(R.id.constraintLayoutLogPackageItemListWrapper)
                constraintLayoutLogPackageItemListWrapper.background = context.resources.getDrawable(R.drawable.rounded_red_bg, null)
                val alertDialog = AlertDialog.Builder(context)
                alertDialog.setTitle(R.string.delete_package_question)

                alertDialog.setNegativeButton(R.string.cancel) {_: DialogInterface, _: Int ->}

                alertDialog.setPositiveButton(R.string.delete){_: DialogInterface, _: Int ->
                    val app = context as AppActivityManager
                    app.removeItem(plankPackagesList[position].id)
                }
                alertDialog.setOnDismissListener {
                    constraintLayoutLogPackageItemListWrapper.background = context.resources.getDrawable(R.drawable.rounded_green_medium_bg, null)
                }
                alertDialog.show()
            }
        }

    }

    override fun getItemCount(): Int = plankPackagesList.size

}

class PlankPackagesListAdapterViewHolder(view: View) : RecyclerView.ViewHolder(view)