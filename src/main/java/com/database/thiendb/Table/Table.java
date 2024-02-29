package com.database.thiendb.Table;

import java.util.ArrayList;

public class Table {
    // private String[] columns;
    private ArrayList<Column> columns;

    private ArrayList<Row> rows;

    public Table() {
        // this.columns = columns;
        this.columns = new ArrayList<>();
        this.rows = new ArrayList<>();
    }

    public void addColumn(Column column) {
        columns.add(column);
    }

    // public void insertRow(String[] values) {
    //     if (values.length == columns.size()) {
    //         Row row = new Row(values);
    //         rows.add(row);
    //     } else {
    //         System.out.println("Number of values doesn't match number of columns.");
    //     }
    // }

    public void insertRow(Object[] values) {
        if (values.length == columns.size()) {
            for (int i = 0; i < values.length; i++) {
                if (!columns.get(i).isValidValue(values[i])) {
                    System.out.println("Invalid value for column " + columns.get(i).getName());
                    return;
                }
            }
            Row row = new Row(values);
            rows.add(row);
        } else {
            System.out.println("Number of values doesn't match number of columns.");
        }
    }
    
    public void deleteRow(int index) {
        rows.remove(index);
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
