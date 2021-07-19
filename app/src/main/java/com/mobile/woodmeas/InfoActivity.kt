package com.mobile.woodmeas

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.text.bold
import androidx.core.text.italic
import androidx.core.view.children
import java.util.*
import kotlin.collections.ArrayList

class InfoActivity : AppCompatActivity() {

    private lateinit var textViewInfoMeasurementDesc: TextView
    private val featuresAppTextViewList: ArrayList<TextView> = arrayListOf()
    private val woodMeasUrl = "http://woodmeas.com"

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        textViewInfoMeasurementDesc = findViewById(R.id.textViewInfoMeasurementDesc)


        findViewById<ImageView>(R.id.imageViewTopBarNavTitle)
            .setImageDrawable(resources.getDrawable(R.drawable.ic_info_14, null))

        findViewById<TextView>(R.id.textViewTopBarNavTitle).text = resources.getText(R.string.information)

        findViewById<ImageButton>(R.id.imageBackButtonTopBarNav).setOnClickListener { this.onBackPressed() }

        findViewById<LinearLayout>(R.id.linearLayoutAppFeaturesWrapper).children.forEach { view ->
            (view as? LinearLayout)?.let { linearLayout ->
                linearLayout.children.forEach { linearLayoutChildrenView ->
                    (linearLayoutChildrenView as? TextView)?.let {
                        featuresAppTextViewList.add(it)
                    }
                }
            }
        }
        val featuresStringArray = resources.getStringArray(R.array.application_features_list)
        if(featuresAppTextViewList.size == featuresStringArray.size) {
            featuresAppTextViewList.forEachIndexed { index, textView ->
                textView.text = featuresStringArray[index]
            }
        }

        // Set Text in Log Measurement Desc. _______________________________________________________
        val defaultText = textViewInfoMeasurementDesc.text.toString().split("___")
        val spannable = SpannableStringBuilder().apply {
            append(defaultText[0])
            bold {
                italic {
                    append(resources.getStringArray(R.array.measurement_logs_swap)[0])
                    }
            }
            append(defaultText[1])
            bold {
                italic {
                    append(resources.getStringArray(R.array.measurement_logs_swap)[1])
                    }
                }
            append(defaultText[2])
            }
        textViewInfoMeasurementDesc.text = spannable

        // _________________________________________________________________________________________

        // FOOTER GET QWERTY MEDIA _________________________________________________________________
        findViewById<LinearLayout>(R.id.linearLayoutQwertyMediaFooterInfo).setOnClickListener {
            Intent(Intent.ACTION_VIEW).let { intent ->
                intent.data = Uri.parse("http://qwertymedia.pl/")
                startActivity(intent)
            }
        }
        // _________________________________________________________________________________________

    }

    fun goToWoodMeasWebsite(view: View) {
        (view as? Button)?.let {
            when(it.id) {
                R.id.buttonInfoLearnMoreLog -> {
                    Intent(Intent.ACTION_VIEW).let { intent ->
                        val url = "${woodMeasUrl}/?site=wood-measurement&lang=${Locale.getDefault().language}"
                            .trim()
                            .replace(" ", "")
                        intent.data = Uri.parse(url)
                        startActivity(intent)
                    }
                }
                R.id.buttonInfoLearnMoreStack -> {
                    Intent(Intent.ACTION_VIEW).let { intent ->
                        val url = "${woodMeasUrl}/?site=wood-measurement&lang=${Locale.getDefault().language}"
                            .trim()
                            .replace(" ", "")
                        intent.data = Uri.parse(url)
                        startActivity(intent)
                    }
                }
                R.id.buttonInfoGoToWoodMeas -> {
                    Intent(Intent.ACTION_VIEW).let { intent ->
                        intent.data = Uri.parse(woodMeasUrl)
                        startActivity(intent)
                    }
                }
            }
        }
    }
}