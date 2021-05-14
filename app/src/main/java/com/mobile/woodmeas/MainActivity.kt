package com.mobile.woodmeas

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import com.mobile.woodmeas.model.DatabaseManager
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    lateinit var itemVolumeCalculator: ConstraintLayout

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        itemVolumeCalculator = findViewById(R.id.itemVolumeCalculator)

        thread { dbManager() }

        println("${BuildConfig.APPLICATION_ID}.provider")

        itemVolumeCalculator.setOnClickListener {
            val intent = Intent(this, VolumeCalculatorActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
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