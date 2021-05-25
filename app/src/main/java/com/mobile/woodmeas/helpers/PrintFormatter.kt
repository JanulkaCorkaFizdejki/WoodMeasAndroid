package com.mobile.woodmeas.helpers

import android.graphics.*
import android.graphics.pdf.*
import android.os.Build
import android.text.TextPaint
import androidx.annotation.RequiresApi
import com.mobile.woodmeas.model.*
import org.apache.poi.hssf.usermodel.HSSFCellStyle
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.hssf.util.HSSFColor
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.xssf.usermodel.XSSFFont
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
        woodPackage: WoodenLogPackages,
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
        woodPackage: WoodenLogPackages,
        treesList: List<Trees>
    ) {
        val workBook = HSSFWorkbook()
        val sheet = workBook.createSheet(woodPackage.name)


        // header cell style _______________________________________________________________________
        val headerCellStyle = workBook.createCellStyle().apply {
            fillForegroundColor = HSSFColor.LIGHT_GREEN.index
            fillPattern = HSSFCellStyle.SOLID_FOREGROUND
            alignment = CellStyle.ALIGN_CENTER
        }

        val headerFont = workBook.createFont().apply {
            fontHeightInPoints = 10
            boldweight = XSSFFont.BOLDWEIGHT_BOLD
        }
        headerCellStyle.setFont(headerFont)
        // _________________________________________________________________________________________

        // date font style -------------------------------------------------------------------------
        val dateFont = workBook.createFont().apply {
            fontHeightInPoints = 6
        }
        val dateCellStyle = workBook.createCellStyle().apply {
            setFont(dateFont)
        }
        // _________________________________________________________________________________________

        // text center style _______________________________________________________________________
        val textCenterStyle = workBook.createCellStyle().apply {
            alignment = CellStyle.ALIGN_CENTER
        }
        // _________________________________________________________________________________________

        // cubic sum style _________________________________________________________________________
        val cubicSumFont = workBook.createFont().apply {
            fontHeightInPoints = 16
            boldweight = XSSFFont.BOLDWEIGHT_BOLD
        }

        val cubicSumStyle = workBook.createCellStyle().apply {
            setFont(cubicSumFont)
        }
        // _________________________________________________________________________________________

        // set column width
        for (i in 1..7) {
            sheet.setColumnWidth(i, 20 * 256)
        }


        // set package name
        sheet.createRow(0).let { hssfRow ->
            val cellPackageName = hssfRow.createCell(0)
            val packageName = "Package: ${woodPackage.name}"
            cellPackageName.setCellValue(packageName)
        }

        // set date created and updated
        sheet.createRow(1).let { hssfRow ->
            val cellCreatedLabel = hssfRow.createCell(0)
            cellCreatedLabel.setCellValue("Created: ")
            cellCreatedLabel.setCellStyle(dateCellStyle)
            woodPackage.addDate?.let {
                val cellCreatedDate = hssfRow.createCell(1)
                cellCreatedDate.setCellValue(DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.MEDIUM).format(it))
                cellCreatedDate.setCellStyle(dateCellStyle)

            }
        }

        sheet.createRow(2).let { hssfRow ->
            val cellUpdatedLabel = hssfRow.createCell(0)
            cellUpdatedLabel.setCellValue("Updated: ")
            cellUpdatedLabel.setCellStyle(dateCellStyle)
            woodenLogList.maxByOrNull { w-> w.id }?.let { wl ->
                wl.addDate?.let {
                    val cellUpdatedDate = hssfRow.createCell(1)
                    cellUpdatedDate.setCellValue(DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.MEDIUM).format(it))
                    cellUpdatedDate.setCellStyle(dateCellStyle)
                }
            }
        }

        sheet.createRow(3).let { hssfRow ->
            val cellId = hssfRow.createCell(0)
            cellId.setCellValue(PrintTbStruct.WoodenLogHeaderCells.ID)
            cellId.setCellStyle(headerCellStyle)

            val cellLength =  hssfRow.createCell(1)
            cellLength.setCellValue(PrintTbStruct.WoodenLogHeaderCells.LENGTH)
            cellLength.setCellStyle(headerCellStyle)

            val cellWidth =  hssfRow.createCell(2)
            cellWidth.setCellValue(PrintTbStruct.WoodenLogHeaderCells.WIDTH)
            cellWidth.setCellStyle(headerCellStyle)

            val cellCubic =  hssfRow.createCell(3)
            cellCubic.setCellValue(PrintTbStruct.WoodenLogHeaderCells.CUBIC)
            cellCubic.setCellStyle(headerCellStyle)

            val cellTree =  hssfRow.createCell(4)
            cellTree.setCellValue(PrintTbStruct.WoodenLogHeaderCells.TREE)
            cellTree.setCellStyle(headerCellStyle)

            val cellBark =  hssfRow.createCell(5)
            cellBark.setCellValue(PrintTbStruct.WoodenLogHeaderCells.BARK)
            cellBark.setCellStyle(headerCellStyle)

            val cellDate =  hssfRow.createCell(6)
            cellDate.setCellValue(PrintTbStruct.WoodenLogHeaderCells.DATE)
            cellDate.setCellStyle(headerCellStyle)
        }

        woodenLogList.forEachIndexed { index, woodenLog ->
            val rowPosition = index + 4
            sheet.createRow(rowPosition).let { harrow->
                // set id
                woodenLog.id.also {
                    val cell =  harrow.createCell(0)
                    cell.setCellValue((index + 1).toDouble())
                    cell.setCellStyle(textCenterStyle)
                }

                // set length
                woodenLog.logLengthCm.also {
                    val cell =  harrow.createCell(1)
                    cell.setCellValue(it.toDouble())
                }

                // set width
                woodenLog.logWidthCm.also {
                    val cell =  harrow.createCell(2)
                    cell.setCellValue(it.toDouble())
                }

                // set cubic
                woodenLog.cubicCm.also {
                    val cell =  harrow.createCell(3)
                    val cubic = "%.2f".format(it.toDouble() / 100.00).replace(",", ".").toDouble()
                    cell.setCellValue(cubic)
                }

                // set tree
                woodenLog.treeId.also {
                    val tree = treesList.first { t-> t.id == it }
                    val cell =  harrow.createCell(4)
                    cell.setCellValue(tree.name)
                    cell.setCellStyle(textCenterStyle)
                }

                // set bark
                woodenLog.barkOn.also {
                    val barkVal = if (it > 0) "Y" else "N"
                    val cell =  harrow.createCell(5)
                    cell.setCellValue(barkVal)
                    cell.setCellStyle(textCenterStyle)
                }

                // set date
                woodenLog.addDate?.also {
                    val cell =  harrow.createCell(6)
                    cell.setCellValue(DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.MEDIUM).format(it))
                    cell.setCellStyle(dateCellStyle)
                }
            }
        }

        sheet.createRow(woodenLogList.size + 6).let { hssfRow ->
            val cell = hssfRow.createCell(3)
            val lastCell = (woodenLogList.size + 4).toString()
            cell.cellFormula = "SUM(D5:D$lastCell)"
            cell.setCellStyle(cubicSumStyle)
        }


        File(filePath).let {
            val fos = FileOutputStream(it)
            workBook.write(fos)
        }

    }

    fun setFileName(woodPackageId: Int): Pair<String, String> {
        val unixTime = System.currentTimeMillis() / 1000
        return Pair(
            "${Settings.PDF_FILE_NAME_PREFIX}_${unixTime}_$woodPackageId.pdf",
            "${Settings.PDF_FILE_NAME_PREFIX}_${unixTime}_$woodPackageId.xls")
    }
}