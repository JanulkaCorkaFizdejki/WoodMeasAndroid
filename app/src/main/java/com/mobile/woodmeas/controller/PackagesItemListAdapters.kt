package com.mobile.woodmeas.controller

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.mobile.woodmeas.AppActivityManager
import com.mobile.woodmeas.R
import com.mobile.woodmeas.datamodel.UnitsMeasurement
import com.mobile.woodmeas.model.Plank
import com.mobile.woodmeas.model.Stack
import com.mobile.woodmeas.model.Trees
import com.mobile.woodmeas.model.WoodenLog

class PackagePlankDetailsItemAdapter(
    private val plankList: List<Plank>,
    private val trees: List<Trees>,
    private val appActivityManager: AppActivityManager,
    private val unitsMeasurement: UnitsMeasurement
    ) : RecyclerView.Adapter<PackagesItemListViewHolder>() {
    private lateinit var context: Context
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): PackagesItemListViewHolder {
        val layoutInflater : LayoutInflater = LayoutInflater.from(viewGroup.context)
        val packagePlankDetailsView: View = layoutInflater.inflate(R.layout.package_plank_details_item, viewGroup, false)
        context = viewGroup.context
        return PackagesItemListViewHolder(packagePlankDetailsView)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: PackagesItemListViewHolder, position: Int) {
        val constraintLayoutPackagePlankDetails = holder.itemView.findViewById<ConstraintLayout>(R.id.constraintLayoutPackagePlankDetails)

        // SET Background layout
        PackagesItemListViewHolder.setBaseBackgroundLayout(constraintLayoutPackagePlankDetails, position, plankList.size)

        holder.itemView.findViewById<TextView>(R.id.textViewPlankPackageDetailsId).apply {
            val textFormat = "${position + 1}."
            text = textFormat
        }

        holder.itemView.findViewById<TextView>(R.id.textViewPackagePlankDetailsLength).apply {
            val textFormat = if(unitsMeasurement == UnitsMeasurement.CM) {
                "${plankList[position].length}"
            } else {
                UnitsMeasurement.convertToInchToIntToString(plankList[position].length)
            }
            text = textFormat
        }

        holder.itemView.findViewById<TextView>(R.id.textViewPackagePlankDetailsWidth).apply {
            val textFormat = if(unitsMeasurement == UnitsMeasurement.CM) {
                "${plankList[position].width}"
            } else {
                UnitsMeasurement.convertToInchToIntToString(plankList[position].width)
            }
            text = textFormat
        }

        holder.itemView.findViewById<TextView>(R.id.textViewPackagePlankDetailsHeight).apply {
            val textFormat = if(unitsMeasurement == UnitsMeasurement.CM) {
                "${plankList[position].height}"
            } else {
                UnitsMeasurement.convertToInchToIntToString(plankList[position].height)
            }
            text = textFormat
        }

        trees.first { it.id == plankList[position].treeId }.let {
            if (it.type > 0) {
                holder.itemView.findViewById<ImageView>(R.id.imageViewPackagePlankDetailsTreeIco).apply {
                    setImageDrawable(context.resources.getDrawable(R.drawable.ic_tree_conifer_green_8, null))
                }
            }
            else {
                holder.itemView.findViewById<ImageView>(R.id.imageViewPackagePlankDetailsTreeIco).apply {
                    setImageDrawable(context.resources.getDrawable(R.drawable.ic_tree_leafy_green_8, null))
                }
            }
            holder.itemView.findViewById<TextView>(R.id.textViewPackagePlankDetailsTreeName)
                .text = it.getNameFromRes(context)
        }

        holder.itemView.findViewById<TextView>(R.id.textViewPlankPackageDetailsCubic).apply {
            val cubicCm = "%.2f".format(plankList[position].cubicCm.toFloat() / 1000000.00F)
            val textFormat =  if(unitsMeasurement == UnitsMeasurement.CM) {
                cubicCm.replace(".", ",")
            } else {
                UnitsMeasurement.convertToFootToString(cubicCm.replace(",", ".").toFloat())
            }
            text = textFormat
        }

        holder.itemView.findViewById<ImageButton>(R.id.imageButtonPackagePlankDetailsDelete).apply {


            setOnClickListener {
                PackagesItemListViewHolder.setPotentialItemRemoval(constraintLayoutPackagePlankDetails, position, plankList.size)

                val alertBuilder = AlertDialog.Builder(context)
                alertBuilder.setTitle(R.string.do_you_want_delete_element_question)
                alertBuilder.setNegativeButton(R.string.cancel) {_:DialogInterface, _:Int -> }
                alertBuilder.setPositiveButton(R.string.ok) {_:DialogInterface, _:Int ->
                    appActivityManager.removeItem(plankList[position].id)
                }
                alertBuilder.setOnDismissListener {
                    PackagesItemListViewHolder.setBaseBackgroundLayout(constraintLayoutPackagePlankDetails, position, plankList.size)
                }
                alertBuilder.show()
            }
        }

    }

    override fun getItemCount(): Int = plankList.size

}

class PackageLogDetailsItemAdapter(
    private val woodenLogList: List<WoodenLog>,
    private val trees: List<Trees>,
    private val appActivityManager: AppActivityManager,
    private val unitsMeasurement: UnitsMeasurement

) : RecyclerView.Adapter<PackagesItemListViewHolder>() {
    private lateinit var context: Context
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): PackagesItemListViewHolder {
        val layoutInflater : LayoutInflater = LayoutInflater.from(viewGroup.context)
        val packageLogDetailsView: View = layoutInflater.inflate(R.layout.package_log_details_item, viewGroup, false)
        context = viewGroup.context
        return PackagesItemListViewHolder(packageLogDetailsView)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: PackagesItemListViewHolder, position: Int) {

        val constraintLayoutPackageLogDetails = holder.itemView.findViewById<ConstraintLayout>(R.id.constraintLayoutPackageLogDetails)

        // SET Background layout
        PackagesItemListViewHolder.setBaseBackgroundLayout(constraintLayoutPackageLogDetails, position, woodenLogList.size)

        holder.itemView.findViewById<TextView>(R.id.textViewLogPackageDetailsId).apply {
            val textFormat = "${position + 1}."
            text = textFormat
        }


        holder.itemView.findViewById<TextView>(R.id.textViewPackageLogDetailsLength).apply {
            val textFormat = if(unitsMeasurement == UnitsMeasurement.CM) {
                "${woodenLogList[position].logLengthCm}"
            } else {
                UnitsMeasurement.convertToInchToIntToString(woodenLogList[position].logLengthCm)
            }
            text = textFormat
        }

        holder.itemView.findViewById<TextView>(R.id.textViewPackageLogDetailsDiameter).apply {
            val textFormat =  if(unitsMeasurement == UnitsMeasurement.CM) {
                "${woodenLogList[position].logWidthCm}"
            } else {
                UnitsMeasurement.convertToInchToIntToString(woodenLogList[position].logWidthCm)
            }
            text = textFormat
        }


        trees.first { it.id == woodenLogList[position].treeId }.let {
            if (it.type > 0) {
                holder.itemView.findViewById<ImageView>(R.id.imageViewPackageLogDetailsTreeIco).apply {
                    setImageDrawable(context.resources.getDrawable(R.drawable.ic_tree_conifer_green_8, null))
                }
            }
            else {
                holder.itemView.findViewById<ImageView>(R.id.imageViewPackageLogDetailsTreeIco).apply {
                    setImageDrawable(context.resources.getDrawable(R.drawable.ic_tree_leafy_green_8, null))
                }
            }
            holder.itemView.findViewById<TextView>(R.id.textViewPackageLogDetailsTreeName)
                .text = it.getNameFromRes(context)
        }

        holder.itemView.findViewById<TextView>(R.id.textViewLogPackageDetailsCubic).apply {
            val cubicCm = "%.2f".format(woodenLogList[position].cubicCm.toFloat() / 1000000.00F)
            val textFormat =  if(unitsMeasurement == UnitsMeasurement.CM) {
                cubicCm.replace(".", ",")
            } else {
                UnitsMeasurement.convertToFootToString(cubicCm.replace(",", ".").toFloat())
            }
          text = textFormat
        }

        holder.itemView.findViewById<ImageView>(R.id.imageViewPackageLogDetailsBarOnOff).let {
            if (woodenLogList[position].barkOn > 0) {
                it.setImageDrawable(context.getDrawable(R.drawable.ic_bark_on_8))
                it.alpha = 1.0F
            }
            else {
                it.setImageDrawable(context.getDrawable(R.drawable.ic_bark_off_8))
                it.alpha = 0.5F
            }
        }


        holder.itemView.findViewById<ImageButton>(R.id.imageButtonPackageLogDetailsDelete).apply {

            setOnClickListener {
                PackagesItemListViewHolder.setPotentialItemRemoval(constraintLayoutPackageLogDetails, position, woodenLogList.size)

                val alertBuilder = AlertDialog.Builder(context)
                alertBuilder.setTitle(R.string.do_you_want_delete_element_question)
                alertBuilder.setNegativeButton(R.string.cancel) {_:DialogInterface, _:Int -> }
                alertBuilder.setPositiveButton(R.string.ok) {_:DialogInterface, _:Int ->
                    appActivityManager.removeItem(woodenLogList[position].id)
                }
                alertBuilder.setOnDismissListener {
                    PackagesItemListViewHolder.setBaseBackgroundLayout(constraintLayoutPackageLogDetails, position, woodenLogList.size)
                }
                alertBuilder.show()
            }
        }
    }

    override fun getItemCount(): Int = woodenLogList.size

}

class PackageStackDetailsItemAdapter(
    private val stackList: List<Stack>,
    private val trees: List<Trees>,
    private val appActivityManager: AppActivityManager,
    private val unitsMeasurement: UnitsMeasurement
) : RecyclerView.Adapter<PackagesItemListViewHolder>() {
    private lateinit var context: Context
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): PackagesItemListViewHolder {
        val layoutInflater : LayoutInflater = LayoutInflater.from(viewGroup.context)
        val packageStackDetailsView: View = layoutInflater.inflate(R.layout.package_stack_details_item, viewGroup, false)
        context = viewGroup.context
        return PackagesItemListViewHolder(packageStackDetailsView)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: PackagesItemListViewHolder, position: Int) {
        val constraintLayoutPackagePlankDetails: ConstraintLayout = holder.itemView.findViewById(R.id.constraintLayoutPackageStackDetails)

        PackagesItemListViewHolder.setBaseBackgroundLayout(constraintLayoutPackagePlankDetails, position, stackList.size)

        holder.itemView.findViewById<TextView>(R.id.textViewStackPackageDetailsId).apply {
            val textFormat = "${position + 1}."
            text = textFormat
        }

        holder.itemView.findViewById<TextView>(R.id.textViewPackageStackDetailsLength).apply {
            val textFormat = if(unitsMeasurement == UnitsMeasurement.CM) {
                "${stackList[position].length}"
            } else {
                UnitsMeasurement.convertToInchToIntToString(stackList[position].length)
            }
            text = textFormat
        }

        holder.itemView.findViewById<TextView>(R.id.textViewPackageStackDetailsWidth).apply {
            val textFormat = if(unitsMeasurement == UnitsMeasurement.CM) {
                "${stackList[position].width}"
            } else {
                UnitsMeasurement.convertToInchToIntToString(stackList[position].width)
            }
            text = textFormat
        }

        holder.itemView.findViewById<TextView>(R.id.textViewPackageStackDetailsHeight).apply {
            val textFormat = if(unitsMeasurement == UnitsMeasurement.CM) {
                "${stackList[position].height}"
            } else {
                UnitsMeasurement.convertToInchToIntToString(stackList[position].height)
            }
            text = textFormat
        }

        holder.itemView.findViewById<ImageView>(R.id.imageViewPackageStackDetailsCrossIco).apply {
            if (stackList[position].cross > 0) {
                setImageDrawable(context.resources.getDrawable(R.drawable.ic_cross_on_8, null))
            }

            else {
                setImageDrawable(context.resources.getDrawable(R.drawable.ic_cross_off_8, null))
                alpha = 0.5F
            }

        }


        trees.first { it.id == stackList[position].treeId }.let {
            if (it.type > 0) {
                holder.itemView.findViewById<ImageView>(R.id.imageViewPackageStackDetailsTreeIco).apply {
                    setImageDrawable(context.resources.getDrawable(R.drawable.ic_tree_conifer_green_8, null))
                }
            }
            holder.itemView.findViewById<TextView>(R.id.textViewPackageStackDetailsTreeName)
                .text = it.getNameFromRes(context)
        }

        holder.itemView.findViewById<TextView>(R.id.textViewStackPackageDetailsCubic).apply {
            val cubicCm = "%.2f".format(stackList[position].cubicCm.toFloat() / 100.00F)
            val textFormat =  if(unitsMeasurement == UnitsMeasurement.CM) { cubicCm.replace(".", ",")
            } else {
                UnitsMeasurement.convertToFootToString(cubicCm.replace(",", ".").toFloat())
            }
            text = textFormat
        }


        holder.itemView.findViewById<ImageButton>(R.id.imageButtonPackageStackDetailsDelete).apply {

            setOnClickListener {
                PackagesItemListViewHolder.setPotentialItemRemoval(constraintLayoutPackagePlankDetails, position, stackList.size)

                val alertBuilder = AlertDialog.Builder(context)
                alertBuilder.setTitle(R.string.do_you_want_delete_element_question)
                alertBuilder.setNegativeButton(R.string.cancel) {_:DialogInterface, _:Int -> }
                alertBuilder.setPositiveButton(R.string.ok) {_:DialogInterface, _:Int ->
                    appActivityManager.removeItem(stackList[position].id)
                }
                alertBuilder.setOnDismissListener {
                    PackagesItemListViewHolder.setBaseBackgroundLayout(constraintLayoutPackagePlankDetails, position, stackList.size)
                }
                alertBuilder.show()
            }
        }

    }

    override fun getItemCount(): Int = stackList.size

}


class PackagesItemListViewHolder(view: View): RecyclerView.ViewHolder(view) {
    companion object {
        fun setBaseBackgroundLayout(constraintLayout: ConstraintLayout, position: Int, packageSize:  Int) {
            if (packageSize == 1) { constraintLayout.setBackgroundResource(R.drawable.rounded_white_bg) }
            else if (packageSize == 2) {
                if (position == 0) { constraintLayout.setBackgroundResource(R.drawable.rounded_top_white_bg) }
                else { constraintLayout.setBackgroundResource(R.drawable.rounded_white_bottom_bg) }
            }
            else {
                when (position) {
                    0 -> constraintLayout.setBackgroundResource(R.drawable.rounded_top_white_bg)
                    packageSize - 1 -> constraintLayout.setBackgroundResource(R.drawable.rounded_white_bottom_bg)
                    else -> constraintLayout.setBackgroundResource(R.drawable.rectangle_white_bg)
                }
            }
        }

        fun setPotentialItemRemoval(constraintLayout: ConstraintLayout, position: Int, packageSize:  Int) {
            if (packageSize == 1) {
                constraintLayout.setBackgroundResource(R.drawable.rounded_red_light)
            }
            else if (packageSize == 2) {
                if (position == 0) { constraintLayout.setBackgroundResource(R.drawable.rounded_red_light_top_bg) }
                else { constraintLayout.setBackgroundResource(R.drawable.rounded_red_light_bottom_bg) }
            }
            else {
                when (position) {
                    0 -> constraintLayout.setBackgroundResource(R.drawable.rounded_red_light_top_bg)
                    packageSize - 1 -> constraintLayout.setBackgroundResource(R.drawable.rounded_red_light_bottom_bg)
                    else ->  constraintLayout.setBackgroundResource(R.drawable.rectangle_red_light_bg)
                }
            }
        }
    }
}