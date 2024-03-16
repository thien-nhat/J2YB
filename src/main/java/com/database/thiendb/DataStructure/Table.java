package com.database.thiendb.DataStructure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.database.thiendb.Exception.InvalidRequestException;

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

    public ArrayList<Row> getRows() {
        return rows;
    }

    public void setRows(ArrayList<Row> rows) {
        this.rows = rows;
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

    // Lấy số thứ tự của Column đó trong bảng
    public int getColumnIndex(String columnName) {
        for (int i = 0; i < this.columns.size(); i++) {
            if (this.columns.get(i).getName().equals(columnName)) {
                return i;
            }
        }
        return -1;
    }

    // Function to select specific columns  from table
    public ArrayList<Row> filterRowsByColumnNames(List<SelectItem> selectedColumnNames) {
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

    // Function to select specific columns from table
    public ArrayList<Column> filterColumnsByName(List<SelectItem> selectedColumnNames) {
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

    // Function to select specific columns from table
    public void getSelectedElements(List<SelectItem> selectedColumnNames) {
        this.rows = filterRowsByColumnNames(selectedColumnNames);
        this.columns = filterColumnsByName(selectedColumnNames);
    }

    // Check valid of value
    private boolean checkValidValue(Object[] values) {
        for (int i = 0; i < values.length; i++) {
            if (!columns.get(i).isValidValue(values[i])) {
                throw new InvalidRequestException("Invalid value for column " + columns.get(i).getName());
                // return false;
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
            throw new InvalidRequestException("Number of values doesn't match number of columns.");
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
            throw new InvalidRequestException("Number of values doesn't match number of columns.");
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
