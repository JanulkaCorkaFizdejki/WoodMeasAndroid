package com.mobile.woodmeas.helpers

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.mobile.woodmeas.LogPackageDetailsActivity
import com.mobile.woodmeas.PlankPackageDetailsActivity
import com.mobile.woodmeas.R
import com.mobile.woodmeas.StackPackageDetailsActivity
import com.mobile.woodmeas.datamodel.MenuItemsType
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
    fun create(context: Context, packageId: Int, directory: String): String? {
        return when (context) {
            is LogPackageDetailsActivity -> {
                val filePath = directory + PrintFormatter.setFileName(packageId, MenuItemsType.LOG).second
                DatabaseManagerDao.getDataBase(context)?.let { databaseManagerDao ->
                    val trees = databaseManagerDao.treesDao().selectAll()
                    val packageDao = databaseManagerDao.woodenLogPackagesDao().selectItem(packageId)
                    val woodenLog = databaseManagerDao.woodenLogDao().selectWithWoodPackageId(packageId)
                    val workBook = setXls(context, packageDao, woodenLog, trees)
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
                    val workBook = setXls(context, packageDao, plank, trees)
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
                    val workBook = setXls(context, packageDao, stack, trees)
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
    private fun setXls(context: Context, packageDao: WoodenLogPackages, woodenLog: List<WoodenLog>, trees: List<Trees>): HSSFWorkbook {
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
                setCellValue(context.resources.getString(R.string.length_short_cm))
                setCellStyle(headerCellStyle)
            }
            hssfRow.createCell(4).apply {
                setCellValue(context.resources.getString(R.string.diameter_short_cm))
                setCellStyle(headerCellStyle)
            }
            hssfRow.createCell(5).apply {
                setCellValue(context.resources.getString(R.string.bark_short))
                setCellStyle(headerCellStyle)
            }
            hssfRow.createCell(6).apply {
                setCellValue(context.resources.getString(R.string.m3_short))
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

                hssfRow.createCell(3).setCellValue(woodenLogItem.logLengthCm.toDouble())

                hssfRow.createCell(4).setCellValue(woodenLogItem.logWidthCm.toDouble())

                hssfRow.createCell(5).apply {
                    setCellValue(
                        if (woodenLogItem.barkOn > 1) context.resources.getString(R.string.yes_short)
                        else context.resources.getString(R.string.no_short)
                    )
                    setCellStyle(centerCellStyle)
                }

                val m3 = "%.2f".format(woodenLogItem.cubicCm.toFloat() / 1000000f).toDouble()
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
                setCellValue("%.2f".format(woodenLog.sumOf { it.cubicCm.toLong() }.toDouble() / 1000000.0).toDouble())
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
    private fun setXls(context: Context, packageDao: PlankPackages, plankList: List<Plank>, trees: List<Trees>): HSSFWorkbook {
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
                setCellValue(context.resources.getString(R.string.length_short_cm))
                setCellStyle(headerCellStyle)
            }
            hssfRow.createCell(4).apply {
                setCellValue(context.resources.getString(R.string.width_short_cm))
                setCellStyle(headerCellStyle)
            }
            hssfRow.createCell(5).apply {
                setCellValue(context.resources.getString(R.string.height_short_cm))
                setCellStyle(headerCellStyle)
            }
            hssfRow.createCell(6).apply {
                setCellValue(context.resources.getString(R.string.m3_short))
                setCellStyle(headerCellStyle)
            }
        }
        // _________________________________________________________________________________________
        plankList.forEachIndexed { index, woodenLogItem ->
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

                hssfRow.createCell(3).setCellValue(woodenLogItem.length.toDouble())

                hssfRow.createCell(4).setCellValue(woodenLogItem.width.toDouble())

                hssfRow.createCell(5).setCellValue(woodenLogItem.height.toDouble())

                val m3 = "%.2f".format(woodenLogItem.cubicCm.toFloat() / 1000000f).toDouble()
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
                setCellValue("%.2f".format(plankList.sumOf { it.cubicCm.toLong() }.toDouble() / 1000000.0).toDouble())
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
    private fun setXls(context: Context, packageDao: StackPackages, stackList: List<Stack>, trees: List<Trees>): HSSFWorkbook {
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
                setCellValue(context.resources.getString(R.string.length_short_cm))
                setCellStyle(headerCellStyle)
            }
            hssfRow.createCell(4).apply {
                setCellValue(context.resources.getString(R.string.width_short_cm))
                setCellStyle(headerCellStyle)
            }
            hssfRow.createCell(5).apply {
                setCellValue(context.resources.getString(R.string.height_short_cm))
                setCellStyle(headerCellStyle)
            }
            hssfRow.createCell(6).apply {
                setCellValue(context.resources.getString(R.string.cross_short))
                setCellStyle(headerCellStyle)
            }
            hssfRow.createCell(7).apply {
                setCellValue(context.resources.getString(R.string.m3_short))
                setCellStyle(headerCellStyle)
            }
        }
        // _________________________________________________________________________________________
        stackList.forEachIndexed { index, woodenLogItem ->
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

                hssfRow.createCell(3).setCellValue(woodenLogItem.length.toDouble())

                hssfRow.createCell(4).setCellValue(woodenLogItem.width.toDouble())

                hssfRow.createCell(5).setCellValue(woodenLogItem.height.toDouble())

                hssfRow.createCell(6).apply {
                    setCellValue(
                        if (woodenLogItem.cross > 1) context.resources.getString(R.string.yes_short)
                        else context.resources.getString(R.string.no_short)
                    )
                    setCellStyle(centerCellStyle)
                }

                val m3 = "%.2f".format(woodenLogItem.cubicCm.toFloat() / 1000000f).toDouble()
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
                setCellValue("%.2f".format(stackList.sumOf { it.cubicCm.toLong() }.toDouble() / 1000000.0).toDouble())
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