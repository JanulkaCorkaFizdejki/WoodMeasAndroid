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
    fun set(appCompatActivity: AppCompatActivity, packageId: Int) {
        var firstTechChanged = false
        val maxText = 1000
        var visibilityNotePanel = false
        val imageButtonNoteMain = appCompatActivity.findViewById<ImageButton>(R.id.imageButtonNoteMain)
        val editTextTextMultiLineNotePanelDesc = appCompatActivity.findViewById<EditText>(R.id.editTextTextMultiLineNotePanelDesc)
        val imageButtonAddNote = appCompatActivity.findViewById<ImageButton>(R.id.imageButtonAddNote)
        val textViewNotePanelCharCounter = appCompatActivity.findViewById<TextView>(R.id.textViewNotePanelCharCounter).apply {
            text = maxText.toString()
        }
        val linearLayoutNoteWrapper = editTextTextMultiLineNotePanelDesc.parent as LinearLayout

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
            linearLayoutNoteWrapper.visibility = if(visibilityNotePanel) View.GONE else View.VISIBLE
            if (visibilityNotePanel) {
                imageButtonNoteMain.setImageDrawable(appCompatActivity.resources.getDrawable(R.drawable.ic_note, null))
                (appCompatActivity.getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager)?.hideSoftInputFromWindow(it.windowToken, 0)
            } else {
                imageButtonNoteMain.setImageDrawable(appCompatActivity.resources.getDrawable(R.drawable.ic_note_light_green, null))
                editTextTextMultiLineNotePanelDesc.requestFocus(editTextTextMultiLineNotePanelDesc.text.length)
               (appCompatActivity.getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager)?.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
            }
            visibilityNotePanel = !visibilityNotePanel
        }

        imageButtonAddNote.setOnClickListener {
            if (editTextTextMultiLineNotePanelDesc.text.isNotEmpty()) {
                when(appCompatActivity) {
                    is PlankPackageDetailsActivity  -> {
                        thread {
                            DatabaseManagerDao.getDataBase(appCompatActivity)?.let { databaseManagerDao ->
                                databaseManagerDao.plankPackagesDao().updateNote(editTextTextMultiLineNotePanelDesc.text.toString().trim(), packageId)
                                appCompatActivity.runOnUiThread {
                                    setAddButton(appCompatActivity, imageButtonAddNote, true)
                                    Toast.makeText(appCompatActivity, R.string.updated, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                    is StackPackageDetailsActivity -> {
                        thread {
                            DatabaseManagerDao.getDataBase(appCompatActivity)?.let { databaseManagerDao ->
                                databaseManagerDao.stackPackagesDao().updateNote(editTextTextMultiLineNotePanelDesc.text.toString().trim(), packageId)
                                appCompatActivity.runOnUiThread {
                                    setAddButton(appCompatActivity, imageButtonAddNote, true)
                                    Toast.makeText(appCompatActivity, R.string.updated, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                    is LogPackageDetailsActivity -> {
                        thread {
                            DatabaseManagerDao.getDataBase(appCompatActivity)?.let { databaseManagerDao ->
                                databaseManagerDao.woodenLogPackagesDao().updateNote(editTextTextMultiLineNotePanelDesc.text.toString().trim(), packageId)
                                appCompatActivity.runOnUiThread {
                                    setAddButton(appCompatActivity, imageButtonAddNote, true)
                                    Toast.makeText(appCompatActivity, R.string.updated, Toast.LENGTH_SHORT).show()
                                }
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
                    if (editTextTextMultiLineNotePanelDesc.text.isNotEmpty()) {
                        setAddButton(appCompatActivity, imageButtonAddNote, false)
                    } else {
                        setAddButton(appCompatActivity, imageButtonAddNote, true)
                    }

                } else {
                    setAddButton(appCompatActivity, imageButtonAddNote, true)
                }
                firstTechChanged = false
            }

            override fun afterTextChanged(p0: Editable?) {
            }

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