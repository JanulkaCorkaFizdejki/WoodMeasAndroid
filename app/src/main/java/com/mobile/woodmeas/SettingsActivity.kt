package com.mobile.woodmeas

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.mobile.woodmeas.model.DatabaseManagerDao
import com.mobile.woodmeas.model.SettingsDb
import java.lang.Exception
import kotlin.concurrent.thread

class SettingsActivity : AppCompatActivity() {
    private lateinit var radioButtonMetricSystem: RadioButton
    private lateinit var radioButtonImperialSystem: RadioButton
    private lateinit var radioButtonOnOffLocation: RadioButton
    private var radioButtonStatus: Boolean = false
    private lateinit var locationManager: LocationManager
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        radioButtonMetricSystem = findViewById(R.id.radioButtonMetricSystem)
        radioButtonImperialSystem = findViewById(R.id.radioButtonImperialSystem)
        radioButtonOnOffLocation = findViewById(R.id.radioButtonOnOffLocation)

        findViewById<ImageView>(R.id.imageViewTopBarNavTitle)
            .setImageDrawable(resources.getDrawable(R.drawable.ic_settings_14, null))

        findViewById<TextView>(R.id.textViewTopBarNavTitle).text = resources.getText(R.string.settings)

        findViewById<ImageButton>(R.id.imageBackButtonTopBarNav).setOnClickListener { this.onBackPressed() }

        thread {
            DatabaseManagerDao.getDataBase(this)?.let { databaseManagerDao ->
                val settingsDb = databaseManagerDao.settingsDbDao().select()
                this.runOnUiThread {
                    if (settingsDb.typeUnits == 0) {
                        radioButtonMetricSystem.isChecked = true
                    }
                    else {
                        radioButtonImperialSystem.isChecked = true
                    }
                    radioButtonStatus = settingsDb.location > 0
                    radioButtonOnOffLocation.isChecked = radioButtonStatus
                }
            }
        }

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            ) { ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1) }

        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 1F, {

            })

        } catch (ex: Exception) {
            val alertDialog = AlertDialog.Builder(this)
            alertDialog.setTitle("Błąd")
            alertDialog.show()
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                println("LOKATION::::${location?.latitude}")
                println("LOKATION::::${location?.longitude}")
            }


        // Measurement options _____________________________________________________________________
        radioButtonMetricSystem.setOnClickListener {
           updateSettings()
        }

        radioButtonImperialSystem.setOnClickListener {
            updateSettings()
        }

        // Location options ________________________________________________________________________
        radioButtonOnOffLocation.setOnClickListener {
            radioButtonOnOffLocation.isChecked = !radioButtonStatus
            radioButtonStatus = !radioButtonStatus
            updateSettings()
        }
    }

    private fun updateSettings() {
        val settingsBd = SettingsDb(0, if (radioButtonMetricSystem.isChecked) 0 else 1, if(radioButtonOnOffLocation.isChecked) 1 else 0)
        thread {
            DatabaseManagerDao.getDataBase(this)?.settingsDbDao()?.update(settingsBd.typeUnits, settingsBd.location)
            this.runOnUiThread {
                Toast.makeText(this, R.string.updated, Toast.LENGTH_SHORT).show()
            }
        }
    }

}