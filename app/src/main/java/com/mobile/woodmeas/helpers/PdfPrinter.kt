package com.mobile.woodmeas.helpers

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.text.TextPaint
import androidx.annotation.RequiresApi
import com.mobile.woodmeas.LogPackageDetailsActivity
import com.mobile.woodmeas.PlankPackageDetailsActivity
import com.mobile.woodmeas.R
import com.mobile.woodmeas.StackPackageDetailsActivity
import com.mobile.woodmeas.datamodel.MenuItemsType
import com.mobile.woodmeas.datamodel.UnitsMeasurement
import com.mobile.woodmeas.model.*
import com.mobile.woodmeas.model.Stack
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.text.DateFormat
import java.util.*
import kotlin.concurrent.thread

object PdfPrinter {
    private const val maxFirst: Int     = 55
    private const val maxOther: Int     = 70
    private const val margin: Int       = 30
    private const val intervalSize: Int = 10
    private const val lengthA4: Int     = 595
    private const val widthA4: Int      = 841

    @RequiresApi(Build.VERSION_CODES.N)
    fun create(context: Context, packageId: Int, directory: String, unitsMeasurement: UnitsMeasurement): String? {
        return when (context) {
            is LogPackageDetailsActivity -> {
                val filePath = directory + PrintFormatter.setFileName(packageId, MenuItemsType.LOG).first
                    DatabaseManagerDao.getDataBase(context)?.let { databaseManagerDao ->
                        val packageDao = databaseManagerDao.woodenLogPackagesDao().selectItem(packageId)
                        val trees = databaseManagerDao.treesDao().selectAll()
                        databaseManagerDao.woodenLogDao().selectWithWoodPackageId(packageId).let { woodenLogList ->
                            val lastUpdateDate = woodenLogList.maxByOrNull { it.id }?.addDate
                            val sum: Long = woodenLogList.sumOf { it.cubicCm.toLong() }
                            val sumFormat = if (unitsMeasurement == UnitsMeasurement.CM) {
                                "%.2f".format(sum.toFloat() / 1000000F).replace(".", ",")
                            }
                            else {
                                UnitsMeasurement.convertToFootToString("%.2f".format(sum.toFloat() / 1000000F).replace(",", ".").toFloat())
                            }
                            val document = createPages(context, setListParts(woodenLogList) as List<List<WoodenLog>>, trees, packageDao, lastUpdateDate, sumFormat, unitsMeasurement)
                            val file = File(filePath)
                            try {
                                document.writeTo(FileOutputStream(file))
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                            }
                        }
                    }
                filePath
            }
            is StackPackageDetailsActivity -> {
                val filePath = directory + PrintFormatter.setFileName(packageId, MenuItemsType.STACK).first
                    DatabaseManagerDao.getDataBase(context)?.let { databaseManagerDao ->
                        val packageDao = databaseManagerDao.stackPackagesDao().selectItem(packageId)
                        val trees = databaseManagerDao.treesDao().selectAll()
                        databaseManagerDao.stackDao().selectWithPackageId(packageId).let { stackList ->
                            val lastUpdateDate = stackList.maxByOrNull { it.id }?.addDate
                            val sum: Long = stackList.sumOf { it.cubicCm.toLong() }
                            val sumFormat = if (unitsMeasurement == UnitsMeasurement.CM) {
                                "%.2f".format(sum.toFloat() / 100.00F).replace(".", ",")
                            }
                            else {
                                UnitsMeasurement.convertToFootToString("%.2f".format(sum.toFloat() / 100.00F).replace(",", ".").toFloat())
                            }
                            val document = createPages(context, setListParts(stackList) as List<List<Stack>>, trees, packageDao, lastUpdateDate, sumFormat, unitsMeasurement)
                            val file = File(filePath)
                            try {
                                document.writeTo(FileOutputStream(file))
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                            }
                        }
                    }
                filePath
            }
            is PlankPackageDetailsActivity -> {
                val filePath = directory + PrintFormatter.setFileName(packageId, MenuItemsType.PLANK).first
                    DatabaseManagerDao.getDataBase(context)?.let { databaseManagerDao ->
                        val packageDao = databaseManagerDao.plankPackagesDao().selectItem(packageId)
                        val trees = databaseManagerDao.treesDao().selectAll()
                        databaseManagerDao.plankDao().selectWithPackageId(packageId).let { plankList ->
                            val lastUpdateDate = plankList.maxByOrNull { it.id }?.addDate
                            val sum: Long = plankList.sumOf { it.cubicCm.toLong() }
                            val sumFormat = if (unitsMeasurement == UnitsMeasurement.CM) {
                                "%.2f".format(sum.toFloat() / 1000000F).replace(".", ",")
                            }
                            else {
                                UnitsMeasurement.convertToFootToString("%.2f".format(sum.toFloat() / 1000000F).replace(",", ".").toFloat())
                            }
                            val document = createPages(context, setListParts(plankList) as List<List<Plank>>, trees, packageDao, lastUpdateDate, sumFormat, unitsMeasurement)
                            val file = File(filePath)
                            try {
                                document.writeTo(FileOutputStream(file))
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                            }
                        }
                    }
                filePath
            }
            else -> null
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("ResourceType")
    private fun createPages(
        context: Context,
        list: List<List<WoodenLog>>,
        trees: List<Trees>,
        packageDao: WoodenLogPackages,
        lastUpdateDate: Date?,
        m3Sum: String,
        unitsMeasurement: UnitsMeasurement): PdfDocument {

        val document = PdfDocument()
        var rowIndex = 1

        val textPaintDarkGray6 = TextPaint().apply {
            textSize = 6f
            color = Color.DKGRAY
            typeface = Typeface.create("Arial", Typeface.NORMAL)
        }

        val textPaintGray6 = TextPaint().apply {
            textSize = 6f
            color = Color.GRAY
            typeface = Typeface.create("Arial", Typeface.NORMAL)
        }

        val textPaintDarkGray5 = TextPaint().apply {
            textSize = 5f
            color = Color.DKGRAY
            typeface = Typeface.create("Arial", Typeface.NORMAL)
        }


        list.forEachIndexed { index, listItem ->
            val pageInfo = PdfDocument.PageInfo.Builder(
                lengthA4,
                widthA4, index + 1).create()

            val page = document.startPage(pageInfo)

            if (index == 0) {
                // set header doc __________________________________________________________________________
                val bitmapLogo = BitmapFactory.decodeResource(context.resources, R.drawable.wood_meas_150)
                page.canvas.drawBitmap(bitmapLogo, null, Rect(margin, margin, 120, 70), null)


                val docDate = TimeDateFormatter.dateFromCalendarToString()
                page.canvas.drawText(docDate.uppercase(), lengthA4.toFloat() - 60f, 70f, textPaintDarkGray5 )


                val packageNameLabel = context.resources.getString(R.string.log_package)
                page.canvas.drawText(packageNameLabel.uppercase(), margin.toFloat(), 90f, textPaintDarkGray6)

                TextPaint().apply {
                    textSize = 16f
                    color = Color.DKGRAY
                    typeface = Typeface.create("Arial", Typeface.BOLD)
                }.let {
                    page.canvas.drawText(packageDao.name, margin.toFloat(), 106f, it)
                }


                val createdDate = context.resources.getString(R.string.created_date)
                page.canvas.drawText(createdDate, margin.toFloat(), 120f, textPaintDarkGray5)


                packageDao.addDate?.let { date->
                    val dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(date)
                    page.canvas.drawText(dateFormat, margin.toFloat() + 100f, 120f, textPaintDarkGray5 )
                }

                val lastUpdateDateLabel = context.resources.getString(R.string.last_update_date)
                page.canvas.drawText(lastUpdateDateLabel, margin.toFloat(), 128f, textPaintDarkGray5 )


                lastUpdateDate?.let { date ->
                    val dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(date)
                    page.canvas.drawText(dateFormat, margin.toFloat() + 100f, 128f, textPaintDarkGray5 )
                }

                Paint().apply {
                    color = Color.LTGRAY
                    strokeWidth = 4f
                }.let {
                    val stopX = lengthA4.toFloat() - margin.toFloat()
                    page.canvas.drawLine(margin.toFloat(), 150f, stopX, 150f, it)
                }

                // _________________________________________________________________________________________

                // Table header ________________________________________________________________________
                val textPaintHeaderTable = TextPaint().apply {
                    textSize = 5f
                    color = Color.DKGRAY
                    typeface = Typeface.create("Arial", Typeface.BOLD)
                }
                page.canvas.drawText(context.resources.getString(R.string.id_short), margin.toFloat(), 160f, textPaintHeaderTable)
                page.canvas.drawText(context.resources.getString(R.string.date_short), margin.toFloat() + 20f, 160f, textPaintHeaderTable)
                page.canvas.drawText(context.resources.getString(R.string.tree), margin.toFloat() + 100f, 160f, textPaintHeaderTable)
                page.canvas.drawText(context.resources.getString(R.string.bark_short), margin.toFloat() + 280f, 160f, textPaintHeaderTable)

                if (unitsMeasurement == UnitsMeasurement.IN) {
                    page.canvas.drawText(context.resources.getString(R.string.length_short_in), margin.toFloat() + 160f, 160f, textPaintHeaderTable)
                    page.canvas.drawText(context.resources.getString(R.string.diameter_short_in), margin.toFloat() + 220f, 160f, textPaintHeaderTable)
                    page.canvas.drawText(context.resources.getString(R.string.ft3_short), margin.toFloat() + 320f, 160f, textPaintHeaderTable)
                } else {
                    page.canvas.drawText(context.resources.getString(R.string.length_short_cm), margin.toFloat() + 160f, 160f, textPaintHeaderTable)
                    page.canvas.drawText(context.resources.getString(R.string.diameter_short_cm), margin.toFloat() + 220f, 160f, textPaintHeaderTable)
                    page.canvas.drawText(context.resources.getString(R.string.m3_short), margin.toFloat() + 320f, 160f, textPaintHeaderTable)
                }
                // _____________________________________________________________________________________
            }

            var intervalSizeIterator = if (index == 0) 170 else margin
            listItem.forEach { woodenLog ->

                page.canvas.drawText("$rowIndex.", margin.toFloat(), intervalSizeIterator.toFloat(),textPaintGray6)

                woodenLog.addDate?.let { date ->
                    val dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(date)
                    page.canvas.drawText(dateFormat, margin.toFloat() + 20f, intervalSizeIterator.toFloat(),textPaintGray6)
                }

                trees.first { it.id == woodenLog.treeId }.let { tree ->
                    page.canvas
                        .drawText(
                            tree.getNameFromRes(context), margin.toFloat() + 100f, intervalSizeIterator.toFloat(),textPaintDarkGray6)
                }


                if (unitsMeasurement == UnitsMeasurement.CM) {
                    page.canvas.drawText(woodenLog.logLengthCm.toString(), margin.toFloat() + 160f, intervalSizeIterator.toFloat(),textPaintDarkGray6)
                    page.canvas.drawText(woodenLog.logWidthCm.toString(), margin.toFloat() + 220f, intervalSizeIterator.toFloat(),textPaintDarkGray6)
                } else {
                    page.canvas.drawText(UnitsMeasurement.convertToInchToString(woodenLog.logLengthCm), margin.toFloat() + 160f, intervalSizeIterator.toFloat(),textPaintDarkGray6)
                    page.canvas.drawText(UnitsMeasurement.convertToInchToString(woodenLog.logWidthCm), margin.toFloat() + 220f, intervalSizeIterator.toFloat(),textPaintDarkGray6)
                }


                val bark = if(woodenLog.barkOn > 0) context.resources.getString(R.string.yes_short) else context.resources.getString(R.string.no_short)
                page.canvas.drawText(bark, margin.toFloat() + 280f, intervalSizeIterator.toFloat(), textPaintDarkGray6)

                val format = if(unitsMeasurement == UnitsMeasurement.CM)
                {"%.2f".format(woodenLog.cubicCm.toFloat() / 1000000f).replace(",", ".")}
                else {
                    UnitsMeasurement.convertToFootToString(woodenLog.cubicCm.toFloat() / 1000000f)
                }
                page.canvas.drawText(format, margin.toFloat() + 320f, intervalSizeIterator.toFloat(), textPaintDarkGray6)

                // _________________________________
                rowIndex ++
                intervalSizeIterator += intervalSize
            }
            // FOOTER ______________________________________________________________________________
            if (index == list.size - 1) {
                Paint().apply {
                    color = Color.LTGRAY
                    strokeWidth = 4f
                }.let {
                    val stopX = lengthA4.toFloat() - margin.toFloat()
                    page.canvas
                        .drawLine(
                            margin.toFloat(),
                            intervalSizeIterator.toFloat() + 2f,
                            stopX, intervalSizeIterator.toFloat() + 2f,
                            it)
                }
                TextPaint().apply {
                    textSize = 12f
                    color = Color.DKGRAY
                    typeface = Typeface.create("Arial", Typeface.BOLD)
                }.let {
                    page.canvas.drawText(m3Sum, margin.toFloat() + 320f, intervalSizeIterator.toFloat() + 20f, it)
                }
            }
            // _____________________________________________________________________________________
            document.finishPage(page)
        }
        return document
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("ResourceType")
    private fun createPages(
        context: Context,
        list: List<List<Stack>>,
        trees: List<Trees>,
        packageDao: StackPackages,
        lastUpdateDate: Date?,
        m3Sum: String,
        unitsMeasurement: UnitsMeasurement): PdfDocument {

        val document = PdfDocument()
        var rowIndex = 1

        val textPaintDarkGray6 = TextPaint().apply {
            textSize = 6f
            color = Color.DKGRAY
            typeface = Typeface.create("Arial", Typeface.NORMAL)
        }

        val textPaintGray6 = TextPaint().apply {
            textSize = 6f
            color = Color.GRAY
            typeface = Typeface.create("Arial", Typeface.NORMAL)
        }

        val textPaintDarkGray5 = TextPaint().apply {
            textSize = 5f
            color = Color.DKGRAY
            typeface = Typeface.create("Arial", Typeface.NORMAL)
        }


        list.forEachIndexed { index, listItem ->
            val pageInfo = PdfDocument.PageInfo.Builder(
                lengthA4,
                widthA4, index + 1).create()

            val page = document.startPage(pageInfo)

            if (index == 0) {
                // set header doc __________________________________________________________________________
                val bitmapLogo = BitmapFactory.decodeResource(context.resources, R.drawable.wood_meas_150)
                page.canvas.drawBitmap(bitmapLogo, null, Rect(margin, margin, 120, 70), null)


                val docDate = TimeDateFormatter.dateFromCalendarToString()
                page.canvas.drawText(docDate.uppercase(), lengthA4.toFloat() - 60f, 70f, textPaintDarkGray5 )


                val packageNameLabel = context.resources.getString(R.string.stack_package)
                page.canvas.drawText(packageNameLabel.uppercase(), margin.toFloat(), 90f, textPaintDarkGray6)

                TextPaint().apply {
                    textSize = 16f
                    color = Color.DKGRAY
                    typeface = Typeface.create("Arial", Typeface.BOLD)
                }.let {
                    page.canvas.drawText(packageDao.name, margin.toFloat(), 106f, it)
                }


                val createdDate = context.resources.getString(R.string.created_date)
                page.canvas.drawText(createdDate, margin.toFloat(), 120f, textPaintDarkGray5)


                packageDao.addDate?.let { date->
                    val dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(date)
                    page.canvas.drawText(dateFormat, margin.toFloat() + 100f, 120f, textPaintDarkGray5 )
                }

                val lastUpdateDateLabel = context.resources.getString(R.string.last_update_date)
                page.canvas.drawText(lastUpdateDateLabel, margin.toFloat(), 128f, textPaintDarkGray5 )


                lastUpdateDate?.let { date ->
                    val dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(date)
                    page.canvas.drawText(dateFormat, margin.toFloat() + 100f, 128f, textPaintDarkGray5 )
                }

                Paint().apply {
                    color = Color.LTGRAY
                    strokeWidth = 4f
                }.let {
                    val stopX = lengthA4.toFloat() - margin.toFloat()
                    page.canvas.drawLine(margin.toFloat(), 150f, stopX, 150f, it)
                }

                // _________________________________________________________________________________________

                // Table header ________________________________________________________________________
                val textPaintHeaderTable = TextPaint().apply {
                    textSize = 5f
                    color = Color.DKGRAY
                    typeface = Typeface.create("Arial", Typeface.BOLD)
                }
                page.canvas.drawText(context.resources.getString(R.string.id_short), margin.toFloat(), 160f, textPaintHeaderTable)
                page.canvas.drawText(context.resources.getString(R.string.date_short), margin.toFloat() + 20f, 160f, textPaintHeaderTable)
                page.canvas.drawText(context.resources.getString(R.string.tree), margin.toFloat() + 100f, 160f, textPaintHeaderTable)
                if(unitsMeasurement == UnitsMeasurement.CM) {
                    page.canvas.drawText(context.resources.getString(R.string.length_short_cm), margin.toFloat() + 160f, 160f, textPaintHeaderTable)
                    page.canvas.drawText(context.resources.getString(R.string.width_short_cm), margin.toFloat() + 220f, 160f, textPaintHeaderTable)
                    page.canvas.drawText(context.resources.getString(R.string.height_short_cm), margin.toFloat() + 280f, 160f, textPaintHeaderTable)
                    page.canvas.drawText(context.resources.getString(R.string.m3_short), margin.toFloat() + 380f, 160f, textPaintHeaderTable)
                } else {
                    page.canvas.drawText(context.resources.getString(R.string.length_short_in), margin.toFloat() + 160f, 160f, textPaintHeaderTable)
                    page.canvas.drawText(context.resources.getString(R.string.width_short_in), margin.toFloat() + 220f, 160f, textPaintHeaderTable)
                    page.canvas.drawText(context.resources.getString(R.string.height_short_in), margin.toFloat() + 280f, 160f, textPaintHeaderTable)
                    page.canvas.drawText(context.resources.getString(R.string.ft3_short), margin.toFloat() + 380f, 160f, textPaintHeaderTable)
                }

                page.canvas.drawText(context.resources.getString(R.string.cross_short), margin.toFloat() + 320f, 160f, textPaintHeaderTable)

                // _____________________________________________________________________________________
            }

            var intervalSizeIterator = if (index == 0) 170 else margin
            listItem.forEach { stack ->

                page.canvas.drawText("$rowIndex.", margin.toFloat(), intervalSizeIterator.toFloat(),textPaintGray6)

                stack.addDate?.let { date ->
                    val dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(date)
                    page.canvas.drawText(dateFormat, margin.toFloat() + 20f, intervalSizeIterator.toFloat(),textPaintGray6)
                }

                trees.first { it.id == stack.treeId }.let { tree ->
                    page.canvas
                        .drawText(
                            tree.getNameFromRes(context), margin.toFloat() + 100f, intervalSizeIterator.toFloat(),textPaintDarkGray6)
                }

                if (unitsMeasurement == UnitsMeasurement.CM) {
                    page.canvas.drawText(stack.length.toString(), margin.toFloat() + 160f, intervalSizeIterator.toFloat(),textPaintDarkGray6)
                    page.canvas.drawText(stack.width.toString(), margin.toFloat() + 220f, intervalSizeIterator.toFloat(),textPaintDarkGray6)
                    page.canvas.drawText(stack.height.toString(), margin.toFloat() + 280f, intervalSizeIterator.toFloat(),textPaintDarkGray6)
                } else {
                    page.canvas.drawText(UnitsMeasurement.convertToInchToString(stack.length), margin.toFloat() + 160f, intervalSizeIterator.toFloat(),textPaintDarkGray6)
                    page.canvas.drawText(UnitsMeasurement.convertToInchToString(stack.width), margin.toFloat() + 220f, intervalSizeIterator.toFloat(),textPaintDarkGray6)
                    page.canvas.drawText(UnitsMeasurement.convertToInchToString(stack.height), margin.toFloat() + 280f, intervalSizeIterator.toFloat(),textPaintDarkGray6)
                }


                val cross = if(stack.cross > 0) context.resources.getString(R.string.yes_short) else context.resources.getString(R.string.no_short)
                page.canvas.drawText(cross, margin.toFloat() + 320f, intervalSizeIterator.toFloat(), textPaintDarkGray6)

                val format = if(unitsMeasurement == UnitsMeasurement.CM)
                {"%.2f".format(stack.cubicCm.toFloat() / 100.00F).replace(",", ".")}
                else {
                    UnitsMeasurement.convertToFootToString("%.2f".format(stack.cubicCm.toFloat() / 100.00F).replace(",", ".").toFloat())
                }
                page.canvas.drawText(format, margin.toFloat() + 380f, intervalSizeIterator.toFloat(), textPaintDarkGray6)

                // _________________________________
                rowIndex ++
                intervalSizeIterator += intervalSize
            }
            // FOOTER ______________________________________________________________________________
            if (index == list.size - 1) {
                Paint().apply {
                    color = Color.LTGRAY
                    strokeWidth = 4f
                }.let {
                    val stopX = lengthA4.toFloat() - margin.toFloat()
                    page.canvas
                        .drawLine(
                            margin.toFloat(),
                            intervalSizeIterator.toFloat() + 2f,
                            stopX, intervalSizeIterator.toFloat() + 2f,
                            it)
                }
                TextPaint().apply {
                    textSize = 12f
                    color = Color.DKGRAY
                    typeface = Typeface.create("Arial", Typeface.BOLD)
                }.let {
                    page.canvas.drawText(m3Sum, margin.toFloat() + 380f, intervalSizeIterator.toFloat() + 20f, it)
                }
            }
            // _____________________________________________________________________________________
            document.finishPage(page)
        }
        return document
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("ResourceType")
    private fun createPages(
        context: Context,
        list: List<List<Plank>>,
        trees: List<Trees>,
        packageDao: PlankPackages,
        lastUpdateDate: Date?,
        m3Sum: String,
        unitsMeasurement: UnitsMeasurement): PdfDocument {

        val document = PdfDocument()
        var rowIndex = 1

        val textPaintDarkGray6 = TextPaint().apply {
            textSize = 6f
            color = Color.DKGRAY
            typeface = Typeface.create("Arial", Typeface.NORMAL)
        }

        val textPaintGray6 = TextPaint().apply {
            textSize = 6f
            color = Color.GRAY
            typeface = Typeface.create("Arial", Typeface.NORMAL)
        }

        val textPaintDarkGray5 = TextPaint().apply {
            textSize = 5f
            color = Color.DKGRAY
            typeface = Typeface.create("Arial", Typeface.NORMAL)
        }


        list.forEachIndexed { index, listItem ->
            val pageInfo = PdfDocument.PageInfo.Builder(
                lengthA4,
                widthA4, index + 1).create()

            val page = document.startPage(pageInfo)

            if (index == 0) {
                // set header doc __________________________________________________________________________
                val bitmapLogo = BitmapFactory.decodeResource(context.resources, R.drawable.wood_meas_150)
                page.canvas.drawBitmap(bitmapLogo, null, Rect(margin, margin, 120, 70), null)


                val docDate = TimeDateFormatter.dateFromCalendarToString()
                page.canvas.drawText(docDate.uppercase(), lengthA4.toFloat() - 60f, 70f, textPaintDarkGray5 )


                val packageNameLabel = context.resources.getString(R.string.plank_package)
                page.canvas.drawText(packageNameLabel.uppercase(), margin.toFloat(), 90f, textPaintDarkGray6)

                TextPaint().apply {
                    textSize = 16f
                    color = Color.DKGRAY
                    typeface = Typeface.create("Arial", Typeface.BOLD)
                }.let {
                    page.canvas.drawText(packageDao.name, margin.toFloat(), 106f, it)
                }


                val createdDate = context.resources.getString(R.string.created_date)
                page.canvas.drawText(createdDate, margin.toFloat(), 120f, textPaintDarkGray5)


                packageDao.addDate?.let { date->
                    val dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(date)
                    page.canvas.drawText(dateFormat, margin.toFloat() + 100f, 120f, textPaintDarkGray5 )
                }

                val lastUpdateDateLabel = context.resources.getString(R.string.last_update_date)
                page.canvas.drawText(lastUpdateDateLabel, margin.toFloat(), 128f, textPaintDarkGray5 )


                lastUpdateDate?.let { date ->
                    val dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(date)
                    page.canvas.drawText(dateFormat, margin.toFloat() + 100f, 128f, textPaintDarkGray5 )
                }

                Paint().apply {
                    color = Color.LTGRAY
                    strokeWidth = 4f
                }.let {
                    val stopX = lengthA4.toFloat() - margin.toFloat()
                    page.canvas.drawLine(margin.toFloat(), 150f, stopX, 150f, it)
                }

                // _________________________________________________________________________________________

                // Table header ________________________________________________________________________
                val textPaintHeaderTable = TextPaint().apply {
                    textSize = 5f
                    color = Color.DKGRAY
                    typeface = Typeface.create("Arial", Typeface.BOLD)
                }
                page.canvas.drawText(context.resources.getString(R.string.id_short), margin.toFloat(), 160f, textPaintHeaderTable)
                page.canvas.drawText(context.resources.getString(R.string.date_short), margin.toFloat() + 20f, 160f, textPaintHeaderTable)
                page.canvas.drawText(context.resources.getString(R.string.tree), margin.toFloat() + 100f, 160f, textPaintHeaderTable)
                if(unitsMeasurement == UnitsMeasurement.CM) {
                    page.canvas.drawText(context.resources.getString(R.string.length_short_cm), margin.toFloat() + 160f, 160f, textPaintHeaderTable)
                    page.canvas.drawText(context.resources.getString(R.string.width_short_cm), margin.toFloat() + 220f, 160f, textPaintHeaderTable)
                    page.canvas.drawText(context.resources.getString(R.string.height_short_cm), margin.toFloat() + 280f, 160f, textPaintHeaderTable)
                }
                else {
                    page.canvas.drawText(context.resources.getString(R.string.length_short_in), margin.toFloat() + 160f, 160f, textPaintHeaderTable)
                    page.canvas.drawText(context.resources.getString(R.string.width_short_in), margin.toFloat() + 220f, 160f, textPaintHeaderTable)
                    page.canvas.drawText(context.resources.getString(R.string.height_short_in), margin.toFloat() + 280f, 160f, textPaintHeaderTable)
                }

                // _____________________________________________________________________________________
            }

            var intervalSizeIterator = if (index == 0) 170 else margin
            listItem.forEach { plank ->

                page.canvas.drawText("$rowIndex.", margin.toFloat(), intervalSizeIterator.toFloat(),textPaintGray6)

                plank.addDate?.let { date ->
                    val dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(date)
                    page.canvas.drawText(dateFormat, margin.toFloat() + 20f, intervalSizeIterator.toFloat(),textPaintGray6)
                }

                trees.first { it.id == plank.treeId }.let { tree ->
                    page.canvas
                        .drawText(
                            tree.getNameFromRes(context), margin.toFloat() + 100f, intervalSizeIterator.toFloat(),textPaintDarkGray6)
                }

                if (unitsMeasurement == UnitsMeasurement.CM) {
                    page.canvas.drawText(plank.length.toString(), margin.toFloat() + 160f, intervalSizeIterator.toFloat(),textPaintDarkGray6)
                    page.canvas.drawText(plank.width.toString(), margin.toFloat() + 220f, intervalSizeIterator.toFloat(),textPaintDarkGray6)
                    page.canvas.drawText(plank.height.toString(), margin.toFloat() + 280f, intervalSizeIterator.toFloat(), textPaintDarkGray6)
                } else {
                    page.canvas.drawText(UnitsMeasurement.convertToInchToString(plank.length), margin.toFloat() + 160f, intervalSizeIterator.toFloat(),textPaintDarkGray6)
                    page.canvas.drawText(UnitsMeasurement.convertToInchToString(plank.width), margin.toFloat() + 220f, intervalSizeIterator.toFloat(),textPaintDarkGray6)
                    page.canvas.drawText(UnitsMeasurement.convertToInchToString(plank.height), margin.toFloat() + 280f, intervalSizeIterator.toFloat(), textPaintDarkGray6)
                }

                val format = if(unitsMeasurement == UnitsMeasurement.CM)
                {"%.2f".format(plank.cubicCm.toFloat() / 1000000f).replace(",", ".")}
                else {
                    UnitsMeasurement.convertToFootToString(plank.cubicCm.toFloat() / 1000000f)
                }
                page.canvas.drawText(format, margin.toFloat() + 320f, intervalSizeIterator.toFloat(), textPaintDarkGray6)

                // _________________________________
                rowIndex ++
                intervalSizeIterator += intervalSize
            }
            // FOOTER ______________________________________________________________________________
            if (index == list.size - 1) {
                Paint().apply {
                    color = Color.LTGRAY
                    strokeWidth = 4f
                }.let {
                    val stopX = lengthA4.toFloat() - margin.toFloat()
                    page.canvas
                        .drawLine(
                            margin.toFloat(),
                            intervalSizeIterator.toFloat() + 2f,
                            stopX, intervalSizeIterator.toFloat() + 2f,
                            it)
                }
                TextPaint().apply {
                    textSize = 12f
                    color = Color.DKGRAY
                    typeface = Typeface.create("Arial", Typeface.BOLD)
                }.let {
                    page.canvas.drawText(m3Sum, margin.toFloat() + 320f, intervalSizeIterator.toFloat() + 20f, it)
                }
            }
            // _____________________________________________________________________________________
            document.finishPage(page)
        }
        return document
    }


    private fun setListParts(list: List<*>): List<List<*>> {
        if (list.size <= maxFirst)
            return listOf(list)

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