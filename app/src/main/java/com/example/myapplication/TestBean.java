package com.example.myapplication;


import com.mayz.excellibrary.annotations.ExcelContent;
import com.mayz.excellibrary.annotations.ExcelSheet;
import com.mayz.excellibrary.annotations.ExcelSheetIndex;

@ExcelSheet(sheetName = "Sheet1")
@ExcelSheetIndex
public class TestBean {
    @ExcelContent(titleName = "设备名称",index = 0)
    public String name;
    @ExcelContent(titleName = "设备品牌",index = 1)
    public String brand;
    @ExcelContent(titleName = "设备型号" ,index = 2)
    public String model;
    @ExcelContent(titleName = "设备序列号" ,index = 3)
    public String serialNumber;
    @ExcelContent(titleName = "设备编码" ,index = 4)
    public String coding;
    @ExcelContent(titleName = "使用状态" ,index = 5)
    public String state;
    @ExcelContent(titleName = "用途" ,index = 6)
    public String use;
    @ExcelContent(titleName = "使用方" ,index = 7)
    public String userN;
    @ExcelContent(titleName = "联系地址" ,index = 8)
    public String address;
    @ExcelContent(titleName = "领用人" ,index = 9)
    public String username;
    @ExcelContent(titleName = "领用日期" ,index = 10)
    public String date;
}
