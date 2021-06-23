package com.mobile.woodmeas

import android.annotation.SuppressLint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi

class InfoActivity : AppCompatActivity() {
    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        findViewById<ImageView>(R.id.imageViewTopBarNavTitle)
            .setImageDrawable(resources.getDrawable(R.drawable.ic_info_14, null))

        findViewById<TextView>(R.id.textViewTopBarNavTitle).text = resources.getText(R.string.information)

        findViewById<ImageButton>(R.id.imageBackButtonTopBarNav).setOnClickListener { this.onBackPressed() }
    }
}