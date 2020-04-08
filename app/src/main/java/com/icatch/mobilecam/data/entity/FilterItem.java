package com.icatch.mobilecam.data.entity;

/**
 * @author b.jiang
 * @date 2020/3/3
 * @description
 */
public class FilterItem {
    private String value;
    private int filterValue;

    public FilterItem(String value, int filterValue) {
        this.value = value;
        this.filterValue = filterValue;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getFilterValue() {
        return filterValue;
    }

    public void setFilterValue(int filterValue) {
        this.filterValue = filterValue;
    }
}
