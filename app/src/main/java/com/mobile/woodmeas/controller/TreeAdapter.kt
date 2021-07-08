package com.mobile.woodmeas.controller

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.mobile.woodmeas.R
import com.mobile.woodmeas.model.Trees

class TreeAdapter(context: Context, resource: Int, treeList: List<Trees>) :
    ArrayAdapter<Trees>(context, resource, treeList) {

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("ViewHolder", "UseCompatLoadingForDrawables")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layout = LayoutInflater.from(context).inflate(R.layout.tree_layout_spinner, parent, false)

        getItem(position)?.let { tree ->
            val treeName: TextView = layout.findViewById(R.id.textViewLayoutSpinnerName)
            treeName.text = tree.getNameFromRes(context)

            val icon: ImageView = layout.findViewById(R.id.imageViewLayoutSpinnerIco)
            if (tree.type > 0) {
                icon.setImageDrawable(context.getDrawable(R.drawable.ic_tree_conifer_white_14))
            } else {
                icon.setImageDrawable(context.getDrawable(R.drawable.ic_tree_leafy_white_14))
            }
        }
        return layout
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val treeDropdown = LayoutInflater.from(context).inflate(R.layout.tree_dropdown_spinner, parent, false)
        getItem(position)?.let { tree ->
            val treeName: TextView = treeDropdown.findViewById(R.id.textViewTreeDropdownName)
            treeName.text = tree.getNameFromRes(context)

            val icon: ImageView = treeDropdown.findViewById(R.id.imageViewTreeDropdownIco)
            if (tree.type > 0) {
                icon.setImageDrawable(context.getDrawable(R.drawable.ic_tree_conifer_white_14))
            }
        }

        return treeDropdown
    }

}