package com.mobile.woodmeas.helpers

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.text.TextPaint
import androidx.annotation.RequiresApi
import com.mobile.woodmeas.LogPackageDetailsActivity
import com.mobile.woodmeas.R
import com.mobile.woodmeas.model.DatabaseManagerDao
import com.mobile.woodmeas.model.Trees
import com.mobile.woodmeas.model.WoodenLog
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import kotlin.concurrent.thread

object PdfPrinter {
    private const val maxFirst: Int     = 50
    private const val maxOther: Int     = 60
    private const val margin: Int       = 30
    private const val intervalSize: Int = 10
    private const val lengthA4: Int     = 595
    private const val widthA4: Int      = 841

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun test(context: Context, packageId: Int, directory: String) {
        if (context is LogPackageDetailsActivity) {
            thread {
                DatabaseManagerDao.getDataBase(context)?.let { databaseManagerDao ->
                    val packageDao = databaseManagerDao.woodenLogPackagesDao().selectItem(packageId)
                    val trees = databaseManagerDao.treesDao().selectAll()
                    databaseManagerDao.woodenLogDao().selectWithWoodPackageId(packageId).let { woodenLogList ->
                        val document = createPages(context, setListParts(woodenLogList) as List<List<WoodenLog>>, trees)
                        val filePath = directory + PrintFormatter.setFileName(packageDao.id).first
                        val file = File(filePath)

                        try {
                            document.writeTo(FileOutputStream(file))
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("ResourceType")
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun createPages(context: Context, list: List<List<WoodenLog>>, trees: List<Trees>): PdfDocument {
        val document = PdfDocument()
        var rowIndex = 1

        val bitmapLogo = BitmapFactory.decodeResource(context.resources, R.drawable.wood_meas_150)

        list.forEachIndexed { index, listItem ->
            val pageInfo = PdfDocument.PageInfo.Builder(
                lengthA4,
                widthA4, index + 1).create()

            val page = document.startPage(pageInfo)

            var intervalSizeIterator = if (index == 0) 170 else margin
            listItem.forEach { woodenLog ->
                TextPaint().apply {
                    textSize = 6f
                    color = Color.LTGRAY
                    typeface = Typeface.create("Arial", Typeface.NORMAL)
                }.let {
                    page.canvas.drawText("$rowIndex.", margin.toFloat(), intervalSizeIterator.toFloat(),it)
                }


                trees.first { it.id == woodenLog.treeId }.let { tree ->

                    TextPaint().apply {
                        textSize = 6f
                        color = Color.parseColor(context.getString(R.color.wm_green_medium))
                        typeface = Typeface.create("Arial", Typeface.NORMAL)
                    }.let { txt ->
                        page.canvas.drawText(tree.getNameFromRes(context), margin.toFloat() + 30f, intervalSizeIterator.toFloat(),txt)
                    }
                }



                TextPaint().apply {
                    textSize = 6f
                    color = Color.DKGRAY
                    typeface = Typeface.create("Arial", Typeface.NORMAL)
                }.let {
                    page.canvas.drawText(woodenLog.logLengthCm.toString(), margin.toFloat() + 80f, intervalSizeIterator.toFloat(),it)
                }

                TextPaint().apply {
                    textSize = 6f
                    color = Color.DKGRAY
                    typeface = Typeface.create("Arial", Typeface.NORMAL)
                }.let {
                    page.canvas.drawText(woodenLog.logWidthCm.toString(), margin.toFloat() + 120f, intervalSizeIterator.toFloat(),it)
                }

                TextPaint().apply {
                    textSize = 6f
                    color = Color.DKGRAY
                    typeface = Typeface.create("Arial", Typeface.NORMAL)
                }.let {
                    val format = "%.2f".format(woodenLog.cubicCm.toFloat() / 1000000f)
                    page.canvas.drawText(format, margin.toFloat() + 160f, intervalSizeIterator.toFloat(), it)
                }

                // _________________________________
                rowIndex ++
                intervalSizeIterator += intervalSize
            }

            document.finishPage(page)
        }
        return document
    }


    private fun setListParts(list: List<*>): List<List<*>> {
        if (list.size <= maxFirst)
            return listOf(list)

        if (list.size <= (maxFirst + maxOther)) {
            val first = list.subList(0, maxFirst)
            val last = list.subList(maxFirst, list.size)
            return listOf(first, last)
        }

        val outputList: MutableList<List<*>> = mutableListOf()
        outputList.add(list.subList(0, maxFirst))

        val other = list.subList(maxFirst, list.size)
        val otherCount = other.size / maxOther

        for (i in 0 until otherCount) { outputList.add(other.subList(i * maxOther, (i + 1) * maxOther)) }

        val difference = other.size - (otherCount * maxOther)
        if (difference > 0) { outputList.add(other.subList(other.size - difference, other.size)) }

        return outputList
    }
}