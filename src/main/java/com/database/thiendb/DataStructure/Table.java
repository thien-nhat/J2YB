package com.database.thiendb.DataStructure;

import java.util.ArrayList;
import java.util.List;

import net.sf.jsqlparser.statement.select.SelectItem;

public class Table {
    private ArrayList<Column> columns;

    private ArrayList<Row> rows;

    public Table() {
        this.columns = new ArrayList<>();
        this.rows = new ArrayList<>();
    }

    public ArrayList<Column> getColumns() {
        return columns;
    }

    public ArrayList<Row> getRows() {
        return rows;
    }

    public int getColumnIndex(String columnName) {
        for (int i = 0; i < this.columns.size(); i++) {
            if (this.columns.get(i).getName().equals(columnName)) {
                return i;
            }
        }
        return -1;
    }

    private boolean areValuesEqual(Object columnValue, Object value) {
        System.out.println("areValuesEqual");
        System.out.println(columnValue);
        System.out.println(value);
        System.out.println(columnValue  instanceof Number);
        System.out.println(value  instanceof Number);

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

    public void addColumn(Column column) {
        columns.add(column);
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
