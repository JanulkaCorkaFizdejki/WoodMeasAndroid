package com.mobile.woodmeas.controller

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.mobile.woodmeas.R
import com.mobile.woodmeas.datamodel.WoodCoefficients

class WoodCoefficientsAdapter(context: Context, resource: Int, woodCoefficientsList: List<WoodCoefficients>) :
    ArrayAdapter<WoodCoefficients>(context, resource, woodCoefficientsList) {

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("ViewHolder", "UseCompatLoadingForDrawables")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layout = LayoutInflater.from(context).inflate(R.layout.wood_coeffictients_layout_spinner, parent, false)

        getItem(position)?.let { woodCoefficients ->
            val treeName: TextView = layout.findViewById(R.id.textViewWoodCoefficientsItem)
            treeName.text = woodCoefficients.getNameType()

        }
        return layout
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val woodCoefficientDropdown = LayoutInflater.from(context).inflate(R.layout.wood_coefficients_dropdown_spinner, parent, false)
        getItem(position)?.let { woodCoefficients ->
            val treeName: TextView = woodCoefficientDropdown.findViewById(R.id.textViewWoodCoefficientsDropdownName)
            treeName.text = woodCoefficients.name
        }

        return woodCoefficientDropdown
    }

}