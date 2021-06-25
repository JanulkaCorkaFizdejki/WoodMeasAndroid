package com.mobile.woodmeas.helpers

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.mobile.woodmeas.LogPackageDetailsActivity
import com.mobile.woodmeas.PlankPackageDetailsActivity
import com.mobile.woodmeas.R
import com.mobile.woodmeas.StackPackageDetailsActivity
import com.mobile.woodmeas.datamodel.MenuItemsType
import com.mobile.woodmeas.datamodel.UnitsMeasurement
import com.mobile.woodmeas.model.*
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.hssf.util.HSSFColor
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.xssf.usermodel.XSSFFont
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.text.DateFormat

object XlsPrinter {
    @RequiresApi(Build.VERSION_CODES.N)
    fun create(context: Context, packageId: Int, directory: String, unitsMeasurement: UnitsMeasurement): String? {
        return when (context) {
            is LogPackageDetailsActivity -> {
                val filePath = directory + PrintFormatter.setFileName(packageId, MenuItemsType.LOG).second
                DatabaseManagerDao.getDataBase(context)?.let { databaseManagerDao ->
                    val trees = databaseManagerDao.treesDao().selectAll()
                    val packageDao = databaseManagerDao.woodenLogPackagesDao().selectItem(packageId)
                    val woodenLog = databaseManagerDao.woodenLogDao().selectWithWoodPackageId(packageId)
                    val workBook = setXls(context, packageDao, woodenLog, trees, unitsMeasurement)
                    val file = File(filePath)
                    try {
                        workBook.write(FileOutputStream(file))
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }
                filePath
            }
            is PlankPackageDetailsActivity -> {
                val filePath = directory + PrintFormatter.setFileName(packageId, MenuItemsType.PLANK).second
                DatabaseManagerDao.getDataBase(context)?.let { databaseManagerDao ->
                    val trees = databaseManagerDao.treesDao().selectAll()
                    val packageDao = databaseManagerDao.plankPackagesDao().selectItem(packageId)
                    val plank = databaseManagerDao.plankDao().selectWithPackageId(packageId)
                    val workBook = setXls(context, packageDao, plank, trees, unitsMeasurement)
                    val file = File(filePath)
                    try {
                        workBook.write(FileOutputStream(file))
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }
                filePath
            }
            is StackPackageDetailsActivity -> {
                val filePath = directory + PrintFormatter.setFileName(packageId, MenuItemsType.STACK).second
                DatabaseManagerDao.getDataBase(context)?.let { databaseManagerDao ->
                    val trees = databaseManagerDao.treesDao().selectAll()
                    val packageDao = databaseManagerDao.stackPackagesDao().selectItem(packageId)
                    val stack = databaseManagerDao.stackDao().selectWithPackageId(packageId)
                    val workBook = setXls(context, packageDao, stack, trees, unitsMeasurement)
                    val file = File(filePath)
                    try {
                        workBook.write(FileOutputStream(file))
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }
                filePath
            }
            else -> null
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setXls(context: Context, packageDao: WoodenLogPackages, woodenLog: List<WoodenLog>, trees: List<Trees>, unitsMeasurement: UnitsMeasurement): HSSFWorkbook {
        val workBook = HSSFWorkbook()
        val sheet = workBook.createSheet(packageDao.name)

        sheet.setColumnWidth(1, 38 * 256)
        sheet.setColumnWidth(2, 20 * 256)
        sheet.setColumnWidth(3, 16 * 256)
        sheet.setColumnWidth(4, 16 * 256)
        sheet.setColumnWidth(6, 16 * 256)

        val headerFont = workBook.createFont().apply {
            fontHeightInPoints = 7
            boldweight = XSSFFont.BOLDWEIGHT_BOLD
        }
        val headerCellStyle = workBook.createCellStyle().apply {
            alignment = CellStyle.ALIGN_CENTER
            verticalAlignment = CellStyle.VERTICAL_CENTER
            setFont(headerFont)
        }
        val boldFont = workBook.createFont().apply {
            boldweight = XSSFFont.BOLDWEIGHT_BOLD
        }
        val boldCellStyle = workBook.createCellStyle().apply {
            setFont(boldFont)
        }
        val centerCellStyle = workBook.createCellStyle().apply {
            alignment = CellStyle.ALIGN_CENTER
        }
        val italicGrayFont = workBook.createFont().apply {
            boldweight = XSSFFont.BOLDWEIGHT_BOLD
            color = HSSFColor.GREY_40_PERCENT.index
            italic = true
        }
        val italicGrayCellStyle = workBook.createCellStyle().apply {
            alignment = CellStyle.ALIGN_RIGHT
            verticalAlignment = CellStyle.VERTICAL_CENTER
            setFont(italicGrayFont)
        }
        // header __________________________________________________________________________________
        sheet.createRow(0).let { hssfRow ->
            hssfRow.height = 800

            hssfRow.createCell(0).apply {
                setCellValue(context.resources.getString(R.string.id_short))
                setCellStyle(headerCellStyle)
            }
            hssfRow.createCell(1).apply {
                setCellValue(context.resources.getString(R.string.date_short))
                setCellStyle(headerCellStyle)
            }
            hssfRow.createCell(2).apply {
                setCellValue(context.resources.getString(R.string.tree))
                setCellStyle(headerCellStyle)
            }
            hssfRow.createCell(3).apply {
                if (unitsMeasurement == UnitsMeasurement.CM) {
                    setCellValue(context.resources.getString(R.string.length_short_cm))
                } else { setCellValue(context.resources.getString(R.string.length_short_in)) }
                setCellStyle(headerCellStyle)
            }
            hssfRow.createCell(4).apply {
                if (unitsMeasurement == UnitsMeasurement.CM) {
                    setCellValue(context.resources.getString(R.string.diameter_short_cm))
                } else { setCellValue(context.resources.getString(R.string.diameter_short_in)) }
                setCellStyle(headerCellStyle)
            }
            hssfRow.createCell(5).apply {
                setCellValue(context.resources.getString(R.string.bark_short))
                setCellStyle(headerCellStyle)
            }
            hssfRow.createCell(6).apply {
                if (unitsMeasurement == UnitsMeasurement.CM) {
                    setCellValue(context.resources.getString(R.string.m3_short))
                } else { setCellValue(context.resources.getString(R.string.ft3_short)) }
                setCellStyle(headerCellStyle)
            }
        }
        // _________________________________________________________________________________________
        woodenLog.forEachIndexed { index, woodenLogItem ->
            val xlsIndex = index + 1
            sheet.createRow(xlsIndex).let { hssfRow ->
                hssfRow.createCell(0)
                    .setCellValue(xlsIndex.toDouble())

                woodenLogItem.addDate?.let { date ->
                    val dateFormat = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.MEDIUM).format(date)
                    hssfRow.createCell(1).apply {
                        setCellValue(dateFormat)
                        setCellStyle(centerCellStyle)
                    }
                }

                trees.firstOrNull { it.id == woodenLogItem.treeId }?.let { tree ->
                    hssfRow.createCell(2).apply {
                        setCellValue(tree.getNameFromRes(context))
                        setCellStyle(centerCellStyle)
                    }
                }

                if (unitsMeasurement == UnitsMeasurement.CM) {
                    hssfRow.createCell(3).setCellValue(woodenLogItem.logLengthCm.toDouble())
                    hssfRow.createCell(4).setCellValue(woodenLogItem.logWidthCm.toDouble())
                }
                else {
                    hssfRow
                        .createCell(3)
                        .setCellValue(
                            "%.2f".format(UnitsMeasurement.convertToFootToFloat(woodenLogItem.logLengthCm.toFloat()))
                                .replace(",", ".")
                                .toDouble())

                    hssfRow
                        .createCell(4)
                        .setCellValue(
                            "%.2f".format(UnitsMeasurement.convertToFootToFloat(woodenLogItem.logWidthCm.toFloat()))
                                .replace(",", ".")
                                .toDouble())
                }


                hssfRow.createCell(5).apply {
                    setCellValue(
                        if (woodenLogItem.barkOn > 1) context.resources.getString(R.string.yes_short)
                        else context.resources.getString(R.string.no_short)
                    )
                    setCellStyle(centerCellStyle)
                }

                val m3 = if(unitsMeasurement == UnitsMeasurement.CM) {
                    "%.2f".format(woodenLogItem.cubicCm.toFloat() / 1000000f)
                        .replace(",", ".")
                        .toDouble()
                }
                else {
                    "%.2f".format(UnitsMeasurement
                        .convertToFootToFloat(woodenLogItem.cubicCm.toFloat() / 1000000f))
                        .replace(",", ".").
                        toDouble()
                }
                hssfRow.createCell(6).apply {
                    setCellValue(m3)
                    setCellStyle(boldCellStyle)
                }
            }
        }

        sheet.createRow(woodenLog.size + 1).let { hssfRow ->
            hssfRow.height = 600
            hssfRow.createCell(5).apply {
                setCellValue("f(x)")
                setCellStyle(italicGrayCellStyle)
            }

            hssfRow.createCell(6).apply {
                cellFormula = "SUM(G2:G${woodenLog.size + 1})"
                val sumFont = workBook.createFont().apply {
                    boldweight = XSSFFont.BOLDWEIGHT_BOLD
                    fontHeightInPoints = 12
                    color = HSSFColor.GREY_40_PERCENT.index
                }
                val sumCellStyle = workBook.createCellStyle().apply {
                    verticalAlignment = CellStyle.VERTICAL_CENTER
                    setFont(sumFont)
                }
                setCellStyle(sumCellStyle)
            }
        }

        sheet.createRow(woodenLog.size + 2).let { hssfRow ->
            hssfRow.height = 600
            hssfRow.createCell(6).apply {
                val result = if (unitsMeasurement == UnitsMeasurement.CM) {
                    "%.2f".format(woodenLog.sumOf { it.cubicCm.toLong() }.toDouble() / 1000000.0)
                        .replace(",", ".")
                        .toDouble()
                } else {
                    "%.2f".format(UnitsMeasurement
                        .convertToFootToFloat(woodenLog.sumOf { it.cubicCm.toLong() }.toFloat() / 1000000.0f))
                        .replace(",", ".")
                        .toDouble()
                }
                setCellValue(result)
                val sumFont = workBook.createFont().apply {
                    boldweight = XSSFFont.BOLDWEIGHT_BOLD
                    fontHeightInPoints = 12
                }
                val sumCellStyle = workBook.createCellStyle().apply {
                    verticalAlignment = CellStyle.VERTICAL_CENTER
                    setFont(sumFont)
                }
                setCellStyle(sumCellStyle)
            }
        }
        return workBook
    }
    private fun setXls(context: Context, packageDao: PlankPackages, plankList: List<Plank>, trees: List<Trees>, unitsMeasurement: UnitsMeasurement): HSSFWorkbook {
        val workBook = HSSFWorkbook()
        val sheet = workBook.createSheet(packageDao.name)

        sheet.setColumnWidth(1, 38 * 256)
        sheet.setColumnWidth(2, 20 * 256)
        sheet.setColumnWidth(3, 16 * 256)
        sheet.setColumnWidth(4, 16 * 256)
        sheet.setColumnWidth(5, 16 * 256)
        sheet.setColumnWidth(6, 16 * 256)

        val headerFont = workBook.createFont().apply {
            fontHeightInPoints = 7
            boldweight = XSSFFont.BOLDWEIGHT_BOLD
        }
        val headerCellStyle = workBook.createCellStyle().apply {
            alignment = CellStyle.ALIGN_CENTER
            verticalAlignment = CellStyle.VERTICAL_CENTER
            setFont(headerFont)
        }
        val boldFont = workBook.createFont().apply {
            boldweight = XSSFFont.BOLDWEIGHT_BOLD
        }
        val boldCellStyle = workBook.createCellStyle().apply {
            setFont(boldFont)
        }
        val centerCellStyle = workBook.createCellStyle().apply {
            alignment = CellStyle.ALIGN_CENTER
        }
        val italicGrayFont = workBook.createFont().apply {
            boldweight = XSSFFont.BOLDWEIGHT_BOLD
            color = HSSFColor.GREY_40_PERCENT.index
            italic = true
        }
        val italicGrayCellStyle = workBook.createCellStyle().apply {
            alignment = CellStyle.ALIGN_RIGHT
            verticalAlignment = CellStyle.VERTICAL_CENTER
            setFont(italicGrayFont)
        }
        // header __________________________________________________________________________________
        sheet.createRow(0).let { hssfRow ->
            hssfRow.height = 800

            hssfRow.createCell(0).apply {
                setCellValue(context.resources.getString(R.string.id_short))
                setCellStyle(headerCellStyle)
            }
            hssfRow.createCell(1).apply {
                setCellValue(context.resources.getString(R.string.date_short))
                setCellStyle(headerCellStyle)
            }
            hssfRow.createCell(2).apply {
                setCellValue(context.resources.getString(R.string.tree))
                setCellStyle(headerCellStyle)
            }
            hssfRow.createCell(3).apply {
                if (unitsMeasurement == UnitsMeasurement.CM) {
                    setCellValue(context.resources.getString(R.string.length_short_cm))
                } else { setCellValue(context.resources.getString(R.string.length_short_in)) }
                setCellStyle(headerCellStyle)
            }
            hssfRow.createCell(4).apply {
                if (unitsMeasurement == UnitsMeasurement.CM) {
                    setCellValue(context.resources.getString(R.string.width_short_cm))
                } else { setCellValue(context.resources.getString(R.string.width_short_in)) }

                setCellStyle(headerCellStyle)
            }
            hssfRow.createCell(5).apply {
                if (unitsMeasurement == UnitsMeasurement.CM) {
                    setCellValue(context.resources.getString(R.string.height_short_cm))
                } else { setCellValue(context.resources.getString(R.string.height_short_in)) }

                setCellStyle(headerCellStyle)
            }
            hssfRow.createCell(6).apply {
                if (unitsMeasurement == UnitsMeasurement.CM) {
                    setCellValue(context.resources.getString(R.string.m3_short))
                } else { setCellValue(context.resources.getString(R.string.ft3_short)) }
                setCellStyle(headerCellStyle)
            }
        }
        // _________________________________________________________________________________________
        plankList.forEachIndexed { index, plankItem ->
            val xlsIndex = index + 1
            sheet.createRow(xlsIndex).let { hssfRow ->
                hssfRow.createCell(0)
                    .setCellValue(xlsIndex.toDouble())

                plankItem.addDate?.let { date ->
                    val dateFormat = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.MEDIUM).format(date)
                    hssfRow.createCell(1).apply {
                        setCellValue(dateFormat)
                        setCellStyle(centerCellStyle)
                    }
                }

                trees.firstOrNull { it.id == plankItem.treeId }?.let { tree ->
                    hssfRow.createCell(2).apply {
                        setCellValue(tree.getNameFromRes(context))
                        setCellStyle(centerCellStyle)
                    }
                }

                if (unitsMeasurement == UnitsMeasurement.CM) {
                    hssfRow.createCell(3).setCellValue(plankItem.length.toDouble())
                    hssfRow.createCell(4).setCellValue(plankItem.width.toDouble())
                    hssfRow.createCell(5).setCellValue(plankItem.height.toDouble())
                }
                else {
                    hssfRow
                        .createCell(3)
                        .setCellValue("%.2f".format(UnitsMeasurement.convertToInchToFloat(plankItem.length))
                            .replace(",", ".")
                            .toDouble())

                    hssfRow
                        .createCell(4)
                        .setCellValue("%.2f".format(UnitsMeasurement.convertToInchToFloat(plankItem.width))
                            .replace(",", ".")
                            .toDouble())

                    hssfRow
                        .createCell(5)
                        .setCellValue("%.2f".format(UnitsMeasurement.convertToInchToFloat(plankItem.height))
                            .replace(",", ".")
                            .toDouble())
                }


                val m3 = if(unitsMeasurement == UnitsMeasurement.CM) {
                    "%.2f".format(plankItem.cubicCm.toFloat() / 1000000f)
                        .replace(",", ".")
                        .toDouble()
                }
                else {
                    "%.2f".format(UnitsMeasurement
                        .convertToFootToFloat(plankItem.cubicCm.toFloat() / 1000000f))
                        .replace(",", ".")
                        .toDouble()
                }
                hssfRow.createCell(6).apply {
                    setCellValue(m3)
                    setCellStyle(boldCellStyle)
                }
            }
        }

        sheet.createRow(plankList.size + 1).let { hssfRow ->
            hssfRow.height = 600
            hssfRow.createCell(5).apply {
                setCellValue("f(x)")
                setCellStyle(italicGrayCellStyle)
            }

            hssfRow.createCell(6).apply {
                cellFormula = "SUM(G2:G${plankList.size + 1})"
                val sumFont = workBook.createFont().apply {
                    boldweight = XSSFFont.BOLDWEIGHT_BOLD
                    fontHeightInPoints = 12
                    color = HSSFColor.GREY_40_PERCENT.index
                }
                val sumCellStyle = workBook.createCellStyle().apply {
                    verticalAlignment = CellStyle.VERTICAL_CENTER
                    setFont(sumFont)
                }
                setCellStyle(sumCellStyle)
            }
        }

        sheet.createRow(plankList.size + 2).let { hssfRow ->
            hssfRow.height = 600
            hssfRow.createCell(6).apply {
                val result = if (unitsMeasurement == UnitsMeasurement.CM) {
                    "%.2f".format(plankList.sumOf { it.cubicCm.toLong() }.toDouble() / 1000000.0)
                        .replace(",", ".")
                        .toDouble()
                } else {
                    "%.2f".format(UnitsMeasurement
                        .convertToFootToFloat(plankList.sumOf { it.cubicCm.toLong() }.toFloat() / 1000000.0f))
                        .replace(",", ".")
                        .toDouble()
                }
                setCellValue(result)
                val sumFont = workBook.createFont().apply {
                    boldweight = XSSFFont.BOLDWEIGHT_BOLD
                    fontHeightInPoints = 12
                }
                val sumCellStyle = workBook.createCellStyle().apply {
                    verticalAlignment = CellStyle.VERTICAL_CENTER
                    setFont(sumFont)
                }
                setCellStyle(sumCellStyle)
            }
        }
        return workBook
    }
    private fun setXls(context: Context, packageDao: StackPackages, stackList: List<Stack>, trees: List<Trees>, unitsMeasurement: UnitsMeasurement): HSSFWorkbook {
        val workBook = HSSFWorkbook()
        val sheet = workBook.createSheet(packageDao.name)
        sheet.setColumnWidth(1, 38 * 256)
        sheet.setColumnWidth(2, 20 * 256)
        sheet.setColumnWidth(3, 16 * 256)
        sheet.setColumnWidth(4, 16 * 256)
        sheet.setColumnWidth(5, 16 * 256)
        sheet.setColumnWidth(7, 16 * 256)

        val headerFont = workBook.createFont().apply {
            fontHeightInPoints = 7
            boldweight = XSSFFont.BOLDWEIGHT_BOLD
        }
        val headerCellStyle = workBook.createCellStyle().apply {
            alignment = CellStyle.ALIGN_CENTER
            verticalAlignment = CellStyle.VERTICAL_CENTER
            setFont(headerFont)
        }
        val boldFont = workBook.createFont().apply {
            boldweight = XSSFFont.BOLDWEIGHT_BOLD
        }
        val boldCellStyle = workBook.createCellStyle().apply {
            setFont(boldFont)
        }
        val centerCellStyle = workBook.createCellStyle().apply {
            alignment = CellStyle.ALIGN_CENTER
        }
        val italicGrayFont = workBook.createFont().apply {
            boldweight = XSSFFont.BOLDWEIGHT_BOLD
            color = HSSFColor.GREY_40_PERCENT.index
            italic = true
        }
        val italicGrayCellStyle = workBook.createCellStyle().apply {
            alignment = CellStyle.ALIGN_RIGHT
            verticalAlignment = CellStyle.VERTICAL_CENTER
            setFont(italicGrayFont)
        }
        // header __________________________________________________________________________________
        sheet.createRow(0).let { hssfRow ->
            hssfRow.height = 800

            hssfRow.createCell(0).apply {
                setCellValue(context.resources.getString(R.string.id_short))
                setCellStyle(headerCellStyle)
            }
            hssfRow.createCell(1).apply {
                setCellValue(context.resources.getString(R.string.date_short))
                setCellStyle(headerCellStyle)
            }
            hssfRow.createCell(2).apply {
                setCellValue(context.resources.getString(R.string.tree))
                setCellStyle(headerCellStyle)
            }
            hssfRow.createCell(3).apply {
                if (unitsMeasurement == UnitsMeasurement.CM) {
                    setCellValue(context.resources.getString(R.string.length_short_cm))
                } else {setCellValue(context.resources.getString(R.string.length_short_in))}
                setCellStyle(headerCellStyle)
            }
            hssfRow.createCell(4).apply {
                if (unitsMeasurement == UnitsMeasurement.CM) {
                    setCellValue(context.resources.getString(R.string.width_short_cm))
                } else { setCellValue(context.resources.getString(R.string.width_short_in)) }
                setCellStyle(headerCellStyle)
            }
            hssfRow.createCell(5).apply {
                if (unitsMeasurement == UnitsMeasurement.CM) {
                    setCellValue(context.resources.getString(R.string.height_short_cm))
                } else { setCellValue(context.resources.getString(R.string.height_short_in)) }
                setCellStyle(headerCellStyle)
            }
            hssfRow.createCell(6).apply {
                setCellValue(context.resources.getString(R.string.cross_short))
                setCellStyle(headerCellStyle)
            }
            hssfRow.createCell(7).apply {
                if (unitsMeasurement == UnitsMeasurement.CM) {
                    setCellValue(context.resources.getString(R.string.m3_short))
                } else { setCellValue(context.resources.getString(R.string.ft3_short))}
                setCellStyle(headerCellStyle)
            }
        }
        // _________________________________________________________________________________________
        stackList.forEachIndexed { index, stackItem ->
            val xlsIndex = index + 1
            sheet.createRow(xlsIndex).let { hssfRow ->
                hssfRow.createCell(0)
                    .setCellValue(xlsIndex.toDouble())

                stackItem.addDate?.let { date ->
                    val dateFormat = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.MEDIUM).format(date)
                    hssfRow.createCell(1).apply {
                        setCellValue(dateFormat)
                        setCellStyle(centerCellStyle)
                    }
                }

                trees.firstOrNull { it.id == stackItem.treeId }?.let { tree ->
                    hssfRow.createCell(2).apply {
                        setCellValue(tree.getNameFromRes(context))
                        setCellStyle(centerCellStyle)
                    }
                }

                if (unitsMeasurement == UnitsMeasurement.CM) {
                    hssfRow.createCell(3).setCellValue(stackItem.length.toDouble())
                    hssfRow.createCell(4).setCellValue(stackItem.width.toDouble())
                    hssfRow.createCell(5).setCellValue(stackItem.height.toDouble())
                } else {
                    hssfRow
                        .createCell(3)
                        .setCellValue("%.2f".format(UnitsMeasurement.convertToInchToFloat(stackItem.length))
                            .replace(",", ".")
                            .toDouble())
                    hssfRow
                        .createCell(4)
                        .setCellValue("%.2f".format(UnitsMeasurement.convertToInchToFloat(stackItem.width))
                            .replace(",", ".")
                            .toDouble())
                    hssfRow
                        .createCell(5)
                        .setCellValue("%.2f".format(UnitsMeasurement.convertToInchToFloat(stackItem.height))
                            .replace(",", ".")
                            .toDouble())
                }


                hssfRow.createCell(6).apply {
                    setCellValue(
                        if (stackItem.cross > 1) context.resources.getString(R.string.yes_short)
                        else context.resources.getString(R.string.no_short)
                    )
                    setCellStyle(centerCellStyle)
                }

                val m3 = if(unitsMeasurement == UnitsMeasurement.CM) {
                    "%.2f".format(stackItem.cubicCm.toFloat() / 100.00F)
                        .replace(",", ".")
                        .toDouble()
                }
                else {
                    "%.2f".format(UnitsMeasurement
                        .convertToFootToFloat(stackItem.cubicCm.toFloat() / 100.00F))
                        .replace(",", ".")
                        .toDouble()
                }
                hssfRow.createCell(7).apply {
                    setCellValue(m3)
                    setCellStyle(boldCellStyle)
                }
            }
        }

        sheet.createRow(stackList.size + 1).let { hssfRow ->
            hssfRow.height = 600
            hssfRow.createCell(6).apply {
                setCellValue("f(x)")
                setCellStyle(italicGrayCellStyle)
            }

            hssfRow.createCell(7).apply {
                cellFormula = "SUM(H2:H${stackList.size + 1})"
                val sumFont = workBook.createFont().apply {
                    boldweight = XSSFFont.BOLDWEIGHT_BOLD
                    fontHeightInPoints = 12
                    color = HSSFColor.GREY_40_PERCENT.index
                }
                val sumCellStyle = workBook.createCellStyle().apply {
                    verticalAlignment = CellStyle.VERTICAL_CENTER
                    setFont(sumFont)
                }
                setCellStyle(sumCellStyle)
            }
        }

        sheet.createRow(stackList.size + 2).let { hssfRow ->
            hssfRow.height = 600
            hssfRow.createCell(7).apply {
                val result = if (unitsMeasurement == UnitsMeasurement.CM) {
                    "%.2f".format(stackList.sumOf { it.cubicCm.toLong() }.toDouble() / 100.00F)
                        .replace(",", ".")
                        .toDouble()
                } else {
                    "%.2f".format(UnitsMeasurement
                        .convertToFootToFloat(stackList.sumOf { it.cubicCm.toLong() }.toFloat() / 100.00F))
                        .replace(",", ".")
                        .toDouble()
                }
                setCellValue(result)
                val sumFont = workBook.createFont().apply {
                    boldweight = XSSFFont.BOLDWEIGHT_BOLD
                    fontHeightInPoints = 12
                }
                val sumCellStyle = workBook.createCellStyle().apply {
                    verticalAlignment = CellStyle.VERTICAL_CENTER
                    setFont(sumFont)
                }
                setCellStyle(sumCellStyle)
            }
        }
        return workBook
    }
}