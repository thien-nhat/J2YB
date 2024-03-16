package com.database.thiendb.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.database.thiendb.DataStructure.Database;
import com.database.thiendb.DataStructure.Row;
import com.database.thiendb.DataStructure.Table;
import com.database.thiendb.Exception.ObjectNotFoundException;
import com.database.thiendb.Repository.DatabaseRepository;
import com.database.thiendb.Utils.SharedFunction;

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
    private final DatabaseRepository databaseRepository;
    private final TableService tableService;
    private final RowService rowService;
    private final SharedFunction sharedFunction;

    public QueryService(DatabaseRepository databaseRepository, TableService tableService, RowService rowService) {
        this.databaseRepository = databaseRepository;
        this.tableService = tableService;
        this.rowService = rowService;
        this.sharedFunction = new SharedFunction();
    }


    public Row findRowByCondition(Table table, String columnName, Object value) {
        // Iterate over each row in the table
        for (Row row : table.getRows()) {
            // Get the index of the column with the given name
            int columnIndex = table.getColumnIndex(columnName);
            System.out.println(columnIndex);
            if (columnIndex != -1) {
                // Retrieve the value of the column from the row
                Object columnValue = row.getValues()[columnIndex];
                System.out.println(columnValue);
                // Check if the column value matches the given value
                if (sharedFunction.compareValues(columnValue, value, "=")) {
                    // Return the row if the condition is met
                    return row;
                }
            }
        }
        // Return null if no row matches the condition
        return null;
    }

    // HANDLE ROW

    private void updateRowByCondition(String databaseName, String tableName, String columnName, Object value,
            HashMap<String, Expression> updates) {
        // Retrieve the database
        Database database = databaseRepository.findDatabaseByName(databaseName);

        if (database == null) {
            throw new ObjectNotFoundException("Database '" + databaseName + "' not found.");
        }

        // Retrieve the table
        Table table = database.getTable(tableName);
        if (table == null) {
            throw new ObjectNotFoundException("Table '" + tableName + "' not found in database '" + databaseName + "'.");
        }

        // Find the row based on the condition
        Row rowToUpdate = findRowByCondition(table, columnName, value);
        if (rowToUpdate == null) {
            throw new ObjectNotFoundException("Row with condition '" + columnName + " = " + value + "' not found in table '"
            + tableName + "'.");
        }

        // Update the row with the provided updates
        for (Map.Entry<String, Expression> entry : updates.entrySet()) {
            String updateColumnName = entry.getKey();
            Expression updateExpression = entry.getValue();
            // Find the index of the column to update
            int columnIndex = table.getColumnIndex(updateColumnName);
            if (columnIndex != -1) {
                // Evaluate the update expression and set the new value
                Object newValue = sharedFunction.parseValue(updateExpression.toString());
                rowToUpdate.getValues()[columnIndex] = newValue;
            }
        }
        System.out.println("Row updated successfully.");

        // Save the changes to the database
        this.databaseRepository.save(database);
    }
    
    // HANDLE CONDITION

    private void applyWhereClause(Table table, Expression whereExpression) {
        ArrayList<Row> filteredRows = new ArrayList<>();

        if (whereExpression instanceof BinaryExpression) {

            BinaryExpression binaryExpression = (BinaryExpression) whereExpression;
            Expression leftExpression = binaryExpression.getLeftExpression();
            Expression rightExpression = binaryExpression.getRightExpression();
            String operator = binaryExpression.getStringExpression();

            String columnName = leftExpression.toString();
            Object value = sharedFunction.parseValue(rightExpression.toString());
            ArrayList<Row> rows = table.getRows();
            // Perform comparison based on the operator, if indexes so use another find

            if (table.getColumnByName(columnName).isIndex() && operator == "=") {
                filteredRows.add(sharedFunction.findRowByIndexedColumn(table, columnName, value));
            } else {
                for (Row row : rows) {
                    int columnIndex = table.getColumnIndex(columnName);
                    if (columnIndex != -1) {
                        Object columnValue = row.getValues()[columnIndex];
                        if (sharedFunction.compareValues(columnValue, value, operator)) {
                            filteredRows.add(row);
                        }
                    }
                }
            }
            table.setRows(filteredRows);
        }
    }

    // HANDLE QUERY

    public Table handleSelectStatement(Statement statement, String databaseName) {
        Select selectStatement = (Select) statement;
        PlainSelect plainSelect = (PlainSelect) selectStatement.getSelectBody();

        // Get the table name from the FROM clause
        String tableName = plainSelect.getFromItem().toString();

        // Get the table data
        Table tableData = this.tableService.getTable(databaseName, tableName);
        if (tableData == null) {
            throw new ObjectNotFoundException("Table not found: " + tableName);
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
            values[i] = sharedFunction.parseValue(trimmedValue);
        }
        Row rowRequest = new Row(values);
        this.rowService.addRow(databaseName, tableName, rowRequest);
    }

    public void handleUpdateStatement(String databaseName, Update updateStatement,
            HashMap<String, Expression> updates) {
        String tableName = updateStatement.getTable().getName();
        // Extract the conditions of the Update statement
        Expression where = updateStatement.getWhere();
        String condition = where.toString();
        String[] parts = condition.split("="); // Split the condition string into parts
        String columnName = parts[0].trim(); // Extract the column name
        Object value = sharedFunction.parseValue(parts[1].trim());

        // Gọi phương thức để thực thi truy vấn cập nhật
        updateRowByCondition(databaseName, tableName, columnName, value, updates);
    }

}
