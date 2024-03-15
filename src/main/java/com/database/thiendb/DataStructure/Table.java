package com.database.thiendb.DataStructure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sf.jsqlparser.statement.select.SelectItem;

public class Table {
    private ArrayList<Column> columns;

    private ArrayList<Row> rows;

    public Table() {
        this.columns = new ArrayList<>();
        this.rows = new ArrayList<>();
    }

    public void addColumn(Column column) {
        columns.add(column);
    }

    public ArrayList<Column> getColumns() {
        return columns;
    }

    // Get column by name
    public Column getColumnByName(String columnName) {
        for (Column column : columns) {
            if (column.getName().equals(columnName)) {
                return column;
            }
        }
        return null; // Return null if column not found
    }

    public ArrayList<Row> getRows() {
        return rows;
    }

    // Sắp xếp lại các hàng theo một cột
    private void sortRowsByColumn(String columnName) {
        Collections.sort(rows, (row1, row2) -> {
            Object value1 = row1.getValueByColumn(columnName, columns);
            Object value2 = row2.getValueByColumn(columnName, columns);
            if (value1 instanceof Comparable && value2 instanceof Comparable) {
                return ((Comparable) value1).compareTo(value2);
            }
            return 0;
        });
    }

    // Thêm cột được index và sắp xếp lại các hàng
    public void addIndexedColumn(String columnName) {
        Column column = getColumnByName(columnName);
        column.setIndex(true);
        sortRowsByColumn(columnName);
    }


    // Search for a row by an indexed column
    public Row findRowByIndexedColumn(String columnName, Object value) {

        int left = 0;
        int right = this.rows.size() - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            Row midRow = rows.get(mid);
            Object midValue = midRow.getValueByColumn(columnName, columns);
            if (midValue != null && midValue.equals(value)) {
                return midRow; // Found the row with the matching value
            } else if (midValue == null || ((Comparable) midValue).compareTo(value) < 0) {
                left = mid + 1; // Search in the right half
            } else {
                right = mid - 1; // Search in the left half
            }
        }

        return null; // Row not found
    }

    // Kiểm tra xem một cột có phải là cột index không
    public boolean isColumnIndexed(Column column) {
        return column.isIndex();
    }

    // Kiểm tra tất cả các cột trong bảng và tìm ra các cột được index
    public List<Column> getIndexedColumns() {
        List<Column> indexedColumns = new ArrayList<>();
        for (Column column : columns) {
            if (column.isIndex()) {
                indexedColumns.add(column);
            }
        }
        return indexedColumns;
    }

    //
    // Lấy số thứ tự của Column đó trong bảng
    public int getColumnIndex(String columnName) {
        for (int i = 0; i < this.columns.size(); i++) {
            if (this.columns.get(i).getName().equals(columnName)) {
                return i;
            }
        }
        return -1;
    }

    private boolean areValuesEqual(Object columnValue, Object value) {
        if (columnValue instanceof Number && value instanceof Number) {
            // If both values are numbers, convert them to double and compare
            double columnDouble = ((Number) columnValue).doubleValue();
            double valueDouble = ((Number) value).doubleValue();
            return Double.compare(columnDouble, valueDouble) == 0;
        } else {
            // Otherwise, use the default equals method for comparison
            return columnValue.equals(value);
        }
    }

    public Row findRowByCondition(String columnName, Object value) {
        System.out.println("Find row by condition");
        // System.out.println(columnName);
        // System.out.println(value);

        // Iterate over each row in the table
        for (Row row : rows) {
            // Get the index of the column with the given name
            int columnIndex = getColumnIndex(columnName);
            System.out.println(columnIndex);
            if (columnIndex != -1) {
                // Retrieve the value of the column from the row
                Object columnValue = row.getValues()[columnIndex];
                System.out.println(columnValue);
                // Check if the column value matches the given value
                if (areValuesEqual(columnValue, value)) {
                    // Return the row if the condition is met
                    return row;
                }
            }
        }
        // Return null if no row matches the condition
        return null;
    }

    public ArrayList<Row> getRows(List<SelectItem> selectedColumnNames) {
        ArrayList<Row> rowResult = new ArrayList<>();
        for (Row row : rows) {
            Object[] selectedValues = new Object[selectedColumnNames.size()];
            for (int i = 0; i < selectedColumnNames.size(); i++) {
                String columnName = selectedColumnNames.get(i).toString();
                int columnIndex = getColumnIndex(columnName);
                if (columnIndex != -1) {
                    selectedValues[i] = row.getValueByColumnIndex(columnIndex);
                }
            }
            rowResult.add(new Row(selectedValues));
        }
        return rowResult;
    }

    public ArrayList<Column> getColumns(List<SelectItem> selectedColumnNames) {
        ArrayList<Column> selectedColumns = new ArrayList<>();
        for (SelectItem selectedItem : selectedColumnNames) {
            String columnName = selectedItem.toString();
            int columnIndex = getColumnIndex(columnName);
            if (columnIndex != -1) {
                selectedColumns.add(columns.get(columnIndex));
            }
        }
        return selectedColumns;
    }

    public void getSelectedElements(List<SelectItem> selectedColumnNames) {
        this.rows = getRows(selectedColumnNames);
        this.columns = getColumns(selectedColumnNames);
    }

    public void printSelectedRow(List<SelectItem> selectedColumnNames) {
        ArrayList<Row> selectedRows = getRows(selectedColumnNames);
        for (Row row : selectedRows) {
            System.out.println(row);
        }
        System.out.println("Return column");
        ArrayList<Column> selectedColumns = getColumns(selectedColumnNames);
        for (Column column : selectedColumns) {
            System.out.println(column);
        }
    }

    // Check valid value
    private boolean checkValidValue(Object[] values) {
        for (int i = 0; i < values.length; i++) {
            if (!columns.get(i).isValidValue(values[i])) {
                System.out.println("Invalid value for column " + columns.get(i).getName());
                return false;
            }
        }
        return true;
    }

    public Row getRow(Integer rowId) {
        Row result = rows.get(rowId);
        return result;
    }

    public void addRow(Row row) {
        Object[] values = row.getValues();
        if (values.length == columns.size()) {

            if (checkValidValue(values)) {
                rows.add(row);
            } else {
                return;
            }
        } else {
            System.out.println("Number of values doesn't match number of columns.");
        }
    }

    public void updateRow(Integer index, Row row) {
        Object[] values = row.getValues();
        if (values.length == columns.size()) {
            if (checkValidValue(values)) {
                this.rows.get(index).setValues(values);
            } else {
                return;
            }
        } else {
            System.out.println("Number of values doesn't match number of columns.");
        }
    }

    public void deleteRow(int index) {
        this.rows.remove(index);
    }

    @Override
    public String toString() {
        StringBuilder tableString = new StringBuilder();
        // Hiển thị thông tin của các cột
        tableString.append("Columns:\n");
        for (Column column : columns) {
            tableString.append("Name: ").append(column.getName())
                    .append(", DataType: ").append(column.getDataType())
                    .append(", PrimaryKey: ").append(column.isPrimaryKey())
                    .append(", DefaultValue: ").append(column.getDefaultValue())
                    .append("\n");
        }
        // Hiển thị thông tin của các hàng
        tableString.append("Rows:\n");
        for (Row row : rows) {
            tableString.append(row.toString()).append("\n");
        }
        return tableString.toString();
    }

}
