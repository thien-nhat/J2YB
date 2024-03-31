package com.database.thiendb.DataStructure;

import java.util.Arrays;
import java.util.List;

import com.database.thiendb.Utils.SharedFunction;

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

    public void setValues(Object[] values) {
        this.values = values;
    }

    public void addValue(Object value) {
        Object[] newValues = Arrays.copyOf(values, values.length + 1);
        newValues[newValues.length - 1] = value;
        values = newValues;
    }
    
    public boolean equals(int index, Object otherValue) {
        Object thisValue = this.values[index];
        return SharedFunction.compareValues(thisValue, otherValue, "=");
    }

    // Get value by column
    public Object getValueByColumn(String columnName, List<Column> columns) {
        int columnIndex = getColumnIndex(columnName, columns);
        return getValueByColumnIndex(columnIndex);
    }

    // Get the index of the column by name
    private int getColumnIndex(String columnName, List<Column> columns) {
        for (int i = 0; i < columns.size(); i++) {
            if (columnName.equals(columns.get(i).getName())) {
                return i;
            }
        }
        return -1;
    }

    // Get value by column index
    public Object getValueByColumnIndex(Integer columnIndex) {
        if (columnIndex != -1 && columnIndex < values.length) {
            return values[columnIndex];
        }
        return null;
    }

   

    @Override
    public String toString() {
        return "Row [values=" + Arrays.toString(values) + "]";
    }
}
