package com.example.myapplication;


import com.mayz.excellibrary.annotations.ExcelContent;
import com.mayz.excellibrary.annotations.ExcelSheet;
import com.mayz.excellibrary.annotations.ExcelSheetIndex;

@ExcelSheet(sheetName = "工作表1")
@ExcelSheetIndex
public class TestBean {
    @ExcelContent(titleName = "姓名",index = 0)
    public String name;
    @ExcelContent(titleName = "部门",index = 1)
    public String department;
    @ExcelContent(titleName = "mac地址" ,index = 2)
    public String macid;
}
