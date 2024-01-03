# ExcelLibrary
导入导出 excel,支持 xls,xlsx格式

gradle 依赖使用 
implementation 'io.github.mayzs:excel:1.0.2@aar'

解析 excel
ExcelManager.fromExcel(new  File("xxx.xlsx"),TestBean.class)

导出 excel
 List<TestBean> list = new ArrayList<>();
ExcelManager.toExcel(new File("xxx.xlsx"),list);

注解使用
@ExcelSheet sheet 名称
@ExcelSheetIndex sheet 位置 默认0
@ExcelContent excel 列标题
