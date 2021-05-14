package com.mobile.woodmeas.controller

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.mobile.woodmeas.AppActivityManager
import com.mobile.woodmeas.R
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
        for (i in woodenLogList.indices) {
            deleteOnList.add(false)
        }
        println(deleteOnList)
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
        val textViewWoodenLogListAddDate: TextView  = holder.itemView.findViewById(R.id.textViewWoodenLogListAddDate)
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



//        constraintLayoutWoodenLogListItem.setOnTouchListener(object : OnSwipeTouchListener(appCompatActivity) {
//
//            override fun onSwipeTop() {
//                super.onSwipeTop()
//                println("top")
//            }
//
//            override fun onSwipeBottom() {
//                super.onSwipeBottom()
//            }
//
//            override fun onSwipeLeft() {
//                super.onSwipeLeft()
//                buttonDeleteWoodenLogItem.visibility = View.VISIBLE
//            }
//
//            override fun onSwipeRight() {
//                super.onSwipeRight()
//                buttonDeleteWoodenLogItem.visibility = View.VISIBLE
//            }
//        })
    }

    override fun getItemCount(): Int = woodenLogList.size

}


class WoodenLogListViewHolder(view: View): RecyclerView.ViewHolder(view)