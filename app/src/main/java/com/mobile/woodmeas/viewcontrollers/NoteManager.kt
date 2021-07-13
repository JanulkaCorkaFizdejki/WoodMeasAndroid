package com.mobile.woodmeas.viewcontrollers

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import com.mobile.woodmeas.LogPackageDetailsActivity
import com.mobile.woodmeas.PlankPackageDetailsActivity
import com.mobile.woodmeas.R
import com.mobile.woodmeas.StackPackageDetailsActivity
import com.mobile.woodmeas.model.DatabaseManagerDao
import kotlin.concurrent.thread
import kotlin.math.max

object NoteManager {
    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun set(appCompatActivity: AppCompatActivity, packageId: Int, linearLayoutHeader: LinearLayout? = null) {
        var firstTechChanged = false
        val maxText = 500
        var visibilityNotePanel = false
        val imageButtonNoteMain = appCompatActivity.findViewById<ImageButton>(R.id.imageButtonNoteMain)
        val editTextTextMultiLineNotePanelDesc = appCompatActivity.findViewById<EditText>(R.id.editTextTextMultiLineNotePanelDesc)
        val imageButtonAddNote = appCompatActivity.findViewById<ImageButton>(R.id.imageButtonAddNote)
        val textViewNotePanelCharCounter = appCompatActivity.findViewById<TextView>(R.id.textViewNotePanelCharCounter).apply {
            text = maxText.toString()
        }
        val linearLayoutNoteWrapper = editTextTextMultiLineNotePanelDesc.parent as LinearLayout

        fun showOrHideNote(imageButton: ImageButton) {
            linearLayoutNoteWrapper.visibility = if(visibilityNotePanel) View.GONE else View.VISIBLE
            if (visibilityNotePanel) {
                imageButtonNoteMain.setImageDrawable(appCompatActivity.resources.getDrawable(R.drawable.ic_note, null))
                (appCompatActivity.getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager)?.hideSoftInputFromWindow(imageButton.windowToken, 0)
                linearLayoutHeader?.visibility = View.VISIBLE
            } else {
                imageButtonNoteMain.setImageDrawable(appCompatActivity.resources.getDrawable(R.drawable.ic_note_light_green, null))
                editTextTextMultiLineNotePanelDesc.requestFocus(editTextTextMultiLineNotePanelDesc.text.length)
                (appCompatActivity.getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager)?.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
                linearLayoutHeader?.visibility = View.GONE
            }
            visibilityNotePanel = !visibilityNotePanel
        }

        thread {
            DatabaseManagerDao.getDataBase(appCompatActivity)?.let {databaseManagerDao ->
                when (appCompatActivity) {
                    is PlankPackageDetailsActivity -> {
                        databaseManagerDao.plankPackagesDao().selectNote(packageId)?.let { note ->
                            appCompatActivity.runOnUiThread {
                                firstTechChanged = true
                                editTextTextMultiLineNotePanelDesc.setText(note)
                                editTextTextMultiLineNotePanelDesc.setSelection(note.length)
                            }
                        }
                    }
                    is StackPackageDetailsActivity -> {
                        databaseManagerDao.stackPackagesDao().selectNote(packageId)?.let { note ->
                            appCompatActivity.runOnUiThread {
                                firstTechChanged = true
                                editTextTextMultiLineNotePanelDesc.setText(note)
                                editTextTextMultiLineNotePanelDesc.setSelection(note.length)
                            }
                        }
                    }
                    is LogPackageDetailsActivity -> {
                        databaseManagerDao.woodenLogPackagesDao().selectNote(packageId)?.let { note ->
                            appCompatActivity.runOnUiThread {
                                firstTechChanged = true
                                editTextTextMultiLineNotePanelDesc.setText(note)
                                editTextTextMultiLineNotePanelDesc.setSelection(note.length)
                            }
                        }
                    }
                }
            }
        }

        imageButtonNoteMain.setOnClickListener {
            showOrHideNote(it as ImageButton)
        }

        imageButtonAddNote.setOnClickListener {
            val note = if(editTextTextMultiLineNotePanelDesc.text.isNotEmpty()) editTextTextMultiLineNotePanelDesc.text.toString().trim() else null
                when(appCompatActivity) {
                    is PlankPackageDetailsActivity  -> {
                        thread {
                            DatabaseManagerDao.getDataBase(appCompatActivity)?.let { databaseManagerDao ->
                                databaseManagerDao.plankPackagesDao().updateNote(note, packageId)
                                appCompatActivity.runOnUiThread {
                                    setAddButton(appCompatActivity, imageButtonAddNote, true)
                                    Toast.makeText(appCompatActivity, R.string.updated, Toast.LENGTH_SHORT).show()
                                    showOrHideNote(it as ImageButton)
                                }
                            }
                        }
                    }
                    is StackPackageDetailsActivity -> {
                        thread {
                            DatabaseManagerDao.getDataBase(appCompatActivity)?.let { databaseManagerDao ->
                                databaseManagerDao.stackPackagesDao().updateNote(note, packageId)
                                appCompatActivity.runOnUiThread {
                                    setAddButton(appCompatActivity, imageButtonAddNote, true)
                                    Toast.makeText(appCompatActivity, R.string.updated, Toast.LENGTH_SHORT).show()
                                    showOrHideNote(it as ImageButton)
                                }
                            }
                        }
                    }
                    is LogPackageDetailsActivity -> {
                        thread {
                            DatabaseManagerDao.getDataBase(appCompatActivity)?.let { databaseManagerDao ->
                                databaseManagerDao.woodenLogPackagesDao().updateNote(note, packageId)
                                appCompatActivity.runOnUiThread {
                                    setAddButton(appCompatActivity, imageButtonAddNote, true)
                                    Toast.makeText(appCompatActivity, R.string.updated, Toast.LENGTH_SHORT).show()
                                    showOrHideNote(it as ImageButton)
                                }
                            }
                        }
                    }
                }
        }

        editTextTextMultiLineNotePanelDesc.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, count: Int) {
                textViewNotePanelCharCounter.text = (maxText - editTextTextMultiLineNotePanelDesc.text.length).toString()
                if (!firstTechChanged) {
                    setAddButton(appCompatActivity, imageButtonAddNote, false)
                } else { setAddButton(appCompatActivity, imageButtonAddNote, true) }
                firstTechChanged = false
            }
            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setAddButton(appCompatActivity: AppCompatActivity, imageButton: ImageButton, onOff: Boolean) {
        if (onOff) {
            imageButton.setImageDrawable(appCompatActivity.resources.getDrawable(R.drawable.ic_plus_12_green, null))
            imageButton.isEnabled = false
        }
        else {
            imageButton.setImageDrawable(appCompatActivity.resources.getDrawable(R.drawable.ic_plus_12_white, null))
            imageButton.isEnabled = true
        }
    }

}