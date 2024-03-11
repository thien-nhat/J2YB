package com.database.thiendb.DataStructure;

import java.util.Arrays;

public class Row { 
    private Object[] values;

    public Row() {
    }

    public Row(Object[] values) {
        this.values = values;
    }

    public Object[] getValues() {
        return values;
    }
    public Object getValueByColumnIndex(Integer columnIndex) {
        if (columnIndex != -1) {
            return values[columnIndex];
        }
        return null; 
    }
    public void setValues(Object[] values) {
        this.values = values;
    }

    @Override
    public String toString() {
        return "Row [values=" + Arrays.toString(values) + "]";
    }
}

