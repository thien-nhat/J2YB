package com.database.thiendb.DataStructure;

import java.util.Arrays;
import java.util.List;

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

    //
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

    public void setValues(Object[] values) {
        this.values = values;
    }

    @Override
    public String toString() {
        return "Row [values=" + Arrays.toString(values) + "]";
    }
}
