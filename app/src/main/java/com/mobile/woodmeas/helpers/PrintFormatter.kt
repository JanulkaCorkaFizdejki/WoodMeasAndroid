package com.mobile.woodmeas.helpers

import android.graphics.*
import android.graphics.pdf.*
import android.os.Build
import android.text.TextPaint
import androidx.annotation.RequiresApi
import com.mobile.woodmeas.model.PrintTbStruct
import com.mobile.woodmeas.model.Trees
import com.mobile.woodmeas.model.WoodPackages
import com.mobile.woodmeas.model.WoodenLog
import org.apache.poi.hssf.usermodel.HSSFRichTextString
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.text.DateFormat



object PrintFormatter {

    private const val margin = 30
    private const val intervalSize = 10
    private const val lengthA4 = 595
    private const val widthA4 = 841

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun createPdfRapport(
        filePath: String,
        woodenLogList: List<WoodenLog>,
        woodPackage: WoodPackages,
        treesList: List<Trees>,
        logo: Bitmap,
        dateDocumentCreate: String)
    {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(lengthA4, widthA4, 1).create()
        val page = document.startPage(pageInfo)

        page.canvas.drawBitmap(logo, null, Rect(margin, 30, 120, 70), null)

        // Add date document create ________________________________________________________________
        TextPaint().apply {
            textSize = 8f
            color = Color.GRAY
            typeface = Typeface.create("Arial", Typeface.ITALIC)
        }.let {
            page.canvas.drawText(dateDocumentCreate, 500f, 72f, it)
        }
        // _________________________________________________________________________________________


        // Package label ___________________________________________________________________________
        TextPaint().apply {
            textSize =  8f
            color = Color.GRAY
            typeface = Typeface.DEFAULT
        }.let {
            page.canvas.drawText("PACKAGE", margin.toFloat(), 100f, it)
        }
        // _________________________________________________________________________________________

        // Package name ____________________________________________________________________________
        TextPaint().apply {
            textSize = 20f
            color = Color.BLACK
            typeface = Typeface.create("Arial", Typeface.BOLD)
        }.let {
            page.canvas.drawText(woodPackage.name,  margin.toFloat(), 120f,  it)
        }

        // Creation date____________________________________________________________________________
        TextPaint().apply {
            textSize = 6f
            color = Color.LTGRAY
            typeface = Typeface.create("Arial", Typeface.BOLD)
        }.let {
            val dateFormat = "Created: " + if (woodPackage.addDate != null)
                DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(woodPackage.addDate)
            else "////-//-//"
            page.canvas.drawText(dateFormat,  margin.toFloat(), 130f,  it)
        }

        // Update date______________________________________________________________________________
        TextPaint().apply {
            textSize = 6f
            color = Color.LTGRAY
            typeface = Typeface.create("Arial", Typeface.BOLD)
        }.let {
            woodenLogList.maxByOrNull { w-> w.id }?.let { woodenLog ->
                val dateFormat = "Update: " + if (woodenLog.addDate != null)
                DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(woodenLog.addDate)
                else "////-//-//"
                page.canvas.drawText(dateFormat,  margin.toFloat(), 140f,  it)
            }
        }

        Paint().apply {
            color = Color.LTGRAY
            strokeWidth = 4f
        }.let {
            val stopX = lengthA4.toFloat() - (margin.toFloat() * 2f)
           page.canvas.drawLine(margin.toFloat(), 150f, stopX, 150f, it)
        }

        var intervalSizeIterator = 170
        woodenLogList.forEachIndexed { index, woodenLog ->
            TextPaint().apply {
                textSize = 6f
                color = Color.DKGRAY
                typeface = Typeface.create("Arial", Typeface.NORMAL)
            }.let {
                page.canvas.drawText((index + 1).toString() + ")",  margin.toFloat(), intervalSizeIterator.toFloat(),  it)
            }

            // set length
            TextPaint().apply {
                textSize = 6f
                color = Color.DKGRAY
                typeface = Typeface.create("Arial", Typeface.NORMAL)
            }.let {
                page.canvas.drawText(woodenLog.logLengthCm.toString(),  margin.toFloat() + 30f, intervalSizeIterator.toFloat(), it)
            }

            // set width
            TextPaint().apply {
                textSize = 6f
                color = Color.DKGRAY
                typeface = Typeface.create("Arial", Typeface.NORMAL)
            }.let {
                page.canvas.drawText(woodenLog.logWidthCm.toString(),  margin.toFloat() + 60f, intervalSizeIterator.toFloat(), it)
            }

            // set cubic
            TextPaint().apply {
                textSize = 6f
                color = Color.DKGRAY
                typeface = Typeface.create("Arial", Typeface.NORMAL)
            }.let {
                val cubic = "%.2f".format(woodenLog.cubicCm.toFloat() / 100f)
                page.canvas.drawText(cubic,  margin.toFloat() + 90f, intervalSizeIterator.toFloat(), it)
            }

            // set tree name
            TextPaint().apply {
                textSize = 6f
                color = Color.DKGRAY
                typeface = Typeface.create("Arial", Typeface.NORMAL)
            }.let {
                val tree = treesList.first { t-> t.id == woodenLog.treeId }
                page.canvas.drawText(tree.name,  margin.toFloat() + 120f, intervalSizeIterator.toFloat(), it)
            }

            // set bark
            TextPaint().apply {
                textSize = 6f
                color = Color.DKGRAY
                typeface = Typeface.create("Arial", Typeface.NORMAL)
            }.let {
                page.canvas.drawText(if (woodenLog.barkOn > 0) "Y" else "N",  margin.toFloat() + 160f, intervalSizeIterator.toFloat(), it)
            }

            // set date
            TextPaint().apply {
                textSize = 6f
                color = Color.DKGRAY
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            }.let {
                val dateFormat = if (woodenLog.addDate != null)
                    DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(woodenLog.addDate)
                    else "////-//-//"
                page.canvas.drawText(dateFormat,  margin.toFloat() + 180f, intervalSizeIterator.toFloat(), it)
            }

            intervalSizeIterator += intervalSize

        }

        Paint().apply {
            color = Color.LTGRAY
            strokeWidth = 4f
        }.let {
            val stopX = lengthA4.toFloat() - (margin.toFloat() * 2f)
            val distance = (intervalSizeIterator.toFloat() + 10f)
            page.canvas.drawLine(margin.toFloat(), distance, stopX, distance, it)
        }

        // set cubic sum ___________________________________________________________________________

        TextPaint().apply {
            textSize = 16f
            color  = Color.DKGRAY
        }.let {
            val cubicSum = "%.2f".format(woodenLogList.sumOf { w->  w.cubicCm }.toFloat() / 100f)
            val distance = (intervalSizeIterator.toFloat() + 30f)

            page.canvas.drawText(cubicSum, lengthA4.toFloat() - 130f, distance, it)
        }

        TextPaint().apply {
            textSize = 10f
            color  = Color.DKGRAY
        }.let {
            val distance = (intervalSizeIterator.toFloat() + 30f)

            page.canvas.drawText("m3", lengthA4.toFloat() - 80f, distance, it)
        }

        document.finishPage(page)

        val file = File(filePath)

        try {
            document.writeTo(FileOutputStream(file))
        } catch (ex:  Exception) {
            ex.printStackTrace()
        }
    }

    fun createXlsRapport(
        filePath: String,
        woodenLogList: List<WoodenLog>,
        woodPackage: WoodPackages,
        treesList: List<Trees>,
        sheetName: String
    ) {
        val workBook = HSSFWorkbook()
        val sheet = workBook.createSheet(sheetName)

        val row1 = sheet.createRow(0)

        for (i in 0..3) {
            val cell = row1.createCell(0)
            cell.setCellValue(HSSFRichTextString("$i"))
        }

        File(filePath).let {
            val fos = FileOutputStream(it)
            workBook.write(fos)
        }

    }

    fun setFileName(woodPackageId: Int): Pair<String, String> {
        val unixTime = System.currentTimeMillis() / 1000
        return Pair(
            "${com.mobile.woodmeas.model.Settings.PDF_FILE_NAME_PREFIX}_${unixTime}_$woodPackageId.pdf",
            "${com.mobile.woodmeas.model.Settings.PDF_FILE_NAME_PREFIX}_${unixTime}_$woodPackageId.xls")
    }
}