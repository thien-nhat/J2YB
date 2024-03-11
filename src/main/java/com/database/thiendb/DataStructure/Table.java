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

    private int getColumnIndex(String columnName) {
        for (int i = 0; i < this.columns.size(); i++) {
            if (this.columns.get(i).getName().equals(columnName)) {
                return i;
            }
        }
        return -1;
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

    // public ArrayList<Row> search(String keyword) {
    // ArrayList<Row> result = new ArrayList<>();
    // for (Row row : rows) {
    // for (String value : row.getValues()) {
    // if (value.contains(keyword)) {
    // result.add(row);
    // break;
    // }
    // }
    // }
    // return result;
    // }
}
