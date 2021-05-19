package com.mobile.woodmeas

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import com.mobile.woodmeas.model.DatabaseManager
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    lateinit var imageViewWoodMain: ImageView
    lateinit var textViewMenuItemName: TextView
    private var rotateAnimation: RotateAnimation? = null

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageViewWoodMain = findViewById(R.id.imageViewWoodMain)
        textViewMenuItemName = findViewById(R.id.textViewMenuItemName)

        textViewMenuItemName.text = "Kalkulatory"

        thread { dbManager() }




//        itemVolumeCalculator.setOnClickListener {
//            val intent = Intent(this, VolumeCalculatorActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//            startActivity(intent)
//        }
    }

    override fun onStart() {
        super.onStart()
        rotateAnimation = RotateAnimation(0F, 360f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f).apply {
            interpolator = LinearInterpolator()
            duration = 5000
            repeatCount = Animation.INFINITE
        }
        imageViewWoodMain.startAnimation(rotateAnimation)
    }

    override fun onBackPressed() {
        imageViewWoodMain.clearAnimation()
        rotateAnimation = null
    }

    fun onClickMenuButton(view: View) {
        when(view.context.resources.getResourceEntryName(view.id)) {
            "imageButtonMenuItemStack" ->  {
                textViewMenuItemName.text = "Pakiety drewna"
            }
            "imageButtonMenuItemCalculator" -> {
                textViewMenuItemName.text = "Kalkulatory"
            }

            "imageButtonMenuItemInfo" -> {

            }

            "imageButtonMenuItemSettings" -> {

            }

            else -> {
                return
            }
        }
    }


    private fun dbManager() {
        DatabaseManager.doesDatabaseExist(applicationContext).let {
            if(!it) {
                DatabaseManager.copy(applicationContext, applicationContext.applicationInfo.dataDir).let { copyResult ->
                    if (copyResult) {
                        println("Kopiowanie powiodło się")
                    }
                    else {
                        println("nIC SIĘ KURWA NIE UDAŁO")
                    }
                }
            }
        }
    }
}