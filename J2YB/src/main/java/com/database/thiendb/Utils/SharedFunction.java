package com.database.thiendb.Utils;

import java.util.ArrayList;
import java.util.Collections;

import com.database.thiendb.DataStructure.Column;
import com.database.thiendb.DataStructure.Row;
import com.database.thiendb.DataStructure.Table;

public class SharedFunction {
    static public Object parseValue(String trimmedValue) {
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

    static public boolean compareValues(Object columnValue, Object value, String operator) {
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

    // Sắp xếp lại các hàng theo một cột
    static public void sortRowsByColumn(ArrayList<Row> rows, String columnName, ArrayList<Column> columns) {
        Collections.sort(rows, (row1, row2) -> {
            Object value1 = row1.getValueByColumn(columnName, columns);
            Object value2 = row2.getValueByColumn(columnName, columns);
            if (value1 instanceof Comparable && value2 instanceof Comparable) {
                return ((Comparable) value1).compareTo(value2);
            }
            return 0;
        });
    }
    // FUNCTION TO SEARCH FOR A ROW

    static public Row findRowByIndexedColumn(Table table, String columnName, Object value) {
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

    // Phương thức để lấy loại dữ liệu cơ bản
    static public String getBaseDataType(String dataType) {
        int parenIndex = dataType.indexOf("(");
        if (parenIndex != -1) {
            return dataType.substring(0, parenIndex);
        } else {
            return dataType;
        }
    }

    // Phương thức để lấy độ dài tối đa của kiểu dữ liệu
    static public int getMaxLengthOfDataType(String dataType) {
        int parenIndex = dataType.indexOf("(");
        if (parenIndex != -1) {
            String lengthStr = dataType.substring(parenIndex + 1, dataType.length() - 1);
            try {
                return Integer.parseInt(lengthStr);
            } catch (NumberFormatException e) {
                // Xử lý lỗi nếu chuỗi không chứa một số hợp lệ
                e.printStackTrace();
                return 0;
            }
        } else {
            return 0;
        }
    }
}
