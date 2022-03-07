package com.mayz.excellibrary

import android.text.TextUtils
import com.mayz.excellibrary.annotations.ExcelContent
import com.mayz.excellibrary.annotations.ExcelSheet
import com.mayz.excellibrary.annotations.ExcelSheetIndex
import org.apache.poi.hssf.usermodel.HSSFDateUtil
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.*
import java.lang.reflect.Field
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

object ExcelManagerKtn {

    private val fieldCache: MutableMap<String, Field> =HashMap()
    private val titleCache: MutableMap<Int, String> = HashMap()

    /**
     * write excel to only one sheet ,no format
     */
    @Throws(Exception::class)
    private fun toExcel(excelStream: OutputStream, dataList: List<*>?): Boolean {
        if (dataList == null || dataList.isEmpty()) {
            return false
        }
        val dataType: Class<*> = dataList[0]!!.javaClass
        val sheetName = getSheetName(dataType)
        val keys = getKeys(dataType)
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet(sheetName)
        var row = sheet.createRow(0)
        for (i in keys.indices) {
            row.createCell(i).setCellValue(keys[i].title)
        }
        fieldCache.clear()
        for (y in dataList.indices) {
            row = sheet.createRow(y + 1)
            for (x in keys.indices) {
                val fieldName = keys[x].fieldName
                val field = getField(dataType, fieldName)
                val value = field!![dataList[y]]
                val content = value?.toString() ?: ""
                row.createCell(x).setCellValue(content)
            }
        }
        workbook.write(excelStream)
        workbook.close()
        excelStream.close()
        return true
    }

    fun toExcel(fileAbsoluteName: String?, dataList: List<*>?): Boolean {
        try {
            val file = File(fileAbsoluteName)
            val folder = file.parentFile
            if (!folder.exists()) {
                folder.mkdirs()
            }
            val stream: OutputStream = FileOutputStream(file, false)
            return toExcel(stream, dataList)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    fun toExcel(file: File, dataList: List<*>?): Boolean {
        try {
            val folder = file.parentFile
            if (!folder.exists()) {
                folder.mkdirs()
            }
            val stream: OutputStream = FileOutputStream(file, false)
            return toExcel(stream, dataList)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    @Throws(Exception::class)
    fun <T> fromExcel(excelFile: File, dataType: Class<T>): List<T>? {
        var sheetName: String? = null
        var sheetIndex = 0
        try {
            sheetName = getSheetName(dataType)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            sheetIndex = getSheetIndex(dataType)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        // read map in excel
        val titleContentValues = getMapFromExcel(excelFile, sheetIndex, sheetName)
        if (titleContentValues == null || titleContentValues.isEmpty()) {
            return null
        }
        val value0 = titleContentValues[0]
        val keys = getKeys(dataType)

        //if there is no ExcelContent annotation in class ,return null
        var isExist = false
        for (kIndex in keys.indices) {
            val title = keys[kIndex].title
            if (value0.containsKey(title)) {
                isExist = true
                break
            }
        }
        if (!isExist) {
            return null
        }
        val datas: MutableList<T> = ArrayList()
        fieldCache.clear()

        // parse data from content
        for (n in titleContentValues.indices) {
            val titleContent = titleContentValues[n]
            val data = dataType.newInstance()
            for (k in keys.indices) {
                val title = keys[k].title
                val fieldName = keys[k].fieldName
                val field = getField(dataType, fieldName)
                field!![data] = titleContent[title]
            }
            datas.add(data)
        }
        return datas
    }

    private fun getKeys(clazz: Class<*>): List<ExcelClassKey> {
        val fields = clazz.declaredFields
        val keys: MutableList<ExcelClassKey> = ArrayList()
        for (i in fields.indices) {
            val content = fields[i].getAnnotation(ExcelContent::class.java)
            if (content != null) {
                keys.add(ExcelClassKey(content.titleName, fields[i].name, content.index))
            }
        }
        //sort to control the title index in excel
        keys.sortWith { t1, t2 -> t1.index - t2.index }
        return keys
    }

    @Throws(Exception::class)
    private fun getField(type: Class<*>, fieldName: String): Field? {
        var f: Field?
        if (fieldCache.containsKey(fieldName)) {
            f = fieldCache[fieldName]
        } else {
            f = type.getDeclaredField(fieldName)
            fieldCache[fieldName] = f
        }
        f!!.isAccessible = true
        return f
    }

    private fun getSheetName(clazz: Class<*>): String {
        val sheet = clazz.getAnnotation(ExcelSheet::class.java)
            ?: throw RuntimeException(clazz.simpleName + " : lost sheet name!")
        return sheet.sheetName
    }

    private fun getSheetIndex(clazz: Class<*>): Int {
        val sheet = clazz.getAnnotation(ExcelSheetIndex::class.java)
            ?: throw RuntimeException(clazz.simpleName + " : lost sheet name!")
        return sheet.sheetIndex
    }

    fun getMapFromExcel(file: File, sheetName: String?): List<Map<String?, String>>? {
        return getMapFromExcel(file, 0, sheetName)
    }

    fun getMapFromExcel(file: File, sheetIndex: Int): List<Map<String?, String>>? {
        return getMapFromExcel(file, sheetIndex, null)
    }

    private fun getMapFromExcel(
        file: File,
        sheetIndex: Int,
        sheetName: String?
    ): List<Map<String?, String>>? {
        var sheetIndex = sheetIndex
        try {
            if (sheetIndex <= 0) {
                sheetIndex = 0
            }
            val values: MutableList<Map<String?, String>> = LinkedList()
            titleCache.clear()
            val workbook = getWorkbook(file)
            val sheet: Sheet = if (sheetName != null) {
                workbook!!.getSheet(sheetName)
            } else {
                workbook!!.getSheetAt(sheetIndex)
            }
            val rowsCount = sheet.physicalNumberOfRows
            val formulaEvaluator = workbook.creationHelper.createFormulaEvaluator()
            val formulaEvaluator2 = workbook.creationHelper.createFormulaEvaluator()
            for (r in 0 until rowsCount) {
                val row = sheet.getRow(r)
                val cellsCount = row.physicalNumberOfCells
                val value: MutableMap<String?, String> = LinkedHashMap()
                for (c in 0 until cellsCount) {
                    formulaEvaluator2.evaluate(row.getCell(0))
                    val title = getExcelTitle(row, c, formulaEvaluator2)
                    val content = getContent(row, c, formulaEvaluator)
                    if (!TextUtils.isEmpty(title)) {
                        value[title] = content
                    }
                }
                values.add(value)
            }
            if (values.size > 0) {
                values.removeAt(0)
            }
            return values
        } catch (e: Exception) {
            /* proper exception handling to be here */
        }
        return null
    }

    private fun getExcelTitle(row: Row, x: Int, formulaEvaluator: FormulaEvaluator): String? {
        val title: String?
        if (titleCache.containsKey(x)) {
            title = titleCache[x]
        } else {
            title = getContent(row, x, formulaEvaluator)
            titleCache[x] = title
        }
        return title
    }

    private fun getContent(row: Row, c: Int, formulaEvaluator: FormulaEvaluator): String {
        var value = ""
        try {
            val cell = row.getCell(c)
            val cellValue = formulaEvaluator.evaluate(cell)
            when (cellValue.cellType) {
                Cell.CELL_TYPE_BOOLEAN -> value = "" + cellValue.booleanValue
                Cell.CELL_TYPE_NUMERIC -> {
                    val numericValue = cellValue.numberValue
                    value = if (HSSFDateUtil.isCellDateFormatted(cell)) {
                        val date = cellValue.numberValue
                        val formatter = SimpleDateFormat("dd/MM/yy")
                        formatter.format(HSSFDateUtil.getJavaDate(date))
                    } else {
                        "" + numericValue
                    }
                }
                Cell.CELL_TYPE_STRING -> value = "" + cellValue.stringValue
                else -> {
                }
            }
        } catch (e: NullPointerException) {
            /* proper error handling should be here */
        }
        return value
    }

    private const val EXCEL_XLS = "xls"
    private const val EXCEL_XLSX = "xlsx"

    @Throws(IOException::class)
    private fun getWorkbook(file: File): Workbook? {
        var wb: Workbook?
        val `in` = FileInputStream(file)
        wb = when {
            file.name.lowercase(Locale.getDefault()).endsWith(EXCEL_XLS) -> {     //Excel 2003
                HSSFWorkbook(`in`)
            }
            file.name.lowercase(Locale.getDefault()).endsWith(EXCEL_XLSX) -> {    // Excel 2007/2010
                XSSFWorkbook(`in`)
            }
            else -> {
                return null
            }
        }
        return wb
    }
}