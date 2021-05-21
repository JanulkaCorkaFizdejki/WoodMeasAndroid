package com.mobile.woodmeas.controller

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.mobile.woodmeas.R
import com.mobile.woodmeas.datamodel.MenuType

class MenuItemsAdapter (private val menuItems: List<String>, private val menuType: MenuType): RecyclerView.Adapter<MenuItemsAdapterViewHolder>() {

    private lateinit var context: Context
    private var icoMenuItemDrawable: Drawable? = null

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): MenuItemsAdapterViewHolder {
        val layoutInflater : LayoutInflater = LayoutInflater.from(viewGroup.context)
        val menuItem: View = layoutInflater.inflate(R.layout.menu_item, viewGroup, false)
        context = viewGroup.context
        icoMenuItemDrawable = context.resources.getDrawable(
             if (menuType == MenuType.CALCULATORS) R.drawable.ic_menu_item_calculators_ico else R.drawable.ic_menu_item_packages_ico,
            null)
        return MenuItemsAdapterViewHolder(menuItem)
    }

    override fun onBindViewHolder(holder: MenuItemsAdapterViewHolder, position: Int) {
        val menuItemName = holder.itemView.findViewById<TextView>(R.id.textViewMainMenuItemName)
        menuItemName.text = menuItems[position]

        holder.itemView.findViewById<ImageView>(R.id.imageViewMainMenuItemIco).apply {
            setImageDrawable(icoMenuItemDrawable)
        }

        holder.itemView.findViewById<ConstraintLayout>(R.id.constraintLayoutMainMenuItem).apply {
            setOnClickListener {
                println("Klik")
            }
        }


    }

    override fun getItemCount(): Int = menuItems.size

}

class MenuItemsAdapterViewHolder(view: View): RecyclerView.ViewHolder(view)