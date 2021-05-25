package com.mobile.woodmeas.controller

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.mobile.woodmeas.AppActivityManager
import com.mobile.woodmeas.R
import com.mobile.woodmeas.model.WoodenLogPackages
import java.text.DateFormat

class WoodenPackagesListAdapter (private val woodenLogPackagesList: List<WoodenLogPackages>) :
    RecyclerView.Adapter<WoodPackageListSelectViewHolder>(){

    private lateinit var context: Context

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): WoodPackageListSelectViewHolder {
        val layoutInflater : LayoutInflater = LayoutInflater.from(viewGroup.context)
        val packageItem: View = layoutInflater.inflate(R.layout.wooden_log_package_list_item, viewGroup, false)
        context = viewGroup.context
        return WoodPackageListSelectViewHolder(packageItem)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: WoodPackageListSelectViewHolder, position: Int) {
        holder.itemView.findViewById<TextView>(R.id.tvWoodenLogPackageListItemId).apply {
            text = (position + 1).toString() + ". "
        }

        holder.itemView.findViewById<TextView>(R.id.tvWoodenLogPackageListItemName).apply {
            text = woodenLogPackagesList[position].name
        }

        woodenLogPackagesList[position].addDate?.let { addDate ->
            holder.itemView.findViewById<TextView>(R.id.tvWoodenLogPackageListItemDate).apply {
                text = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(addDate)
            }
        }

        holder.itemView.findViewById<ImageButton>(R.id.imageButtonWoodenLogPackageListItemDeleteItem).apply {
            setOnClickListener {
                val alertDialog = AlertDialog.Builder(context)
                alertDialog.setTitle(R.string.delete_package_question)

                alertDialog.setNegativeButton(R.string.cancel) {_: DialogInterface, _: Int ->}

                alertDialog.setPositiveButton(R.string.delete){_: DialogInterface, _: Int ->
                    val app = context as AppActivityManager
                    app.removeItem(woodenLogPackagesList[position].id)
                }
                alertDialog.show()
            }
        }
    }

    override fun getItemCount(): Int = woodenLogPackagesList.size

}

class WoodenPackagesListAdapterViewHolder(view: View) : RecyclerView.ViewHolder(view)