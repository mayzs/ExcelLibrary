package com.mayz.excellibrary;

import android.text.TextUtils;

import com.mayz.excellibrary.annotations.ExcelContent;
import com.mayz.excellibrary.annotations.ExcelSheet;
import com.mayz.excellibrary.annotations.ExcelSheetIndex;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class ExcelManager {

    private static final Map<String, Field> fieldCache = new HashMap<>();
    private static final Map<Integer, String> titleCache = new HashMap<>();

    /**
     * write excel to only one sheet ,no format
     */
    private static boolean toExcel(OutputStream excelStream, List<?> dataList) throws Exception {
        if (dataList == null || dataList.size() == 0) {
            return false;
        }
        Class<?> dataType = dataList.get(0).getClass();
        String sheetName = getSheetName(dataType);
        List<ExcelClassKey> keys = getKeys(dataType);
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(sheetName);
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < keys.size(); i++) {
            row.createCell(i).setCellValue(keys.get(i).getTitle());
        }
        fieldCache.clear();
        for (int y = 0; y < dataList.size(); y++) {
            row = sheet.createRow(y + 1);
            for (int x = 0; x < keys.size(); x++) {
                String fieldName = keys.get(x).getFieldName();

                Field field = getField(dataType, fieldName);
                Object value = field.get(dataList.get(y));
                String content = value != null ? value.toString() : "";
                row.createCell(x).setCellValue(content);
            }
        }
        workbook.write(excelStream);
        workbook.close();
        excelStream.close();
        return true;
    }

    public static boolean toExcel(String fileAbsoluteName, List<?> dataList) {
        try {
            File file = new File(fileAbsoluteName);
            File folder = file.getParentFile();
            if (!folder.exists()) {
                folder.mkdirs();
            }

            OutputStream stream = new FileOutputStream(file, false);
            return toExcel(stream, dataList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean toExcel(File file, List<?> dataList) {
        try {
            File folder = file.getParentFile();
            if (!folder.exists()) {
                folder.mkdirs();
            }
            OutputStream stream = new FileOutputStream(file, false);
            return toExcel(stream, dataList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static <T> List<T> fromExcel(File excelFile, Class<T> dataType) throws Exception {
        String sheetName = null;
        int sheetIndex = 0;
        try {
            sheetName = getSheetName(dataType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            sheetIndex = getSheetIndex(dataType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // read map in excel
        List<Map<String, String>> title_content_values = getMapFromExcel(excelFile, sheetIndex, sheetName);
        if (title_content_values == null || title_content_values.size() == 0) {
            return null;
        }

        Map<String, String> value0 = title_content_values.get(0);
        List<ExcelClassKey> keys = getKeys(dataType);

        //if there is no ExcelContent annotation in class ,return null
        boolean isExist = false;
        for (int kIndex = 0; kIndex < keys.size(); kIndex++) {
            String title = keys.get(kIndex).getTitle();
            if (value0.containsKey(title)) {
                isExist = true;
                break;
            }
        }
        if (!isExist) {
            return null;
        }

        List<T> datas = new ArrayList<>();
        fieldCache.clear();

        // parse data from content
        for (int n = 0; n < title_content_values.size(); n++) {
            Map<String, String> title_content = title_content_values.get(n);
            T data = dataType.newInstance();
            for (int k = 0; k < keys.size(); k++) {

                String title = keys.get(k).getTitle();
                String fieldName = keys.get(k).getFieldName();
                Field field = getField(dataType, fieldName);
                field.set(data, title_content.get(title));
            }
            datas.add(data);
        }
        return datas;
    }

    private static List<ExcelClassKey> getKeys(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        List<ExcelClassKey> keys = new ArrayList<>();
        for (int i = 0; i < fields.length; i++) {
            ExcelContent content = fields[i].getAnnotation(ExcelContent.class);
            if (content != null) {
                keys.add(new ExcelClassKey(content.titleName(), fields[i].getName(), content.index()));
            }
        }
        //sort to control the title index in excel
        Collections.sort(keys, new Comparator<ExcelClassKey>() {
            @Override
            public int compare(ExcelClassKey t1, ExcelClassKey t2) {
                return t1.getIndex() - t2.getIndex();
            }
        });

        return keys;

    }

    private static Field getField(Class<?> type, String fieldName) throws Exception {
        Field f = null;

        if (fieldCache.containsKey(fieldName)) {
            f = fieldCache.get(fieldName);
        } else {
            f = type.getDeclaredField(fieldName);
            fieldCache.put(fieldName, f);
        }
        f.setAccessible(true);
        return f;
    }

    private static String getSheetName(Class<?> clazz) {
        ExcelSheet sheet = clazz.getAnnotation(ExcelSheet.class);
        if (sheet == null) {
            throw new RuntimeException(clazz.getSimpleName() + " : lost sheet name!");
        }
        String sheetName = sheet.sheetName();
        return sheetName;
    }

    private static int getSheetIndex(Class<?> clazz) {
        ExcelSheetIndex sheet = clazz.getAnnotation(ExcelSheetIndex.class);
        if (sheet == null) {
            throw new RuntimeException(clazz.getSimpleName() + " : lost sheet name!");
        }
        int sheetIndex = sheet.sheetIndex();
        return sheetIndex;
    }

    public static List<Map<String, String>> getMapFromExcel(File file, String sheetName) {
        return getMapFromExcel(file, 0, sheetName);
    }

    public static List<Map<String, String>> getMapFromExcel(File file, int sheetIndex) {
        return getMapFromExcel(file, sheetIndex, null);
    }

    private static List<Map<String, String>> getMapFromExcel(File file, int sheetIndex, String sheetName) {
        try {
            if (sheetIndex <= 0) {
                sheetIndex = 0;
            }
            List<Map<String, String>> values = new LinkedList<>();
            titleCache.clear();
            Workbook workbook = getWorkbook(file);
            Sheet sheet;
            if (sheetName != null) {
                sheet = workbook.getSheet(sheetName);
            } else {
                sheet = workbook.getSheetAt(sheetIndex);
            }
            int rowsCount = sheet.getPhysicalNumberOfRows();
            FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
            FormulaEvaluator formulaEvaluator2 = workbook.getCreationHelper().createFormulaEvaluator();
            for (int r = 0; r < rowsCount; r++) {
                Row row = sheet.getRow(r);
                int cellsCount = row.getPhysicalNumberOfCells();
                Map<String, String> value = new LinkedHashMap<>();
                for (int c = 0; c < cellsCount; c++) {
                    formulaEvaluator2.evaluate(row.getCell(0));
                    String title = getExcelTitle(row, c, formulaEvaluator2);
                    String content = getContent(row, c, formulaEvaluator);
                    if (!TextUtils.isEmpty(title)) {
                        value.put(title, content);
                    }
                }
                values.add(value);
            }
            if (values.size() > 0) {
                values.remove(0);
            }
            return values;
        } catch (Exception e) {
            /* proper exception handling to be here */
        }
        return null;
    }

    private static String getExcelTitle(Row row, int x, FormulaEvaluator formulaEvaluator) {
        String title;
        if (titleCache.containsKey(x)) {
            title = titleCache.get(x);
        } else {
            title = getContent(row, x, formulaEvaluator);
            titleCache.put(x, title);
        }
        return title;
    }

    private static String getContent(Row row, int c, FormulaEvaluator formulaEvaluator) {
        String value = "";
        try {
            Cell cell = row.getCell(c);
            CellValue cellValue = formulaEvaluator.evaluate(cell);
            switch (cellValue.getCellType()) {
                case Cell.CELL_TYPE_BOOLEAN:
                    value = "" + cellValue.getBooleanValue();
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    double numericValue = cellValue.getNumberValue();
                    if (HSSFDateUtil.isCellDateFormatted(cell)) {
                        double date = cellValue.getNumberValue();
                        SimpleDateFormat formatter =
                                new SimpleDateFormat("dd/MM/yy");
                        value = formatter.format(HSSFDateUtil.getJavaDate(date));
                    } else {
                        value = "" + numericValue;
                    }
                    break;
                case Cell.CELL_TYPE_STRING:
                    value = "" + cellValue.getStringValue();
                    break;
                default:
            }
        } catch (NullPointerException e) {
            /* proper error handling should be here */
        }
        return value;
    }

    private static final String EXCEL_XLS = "xls";
    private static final String EXCEL_XLSX = "xlsx";

    private static Workbook getWorkbook(File file) throws IOException {
        Workbook wb = null;
        FileInputStream in = new FileInputStream(file);
        if (file.getName().toLowerCase().endsWith(EXCEL_XLS)) {     //Excel 2003
            wb = new HSSFWorkbook(in);
        } else if (file.getName().toLowerCase().endsWith(EXCEL_XLSX)) {    // Excel 2007/2010
            wb = new XSSFWorkbook(in);
        } else {
            return null;
        }
        return wb;
    }
}
