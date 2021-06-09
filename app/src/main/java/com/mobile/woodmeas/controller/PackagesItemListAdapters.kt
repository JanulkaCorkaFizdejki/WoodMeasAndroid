package com.mobile.woodmeas.controller

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.graphics.Paint
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
import com.mobile.woodmeas.model.Plank
import com.mobile.woodmeas.model.Stack
import com.mobile.woodmeas.model.Trees
import com.mobile.woodmeas.model.WoodenLog
import java.text.DateFormat

class WoodenLogListAdapter (private val woodenLogList: List<WoodenLog>,
                            private val treesList: List<Trees>,
                            private val appActivityManager: AppActivityManager):
    RecyclerView.Adapter<WoodPackageListSelectViewHolder>(){

    private lateinit var context: Context
    private val deleteOnList: ArrayList<Boolean> = arrayListOf()

    init {
        for (i in woodenLogList.indices) { deleteOnList.add(false) }
    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): WoodPackageListSelectViewHolder {
        val layoutInflater : LayoutInflater = LayoutInflater.from(viewGroup.context)
        val woodLogListItem: View = layoutInflater.inflate(R.layout.wooden_log_list_item, viewGroup, false)
        context = viewGroup.context
        return WoodPackageListSelectViewHolder(woodLogListItem)
    }

    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    override fun onBindViewHolder(holder: WoodPackageListSelectViewHolder, position: Int) {
        val constraintLayoutWoodenLogListItem: ConstraintLayout = holder.itemView.findViewById(R.id.constraintLayoutWoodenLogListItem)
        val textViewWoodenLogListId: TextView       = holder.itemView.findViewById(R.id.textViewWoodenLogListId)
        val textViewWoodenLogListLength: TextView   = holder.itemView.findViewById(R.id.textViewWoodenLogListLength)
        val textViewWoodenLogListWidth: TextView    = holder.itemView.findViewById(R.id.textViewWoodenLogListWidth)
        val textViewWoodenLogListCubic: TextView    = holder.itemView.findViewById(R.id.textViewWoodenLogListCubic)
        val textViewTypeOfTree: TextView            = holder.itemView.findViewById(R.id.textViewTypeOfTree)
        val textViewWoodenLogListBarkOn: TextView   = holder.itemView.findViewById(R.id.textViewWoodenLogListBarkOn)
        val textViewWoodenLogListAddDate: TextView  = holder.itemView.findViewById(R.id.textView101)
        val imageButtonDeleteWoodenLogItem: ImageButton      = holder.itemView.findViewById(R.id.imageButtonDeleteWoodenLogItem)

        textViewWoodenLogListId.text = "${position + 1}. "
        textViewWoodenLogListLength.text = woodenLogList[position].logLengthCm.toString()
        textViewWoodenLogListWidth.text = woodenLogList[position].logWidthCm.toString()
        textViewWoodenLogListCubic.text = "%.2f".format(woodenLogList[position].cubicCm.toFloat() / 100.0F)

        treesList.first { it.id == woodenLogList[position].treeId}.let { tree ->
            textViewTypeOfTree.text = tree.name
        }

        textViewWoodenLogListBarkOn.text = if (woodenLogList[position].barkOn > 0) "Tak" else "Nie"
        woodenLogList[position].addDate?.let {
            val dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(it)
            textViewWoodenLogListAddDate.text = dateFormat
        }

        imageButtonDeleteWoodenLogItem.isEnabled = false

        imageButtonDeleteWoodenLogItem.setOnClickListener {
            appActivityManager.removeItem(woodenLogList[position].id)
            appActivityManager.loadView()
        }

        constraintLayoutWoodenLogListItem.setOnClickListener {
            if (deleteOnList[position]) {
                imageButtonDeleteWoodenLogItem.isEnabled = false
                imageButtonDeleteWoodenLogItem.alpha = 0.0F
            }
            else {
                imageButtonDeleteWoodenLogItem.isEnabled = true
                imageButtonDeleteWoodenLogItem.alpha = 1.0F
            }
            deleteOnList[position] = !deleteOnList[position]
        }

    }

    override fun getItemCount(): Int = woodenLogList.size

}

class PackagePlankDetailsItemAdapter(
    private val plankList: List<Plank>,
    private val trees: List<Trees>,
    private val appActivityManager: AppActivityManager
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

        holder.itemView.findViewById<TextView>(R.id.textViewPlankPackageDetailsId).apply {
            val textFormat = "${position + 1}."
            text = textFormat
        }

        holder.itemView.findViewById<TextView>(R.id.textViewPackagePlankDetailsLength).apply {
            val textFormat = "${plankList[position].length} cm"
            text = textFormat
        }

        holder.itemView.findViewById<TextView>(R.id.textViewPackagePlankDetailsWidth).apply {
            val textFormat = "${plankList[position].width} cm"
            text = textFormat
        }

        holder.itemView.findViewById<TextView>(R.id.textViewPackagePlankDetailsHeight).apply {
            val textFormat = "${plankList[position].height} cm"
            text = textFormat
        }

        trees.first { it.id == plankList[position].treeId }.let {
            if (it.type > 0) {
                holder.itemView.findViewById<ImageView>(R.id.imageViewPackagePlankDetailsTreeIco).apply {
                    setImageDrawable(context.resources.getDrawable(R.drawable.ic_tree_conifer_green_8, null))
                }
            }
            holder.itemView.findViewById<TextView>(R.id.textViewPackagePlankDetailsTreeName)
                .text = it.name
        }

        holder.itemView.findViewById<TextView>(R.id.textViewPlankPackageDetailsCubic).apply {
            val textFormat = "%.2f".format(plankList[position].cubicCm.toFloat() / 1000000.00F)
            text = textFormat
        }

        holder.itemView.findViewById<ImageButton>(R.id.imageButtonPackagePlankDetailsDelete).apply {
            val constraintLayoutPackagePlankDetails: ConstraintLayout = holder.itemView.findViewById(R.id.constraintLayoutPackageLogDetails)

            setOnClickListener {
                constraintLayoutPackagePlankDetails.background = context.resources.getDrawable(R.drawable.rounded_red_bg, null)

                val alertBuilder = AlertDialog.Builder(context)
                alertBuilder.setTitle(R.string.do_you_want_delete_element_question)
                alertBuilder.setNegativeButton(R.string.cancel) {_:DialogInterface, _:Int -> }
                alertBuilder.setPositiveButton(R.string.ok) {_:DialogInterface, _:Int ->
                    appActivityManager.removeItem(plankList[position].id)
                }
                alertBuilder.setOnDismissListener {
                    constraintLayoutPackagePlankDetails.background = context.resources.getDrawable(R.drawable.rounded_white_bg, null)
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
    private val appActivityManager: AppActivityManager
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

        holder.itemView.findViewById<TextView>(R.id.textViewLogPackageDetailsId).apply {
            val textFormat = "${position + 1}."
            text = textFormat
        }

        holder.itemView.findViewById<TextView>(R.id.textViewPackageLogDetailsLength).apply {
            val textFormat = "${woodenLogList[position].logLengthCm} cm"
            text = textFormat
        }

        holder.itemView.findViewById<TextView>(R.id.textViewPackageLogDetailsDiameter).apply {
            val textFormat = "${woodenLogList[position].logWidthCm} cm"
            text = textFormat
        }


        trees.first { it.id == woodenLogList[position].treeId }.let {
            if (it.type > 0) {
                holder.itemView.findViewById<ImageView>(R.id.imageViewPackageLogDetailsTreeIco).apply {
                    setImageDrawable(context.resources.getDrawable(R.drawable.ic_tree_conifer_green_8, null))
                }
            }
            holder.itemView.findViewById<TextView>(R.id.textViewPackageLogDetailsTreeName)
                .text = it.name
        }

        holder.itemView.findViewById<TextView>(R.id.textViewLogPackageDetailsCubic).apply {
            val textFormat = "%.2f".format(woodenLogList[position].cubicCm.toFloat() / 1000000.00F)
            text = textFormat
        }

        holder.itemView.findViewById<ImageView>(R.id.imageViewPackageLogDetailsBarOnOff).let {
            if (woodenLogList[position].barkOn > 0) {
                it.setImageDrawable(context.getDrawable(R.drawable.ic_bark_on_8))
            }
        }

        holder.itemView.findViewById<TextView>(R.id.textViewPackageLogDetailsBark).let {
            if (woodenLogList[position].barkOn < 1) {
                it.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            }
        }

        holder.itemView.findViewById<ImageButton>(R.id.imageButtonPackageLogDetailsDelete).apply {
            val constraintLayoutPackagePlankDetails: ConstraintLayout = holder.itemView.findViewById(R.id.constraintLayoutPackageLogDetails)

            setOnClickListener {
                constraintLayoutPackagePlankDetails.background = context.resources.getDrawable(R.drawable.rounded_red_bg, null)

                val alertBuilder = AlertDialog.Builder(context)
                alertBuilder.setTitle(R.string.do_you_want_delete_element_question)
                alertBuilder.setNegativeButton(R.string.cancel) {_:DialogInterface, _:Int -> }
                alertBuilder.setPositiveButton(R.string.ok) {_:DialogInterface, _:Int ->
                    appActivityManager.removeItem(woodenLogList[position].id)
                }
                alertBuilder.setOnDismissListener {
                    constraintLayoutPackagePlankDetails.background = context.resources.getDrawable(R.drawable.rounded_white_bg, null)
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
    private val appActivityManager: AppActivityManager
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

        holder.itemView.findViewById<TextView>(R.id.textViewStackPackageDetailsId).apply {
            val textFormat = "${position + 1}."
            text = textFormat
        }

        holder.itemView.findViewById<TextView>(R.id.textViewPackageStackDetailsLength).apply {
            val textFormat = "${"%.1f".format(stackList[position].length.toFloat() / 100.00F)} m"
            text = textFormat
        }

        holder.itemView.findViewById<TextView>(R.id.textViewPackageStackDetailsWidth).apply {
            val textFormat = "${"%.1f".format(stackList[position].width.toFloat() / 100.00F)} m"
            text = textFormat
        }

        holder.itemView.findViewById<TextView>(R.id.textViewPackageStackDetailsHeight).apply {
            val textFormat = "${"%.1f".format(stackList[position].height.toFloat() / 100.00F)} m"
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
                .text = it.name
        }

        holder.itemView.findViewById<TextView>(R.id.textViewStackPackageDetailsCubic).apply {
            val textFormat = "%.2f".format(stackList[position].cubicCm.toFloat() / 1000000.00F)
            text = textFormat
        }

//        holder.itemView.findViewById<ImageView>(R.id.imageViewPackageLogDetailsBarOnOff).let {
//            if (woodenLogList[position].barkOn > 0) {
//                it.setImageDrawable(context.getDrawable(R.drawable.ic_bark_on_8))
//            }
//        }
//
//        holder.itemView.findViewById<TextView>(R.id.textViewPackageLogDetailsBark).let {
//            if (woodenLogList[position].barkOn < 1) {
//                it.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
//            }
//        }

        holder.itemView.findViewById<ImageButton>(R.id.imageButtonPackageStackDetailsDelete).apply {
            val constraintLayoutPackagePlankDetails: ConstraintLayout = holder.itemView.findViewById(R.id.constraintLayoutPackageStackDetails)

            setOnClickListener {
                constraintLayoutPackagePlankDetails.background = context.resources.getDrawable(R.drawable.rounded_red_bg, null)

                val alertBuilder = AlertDialog.Builder(context)
                alertBuilder.setTitle(R.string.do_you_want_delete_element_question)
                alertBuilder.setNegativeButton(R.string.cancel) {_:DialogInterface, _:Int -> }
                alertBuilder.setPositiveButton(R.string.ok) {_:DialogInterface, _:Int ->
                    appActivityManager.removeItem(stackList[position].id)
                }
                alertBuilder.setOnDismissListener {
                    constraintLayoutPackagePlankDetails.background = context.resources.getDrawable(R.drawable.rounded_white_bg, null)
                }
                alertBuilder.show()
            }
        }

    }

    override fun getItemCount(): Int = stackList.size

}


class PackagesItemListViewHolder(view: View): RecyclerView.ViewHolder(view)