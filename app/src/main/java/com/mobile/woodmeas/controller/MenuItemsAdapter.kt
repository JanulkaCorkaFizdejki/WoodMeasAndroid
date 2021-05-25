package com.mobile.woodmeas.controller

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mobile.woodmeas.*
import com.mobile.woodmeas.datamodel.MenuType
import com.mobile.woodmeas.viewcontrollers.OnSwipeTouchListener

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
                if (menuType == MenuType.PACKAGES) {
                    val activityClass = when(position) {
                        0 -> LogPackageListActivity::class.java
                        1 -> PlankPackageListActivity::class.java
                        else -> StackPackageListActivity::class.java
                    }
                    Intent(context, activityClass).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    }.let {
                        ContextCompat.startActivity(context, it, null)
                    }
                }
                else {
                    val activityClass = when(position) {
                        0 -> LogCalculatorActivity::class.java
                        1 -> PlankCalculatorActivity::class.java
                        else -> StackCalculatorActivity::class.java
                    }
                    Intent(context, activityClass).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    }.let {
                        ContextCompat.startActivity(context, it, null)
                    }
                }
            }


        }
    }

    override fun getItemCount(): Int = menuItems.size

}

class MenuItemsAdapterViewHolder(view: View): RecyclerView.ViewHolder(view)