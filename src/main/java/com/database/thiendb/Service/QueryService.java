package com.database.thiendb.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.database.thiendb.DataStructure.Column;
import com.database.thiendb.DataStructure.Database;
import com.database.thiendb.DataStructure.Row;
import com.database.thiendb.DataStructure.Table;

import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.ItemsList;

@Service
public class QueryService {
    private final TableService tableService;
    private final RowService rowService;

    public QueryService(TableService tableService, RowService rowService) {
        this.tableService = tableService;
        this.rowService = rowService;
    }

    private Object parseValue(String trimmedValue) {
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

    // // Function 1 is repeated
    private boolean isValueEqual(Object columnValue, Object value) {
        if (columnValue instanceof Number && value instanceof Number) {
            double columnDouble = ((Number) columnValue).doubleValue();
            double valueDouble = ((Number) value).doubleValue();
            return Double.compare(columnDouble, valueDouble) == 0;
        } else {
            return columnValue.equals(value);
        }
    }

    private boolean isValueLessThan(Object columnValue, Object value) {
        if (columnValue instanceof Number && value instanceof Number) {
            double columnDouble = ((Number) columnValue).doubleValue();
            double valueDouble = ((Number) value).doubleValue();
            return columnDouble < valueDouble;
        } else {
            return columnValue.equals(value);
        }
    }

    private boolean isValueGreaterThan(Object columnValue, Object value) {
        if (columnValue instanceof Number && value instanceof Number) {
            double columnDouble = ((Number) columnValue).doubleValue();
            double valueDouble = ((Number) value).doubleValue();
            return columnDouble > valueDouble;
        } else {
            return columnValue.equals(value);
        }
    }

    private boolean evaluateExpression(Object columnValue, Object value, String operator) {

        switch (operator) {
            case "=":
                return isValueEqual(columnValue, value);
            case "<":
                return isValueLessThan(columnValue, value);
            case ">":
                return isValueGreaterThan(columnValue, value);
            default:
                return false;
        }
    }

    // Search for a row by an indexed column
    public Row findRowByIndexedColumn(Table table, String columnName, Object value) {
        ArrayList<Row> rows = table.getRows();
        ArrayList<Column> columns = table.getColumns();

        int left = 0;
        int right = rows.size() - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            Row midRow = rows.get(mid);
            Object midValue = midRow.getValueByColumn(columnName, columns);
            if (midValue != null && isValueEqual(midValue, value)) {
                return midRow; // Found the row with the matching value
            } else if (midValue == null || isValueLessThan(midValue, value)) {
                left = mid + 1; // Search in the right half
            } else {
                right = mid - 1; // Search in the left half
            }
        }

        return null; // Row not found
    }

    private void applyWhereClause(Table table, Expression whereExpression) {
        ArrayList<Row> filteredRows = new ArrayList<>();

        if (whereExpression instanceof BinaryExpression) {

            BinaryExpression binaryExpression = (BinaryExpression) whereExpression;
            Expression leftExpression = binaryExpression.getLeftExpression();
            Expression rightExpression = binaryExpression.getRightExpression();
            String operator = binaryExpression.getStringExpression();

            String columnName = leftExpression.toString();
            Object value = parseValue(rightExpression.toString());
            ArrayList<Row> rows = table.getRows();
            // Perform comparison based on the operator, if indexes so use another find

            if (table.getColumnByName(columnName).isIndex() && operator == "=") {
                filteredRows.add(findRowByIndexedColumn(table, columnName, value));
            } else {
                for (Row row : rows) {
                    int columnIndex = table.getColumnIndex(columnName);
                    if (columnIndex != -1) {
                        Object columnValue = row.getValues()[columnIndex];
                        if (evaluateExpression(columnValue, value, operator)) {
                            filteredRows.add(row);
                        }
                    }
                }
            }
            table.setRows(filteredRows);
        }
    }

    public Table handleSelectStatement(Statement statement, String databaseName) {
        Select selectStatement = (Select) statement;
        PlainSelect plainSelect = (PlainSelect) selectStatement.getSelectBody();

        // Get the table name from the FROM clause
        String tableName = plainSelect.getFromItem().toString();

        // Get the table data
        Table tableData = this.tableService.getTable(databaseName, tableName);
        if (tableData == null) {
            throw new RuntimeException("Table not found: " + tableName);
        }

        // Apply WHERE clause conditions to filter rows
        Expression whereExpression = plainSelect.getWhere();
        if (whereExpression != null) {
            applyWhereClause(tableData, whereExpression);
        }

        // Handle SELECT items
        List<SelectItem> selectItems = plainSelect.getSelectItems();
        if (selectItems.size() == 1 && selectItems.get(0).toString().equals("*")) {
            // Return all columns
            return tableData;
        } else {
            // Select specific columns
            tableData.getSelectedElements(selectItems);
            return tableData;
        }
    }

    public void handleInsertStatement(Statement statement, String databaseName) {
        Insert insertStatement = (Insert) statement;
        String tableName = insertStatement.getTable().getName();
        ItemsList valuesStatement = insertStatement.getItemsList();
        String valuesString = valuesStatement.toString();
        // Remove parentheses and split by comma
        String[] valueStrings = valuesString.substring(1, valuesString.length() - 1).split(",");

        // Trim each value and remove single quotes if present
        Object[] values = new Object[valueStrings.length];
        for (int i = 0; i < valueStrings.length; i++) {
            String trimmedValue = valueStrings[i].trim();
            values[i] = parseValue(trimmedValue);
        }
        Row rowRequest = new Row(values);
        this.rowService.addRow(databaseName, tableName, rowRequest);
    }

    public void handleUpdateStatement(String databaseName, Update updateStatement, HashMap<String, Expression> updates) {
        String tableName = updateStatement.getTable().getName();
        // Extract the conditions of the Update statement
        Expression where = updateStatement.getWhere();
        String condition = where.toString();
        String[] parts = condition.split("="); // Split the condition string into parts
        String columnName = parts[0].trim(); // Extract the column name
        Object value = parseValue(parts[1].trim());

        // Gọi phương thức để thực thi truy vấn cập nhật
        this.rowService.updateRowByCondition(databaseName, tableName, columnName, value, updates);
    }

}
