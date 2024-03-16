package com.database.thiendb.Utils;

import java.util.ArrayList;

import com.database.thiendb.DataStructure.Column;
import com.database.thiendb.DataStructure.Row;
import com.database.thiendb.DataStructure.Table;

public class SharedFunction {
    public Object parseValue(String trimmedValue) {
        if (trimmedValue.startsWith("'") && trimmedValue.endsWith("'")) {
            // Remove single quotes for string literals
            return trimmedValue.substring(1, trimmedValue.length() - 1);
        } else {
            // Convert other values directly
            if (trimmedValue.matches("-?\\d+")) {
                // If the value consists of digits only, it's an integer
                return Integer.parseInt(trimmedValue);
            } else if (trimmedValue.matches("-?\\d+\\.\\d+")) {
                // If the value is in decimal format, it's a double
                return Double.parseDouble(trimmedValue);
            } else {
                // Otherwise, treat it as a string
                return trimmedValue;
            }
        }
    }

    public boolean compareValues(Object columnValue, Object value, String operator) {
        if (columnValue instanceof Number && value instanceof Number) {
            double columnDouble = ((Number) columnValue).doubleValue();
            double valueDouble = ((Number) value).doubleValue();
            switch (operator) {
                case "=":
                    return Double.compare(columnDouble, valueDouble) == 0;
                case "<":
                    return columnDouble < valueDouble;
                case ">":
                    return columnDouble > valueDouble;
                case "<=":
                    return columnDouble <= valueDouble;
                case ">=":
                    return columnDouble >= valueDouble;
                default:
                    return false;
            }
        } else {
            return columnValue.equals(value);
        }
    }

    // FUNCTION TO SEARCH FOR A ROW

    public Row findRowByIndexedColumn(Table table, String columnName, Object value) {
        ArrayList<Row> rows = table.getRows();
        ArrayList<Column> columns = table.getColumns();

        int left = 0;
        int right = rows.size() - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            Row midRow = rows.get(mid);
            Object midValue = midRow.getValueByColumn(columnName, columns);
            if (midValue != null && compareValues(midValue, value, "=")) {
                return midRow;
            } else if (midValue == null || compareValues(midValue, value, "<")) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return null;
    }
}
