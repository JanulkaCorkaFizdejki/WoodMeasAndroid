package com.mobile.woodmeas

import android.annotation.SuppressLint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import com.mobile.woodmeas.model.DatabaseManagerDao
import com.mobile.woodmeas.model.SettingsDb
import kotlin.concurrent.thread

class SettingsActivity : AppCompatActivity() {
    private lateinit var radioButtonMetricSystem: RadioButton
    private lateinit var radioButtonImperialSystem: RadioButton
  //  private lateinit var radioButtonOnOffLocation: RadioButton
   // private var radioButtonStatus: Boolean = false
   // private lateinit var locationManager: LocationManager
   // private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var checkBoxAddNoteToPdf: CheckBox
    private lateinit var checkBoxAddNoteToEmail: CheckBox

    private var currentButtonMeasMethod = false

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        radioButtonMetricSystem = findViewById(R.id.radioButtonMetricSystem)
        radioButtonImperialSystem = findViewById(R.id.radioButtonImperialSystem)
       // radioButtonOnOffLocation = findViewById(R.id.radioButtonOnOffLocation)
        checkBoxAddNoteToPdf = findViewById(R.id.checkBoxAddNoteToPdf)
        checkBoxAddNoteToEmail = findViewById(R.id.checkBoxAddNoteToEmail)

        findViewById<ImageView>(R.id.imageViewTopBarNavTitle)
            .setImageDrawable(resources.getDrawable(R.drawable.ic_settings_14, null))

        findViewById<TextView>(R.id.textViewTopBarNavTitle).text = resources.getText(R.string.settings)

        findViewById<ImageButton>(R.id.imageBackButtonTopBarNav).setOnClickListener { this.onBackPressed() }

        thread {
            DatabaseManagerDao.getDataBase(this)?.let { databaseManagerDao ->
                val settingsDb = databaseManagerDao.settingsDbDao().select()

                currentButtonMeasMethod = settingsDb.logMeasMethod > 0

                this.runOnUiThread {
                    if (settingsDb.typeUnits == 0) {
                        radioButtonMetricSystem.isChecked = true
                    }
                    else {
                        radioButtonImperialSystem.isChecked = true
                    }
                    checkBoxAddNoteToEmail.isChecked = settingsDb.addNoteToEmail > 0
                    checkBoxAddNoteToPdf.isChecked = settingsDb.addNoteToPdf > 0

                    if (settingsDb.logMeasMethod == 0) {
                        findViewById<ImageButton>(R.id.imageButtonEndMeasMethod).apply {
                            setBackgroundResource(R.drawable.rounded_left_green_light)
                            setImageDrawable(resources.getDrawable(R.drawable.ic_top_meas, null))
                        }
                        findViewById<ImageButton>(R.id.imageButtonCenterMeasMethod).apply {
                            setBackgroundResource(R.drawable.rounded_right_white)
                            setImageDrawable(resources.getDrawable(R.drawable.ic_center_meas_green_light, null))
                        }
                    }

                    else {
                        findViewById<ImageButton>(R.id.imageButtonEndMeasMethod).apply {
                            setBackgroundResource(R.drawable.rounded_left_white)
                            setImageDrawable(resources.getDrawable(R.drawable.ic_top_meas_green_light, null))
                        }
                        findViewById<ImageButton>(R.id.imageButtonCenterMeasMethod).apply {
                            setBackgroundResource(R.drawable.rounded_right_green_light)
                            setImageDrawable(resources.getDrawable(R.drawable.ic_center_meas, null))
                        }
                    }

                   // radioButtonStatus = settingsDb.location > 0
                   // radioButtonOnOffLocation.isChecked = radioButtonStatus
                }
            }
        }

//        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
//
//        if (ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//            ) { ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1) }
//
//        try {
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 1F, {
//
//            })
//
//        } catch (ex: Exception) {
//            val alertDialog = AlertDialog.Builder(this)
//            alertDialog.setTitle("Błąd")
//            alertDialog.show()
//        }
//
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
//
//        fusedLocationClient.lastLocation
//            .addOnSuccessListener { location : Location? ->
//                println("LOKATION::::${location?.latitude}")
//                println("LOKATION::::${location?.longitude}")
//            }


        // Measurement options _____________________________________________________________________
        radioButtonMetricSystem.setOnClickListener {
           updateSettings()
        }

        radioButtonImperialSystem.setOnClickListener {
            updateSettings()
        }

        // Location options ________________________________________________________________________
//        radioButtonOnOffLocation.setOnClickListener {
//            radioButtonOnOffLocation.isChecked = !radioButtonStatus
//            radioButtonStatus = !radioButtonStatus
//            updateSettings()
//        }
    }

   fun checkboxNoteOnClick(view: View) { (view as? CheckBox)?.let { _ -> updateSettings() } }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun setLogMeasMethod(view: View) {
        (view as? ImageButton)?.let { imageButton ->
            when(imageButton.id) {
                R.id.imageButtonCenterMeasMethod -> {
                    if(!currentButtonMeasMethod) return

                    imageButton.apply {
                        setBackgroundResource(R.drawable.rounded_right_white)
                        setImageDrawable(resources.getDrawable(R.drawable.ic_center_meas_green_light, null))
                    }

                    findViewById<ImageButton>(R.id.imageButtonEndMeasMethod).apply {
                        setBackgroundResource(R.drawable.rounded_left_green_light)
                        setImageDrawable(resources.getDrawable(R.drawable.ic_top_meas, null))
                    }

                    currentButtonMeasMethod = false
                }

                R.id.imageButtonEndMeasMethod -> {
                    if(currentButtonMeasMethod) return

                    imageButton.apply {
                        setBackgroundResource(R.drawable.rounded_left_white)
                        setImageDrawable(resources.getDrawable(R.drawable.ic_top_meas_green_light, null))
                    }

                    findViewById<ImageButton>(R.id.imageButtonCenterMeasMethod).apply {
                        setBackgroundResource(R.drawable.rounded_right_green_light)
                        setImageDrawable(resources.getDrawable(R.drawable.ic_center_meas, null))
                    }

                    currentButtonMeasMethod = true
                }
            }
            updateSettings()
        }
    }

    private fun updateSettings() {
        val settingsBd = SettingsDb(0,
            if (radioButtonMetricSystem.isChecked) 0 else 1,
            0,
            if (checkBoxAddNoteToEmail.isChecked) 1 else 0,
            if(checkBoxAddNoteToPdf.isChecked) 1 else 0,
            if (currentButtonMeasMethod) 1 else 0
            )
        thread {
            DatabaseManagerDao.getDataBase(this)?.settingsDbDao()?.update(
                settingsBd.typeUnits,
                settingsBd.location,
                settingsBd.addNoteToEmail,
                settingsBd.addNoteToPdf,
                settingsBd.logMeasMethod)
            this.runOnUiThread {
                Toast.makeText(this, R.string.updated, Toast.LENGTH_SHORT).show()
            }
        }
    }

}