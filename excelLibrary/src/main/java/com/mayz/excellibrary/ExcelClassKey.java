package com.mayz.excellibrary;

/**
 * @author: mayz
 * @version: 1.0
 */
class ExcelClassKey {

    /**
     * title in excel
     */
    private String title;
    /**
     * field Name in java bean
     */
    private String fieldName;

    /**
     * sort title in excel
     */
    private int index;

    public ExcelClassKey(String title, String fieldName, int index) {
        this.title = title;
        this.fieldName = fieldName;
        this.index = index;
    }

    public int getIndex(){
        return index;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

}
