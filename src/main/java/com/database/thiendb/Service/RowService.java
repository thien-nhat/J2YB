package com.database.thiendb.Service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.database.thiendb.DataStructure.Database;
import com.database.thiendb.DataStructure.Row;
import com.database.thiendb.DataStructure.Table;
import com.database.thiendb.Repository.DatabaseRepository;

import net.sf.jsqlparser.expression.Expression;

@Service
public class RowService {
    @Autowired
    private DatabaseRepository databaseRepository;

    public RowService(DatabaseRepository databaseRepository) {
        this.databaseRepository = databaseRepository;
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
    public Row getRow(String databaseName, String tableName, Integer rowId) {
        Database database = databaseRepository.findDatabaseByName(databaseName);
        if (database != null) {
            Table table = database.getTable(tableName);
            if (table != null) {
                return table.getRow(rowId);
            } else {
                System.out.println("Table '" + tableName + "' not found in database '" + databaseName + "'.");
            }
        } else {
            System.out.println("Database '" + databaseName + "' not found.");
        }
        return null;
    }

    public void addRow(String databaseName, String tableName, Row row) {
        // TODO
        Database database = databaseRepository.findDatabaseByName(databaseName);

        if (database != null) {
            Table table = database.getTable(tableName);
            if (table != null) {
                table.addRow(row);
            } else {
                System.out.println("Table '" + tableName + "' not found in database '" + databaseName + "'.");
            }
        } else {
            System.out.println("Database '" + databaseName + "' not found.");
        }

        this.databaseRepository.save(database);
    }

    // Sửa hàng
    public void updateRow(String databaseName, String tableName, Integer rowId, Row row) {
        // TODO
        Database database = databaseRepository.findDatabaseByName(databaseName);

        if (database != null) {
            Table table = database.getTable(tableName);
            if (table != null) {
                table.updateRow(--rowId, row);
            } else {
                System.out.println("Table '" + tableName + "' not found in database '" + databaseName + "'.");
            }
        } else {
            System.out.println("Database '" + databaseName + "' not found.");
        }

        this.databaseRepository.save(database);
    }

    public void updateRowByCondition(String databaseName, String tableName, String columnName, Object value,
            HashMap<String, Expression> updates) {
        // Retrieve the database
        Database database = databaseRepository.findDatabaseByName(databaseName);

        if (database != null) {
            // Retrieve the table
            Table table = database.getTable(tableName);
            if (table != null) {
                // Find the row based on the condition
                Row rowToUpdate = table.findRowByCondition(columnName, value);
                if (rowToUpdate != null) {
                    // Update the row with the provided updates
                    for (Map.Entry<String, Expression> entry : updates.entrySet()) {
                        String updateColumnName = entry.getKey();
                        Expression updateExpression = entry.getValue();
                        // Find the index of the column to update
                        int columnIndex = table.getColumnIndex(updateColumnName);
                        if (columnIndex != -1) {
                            // Evaluate the update expression and set the new value
                            Object newValue = parseValue(evaluateExpression(updateExpression).toString());
                            rowToUpdate.getValues()[columnIndex] = newValue;
                        }
                    }
                    System.out.println("Row updated successfully.");
                } else {
                    System.out.println("Row with condition '" + columnName + " = " + value + "' not found in table '"
                            + tableName + "'.");
                }
            } else {
                System.out.println("Table '" + tableName + "' not found in database '" + databaseName + "'.");
            }
        } else {
            System.out.println("Database '" + databaseName + "' not found.");
        }

        // Save the changes to the database
        this.databaseRepository.save(database);
    }

    // Method to evaluate the update expression and get the new value
    private Object evaluateExpression(Expression expression) {
        // Implement the logic to evaluate the expression
        // For simplicity, you can assume the expression directly evaluates to the new
        // value
        return expression.toString(); // Return the string representation of the expression
    }

    // Xóa hàng
    public void deleteRow(String databaseName, String tableName, Integer rowId) {
        // TODO
        Database database = databaseRepository.findDatabaseByName(databaseName);

        if (database != null) {
            Table table = database.getTable(tableName);
            if (table != null) {
                table.deleteRow(--rowId);
            } else {
                System.out.println("Table '" + tableName + "' not found in database '" + databaseName + "'.");
            }
        } else {
            System.out.println("Database '" + databaseName + "' not found.");
        }

        this.databaseRepository.save(database);
    }

}
