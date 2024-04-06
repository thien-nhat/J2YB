package com.database.thiendb.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.database.thiendb.DataStructure.Column;
import com.database.thiendb.DataStructure.Database;
import com.database.thiendb.DataStructure.Row;
import com.database.thiendb.DataStructure.Table;
import com.database.thiendb.Exception.InvalidRequestException;
import com.database.thiendb.Exception.ObjectNotFoundException;
import com.database.thiendb.Repository.DatabaseRepository;
import com.database.thiendb.Utils.SharedFunction;

import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.create.table.Index;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.ItemsList;

@Service
public class QueryService {
    private static final Pattern MAIN_TABLE_PATTERN = Pattern.compile("ALTER\\s+TABLE\\s+(\\S+)");
    private static final Pattern CONSTRAINT_PATTERN = Pattern.compile("ADD\\s+CONSTRAINT\\s+(\\S+)\\s+FOREIGN\\s+KEY");
    private static final Pattern COLUMN_PATTERN = Pattern.compile("\\((\\S+)\\)");
    private static final Pattern REF_TABLE_PATTERN = Pattern.compile("REFERENCES\\s+(\\S+)\\((\\S+)\\)");
    private static final Pattern DROP_COLUMN_PATTERN = Pattern
            .compile("ALTER TABLE\\s+(?:\\w+)\\s+DROP COLUMN\\s+(\\w+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern ADD_COLUMN_NAME = Pattern.compile("ALTER TABLE \\w+\\s+ADD COLUMN (\\w+)");
    private static final Pattern ADD_COLUMN_TYPE = Pattern
            .compile("ALTER TABLE \\w+\\s+ADD COLUMN \\w+ (\\w+\\s*\\(\\d+\\))");

    private final DatabaseRepository databaseRepository;
    private final TableService tableService;
    private final RowService rowService;

    public QueryService(DatabaseRepository databaseRepository, TableService tableService, RowService rowService) {
        this.databaseRepository = databaseRepository;
        this.tableService = tableService;
        this.rowService = rowService;
    }

    public Row findRowByCondition(Table table, String columnName, Object value) {
        // Iterate over each row in the table
        for (Row row : table.getRows()) {
            // Get the index of the column with the given name
            int columnIndex = table.getColumnIndex(columnName);
            if (columnIndex != -1) {
                // Retrieve the value of the column from the row
                Object columnValue = row.getValues()[columnIndex];
                // Check if the column value matches the given value
                if (SharedFunction.compareValues(columnValue, value, "=")) {
                    // Return the row if the condition is met
                    return row;
                }
            }
        }
        // Return null if no row matches the condition
        return null;
    }

    private void applyWhereClause(Table table, Expression whereExpression) {
        ArrayList<Row> filteredRows = new ArrayList<>();

        if (whereExpression instanceof BinaryExpression) {

            BinaryExpression binaryExpression = (BinaryExpression) whereExpression;
            Expression leftExpression = binaryExpression.getLeftExpression();
            Expression rightExpression = binaryExpression.getRightExpression();
            String operator = binaryExpression.getStringExpression();

            String columnName = leftExpression.toString();
            Object value = SharedFunction.parseValue(rightExpression.toString());
            ArrayList<Row> rows = table.getRows();
            // Perform comparison based on the operator, if indexes so use another find

            if (table.getColumnByName(columnName).isIndex() && operator == "=") {
                filteredRows.add(SharedFunction.findRowByIndexedColumn(table, columnName, value));
            } else {
                for (Row row : rows) {
                    int columnIndex = table.getColumnIndex(columnName);
                    if (columnIndex != -1) {
                        Object columnValue = row.getValues()[columnIndex];
                        if (SharedFunction.compareValues(columnValue, value, operator)) {
                            filteredRows.add(row);
                        }
                    }
                }
            }
            table.setRows(filteredRows);
        }
    }

    private Row combineRows(Row mainRow, Row joinRow) {
        Object[] combinedValues = new Object[mainRow.getValues().length + joinRow.getValues().length];

        // Copy values from main row
        System.arraycopy(mainRow.getValues(), 0, combinedValues, 0, mainRow.getValues().length);

        // Copy values from join row
        System.arraycopy(joinRow.getValues(), 0, combinedValues, mainRow.getValues().length,
                joinRow.getValues().length);

        return new Row(combinedValues);
    }

    private void applyInnerJoin(Table mainTableData, Table joinTableData, Expression onExpression) {

        ArrayList<Row> resultRows = new ArrayList<>();

        for (Row mainRow : mainTableData.getRows()) {
            // Get the value of the join column from the main table
            String joinColumnName = ((BinaryExpression) onExpression).getLeftExpression().toString();
            int joinColumnIndex = mainTableData.getColumnIndex(joinColumnName);

            Object joinColumnValue = mainRow.getValues()[joinColumnIndex];

            // Find matching rows in the join table based on the join column value
            Row matchingJoinRow = findRowByCondition(joinTableData, joinColumnName, joinColumnValue);
            // If matching row is found, combine the data from main table row and join table
            if (matchingJoinRow != null) {
                Row newRow = combineRows(mainRow, matchingJoinRow);
                // resultTable.addRow(newRow);
                resultRows.add(newRow);

            }
        }
        mainTableData.setRows(resultRows);
        for (Column joinColumn : joinTableData.getColumns()) {
            // Check if the column already exists in the main table
            mainTableData.getColumns().add(joinColumn);
        }

    }

    private Table getTableOrThrow(String databaseName, String tableName) {
        Table table = this.tableService.getTable(databaseName, tableName);
        if (table == null) {
            throw new ObjectNotFoundException("Table not found: " + tableName);
        }
        return table;
    }

    // HANDLE ALTER STATEMENT
    private String extractTableName(String sqlExpression) {
        Pattern mainTablePattern = MAIN_TABLE_PATTERN;
        Matcher mainTableMatcher = mainTablePattern.matcher(sqlExpression);
        if (mainTableMatcher.find()) {
            return mainTableMatcher.group(1);
        }
        return null;
    }

    private String extractConstraintName(String sqlExpression) {
        Pattern constraintPattern = CONSTRAINT_PATTERN;
        Matcher constraintMatcher = constraintPattern.matcher(sqlExpression);
        if (constraintMatcher.find()) {
            return constraintMatcher.group(1);
        }
        return null;
    }

    private String extractColumnName(String sqlExpression) {
        Pattern columnPattern = COLUMN_PATTERN;
        Matcher columnMatcher = columnPattern.matcher(sqlExpression);
        if (columnMatcher.find()) {
            return columnMatcher.group(1);
        }
        return null;
    }

    private String extractReferencedTable(String sqlExpression) {
        Pattern refTablePattern = REF_TABLE_PATTERN;
        Matcher refTableMatcher = refTablePattern.matcher(sqlExpression);
        if (refTableMatcher.find()) {
            return refTableMatcher.group(1);
        }
        return null;
    }

    private String extractReferencedColumn(String sqlExpression) {
        Pattern refTablePattern = REF_TABLE_PATTERN;
        Matcher refTableMatcher = refTablePattern.matcher(sqlExpression);
        if (refTableMatcher.find()) {
            return refTableMatcher.group(2);
        }
        return null;
    }

    private String extractDropColumnName(String sqlExpression) {
        Matcher matcher = DROP_COLUMN_PATTERN.matcher(sqlExpression);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private String extractAddColumnName(String sqlExpression) {
        Matcher matcher = ADD_COLUMN_NAME.matcher(sqlExpression);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private String extractAddColumnType(String sqlExpression) {
        Matcher matcher = ADD_COLUMN_TYPE.matcher(sqlExpression);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    public Table handleAlterStatement(Statement statement, String databaseName) {
        String sqlExpression = statement.toString();
        String tableName = extractTableName(sqlExpression);
        Database database = databaseRepository.findDatabaseByName(databaseName);
        Table tableData = database.getTable(tableName);
        if (sqlExpression.toUpperCase().contains("DROP COLUMN")) {
            // Handle drop column
            String columnName = extractDropColumnName(sqlExpression);
            tableData = this.tableService.deleteColumn(databaseName, tableName, columnName);
        } else if (sqlExpression.toUpperCase().contains("ADD CONSTRAINT")) {
            // Handle add foreign key
            String constraintName = extractConstraintName(sqlExpression);
            String columnName = extractColumnName(sqlExpression);
            String referencedTable = extractReferencedTable(sqlExpression);
            String referencedColumn = extractReferencedColumn(sqlExpression);
            tableData.addForeignKey(columnName, referencedTable, referencedColumn);
            this.databaseRepository.save(database);
        } else {
            // Handle add column
            String columnName = extractAddColumnName(sqlExpression);
            String columnType = extractAddColumnType(sqlExpression);
            tableData.addColumn(columnName, columnType);
            this.databaseRepository.save(database);
        }
        return tableData;
    }

    private List<String> extractPrimaryKeyDetails(String primaryKeyExpression) {
        List<String> details = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\((.*?)\\)");
        Matcher matcher = pattern.matcher(primaryKeyExpression);
        while (matcher.find()) {
            details.add(matcher.group(1));
        }
        return details;
    }

    private List<String> extractForeignKeyDetails(String foreignKeyExpression) {
        List<String> details = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\((.*?)\\)");
        Matcher matcher = pattern.matcher(foreignKeyExpression);
        while (matcher.find()) {
            details.add(matcher.group(1));
        }
        pattern = Pattern.compile("REFERENCES (.*?)\\(");
        matcher = pattern.matcher(foreignKeyExpression);
        if (matcher.find()) {
            details.add(matcher.group(1).trim());
        }
        return details;
    }

    public Table handleCreateTable(Statement statement, String databaseName) {
        CreateTable createTableStatement = (CreateTable) statement;

        String tableName = createTableStatement.getTable().getName();
        List<ColumnDefinition> columnDefinitions = createTableStatement.getColumnDefinitions();

        this.tableService.addTable(databaseName, tableName, columnDefinitions);
        // Extract foreign key details

        Database database = databaseRepository.findDatabaseByName(databaseName);
        Table tableData = database.getTable(tableName);

        for (Index index : createTableStatement.getIndexes()) {
            if (index.getType().equals("FOREIGN KEY")) {
                List<String> foreignKeyDetails = extractForeignKeyDetails(index.toString());
                String[] foreignKeyArray = foreignKeyDetails.toArray(new String[0]);
                tableData.addForeignKey(foreignKeyArray[0], foreignKeyArray[2], foreignKeyArray[1]);
            } else if (index.getType().equals("PRIMARY KEY")) {
                List<String> primaryKeyDetails = extractPrimaryKeyDetails(index.toString());
                String primaryKey = primaryKeyDetails.get(0);
                tableData.setPrimaryKey(primaryKey);
            }
        }
        this.databaseRepository.save(database);
        System.out.println("Table created successfully");
        return tableData;
    }

    public Table handleSelectStatement(Statement statement, String databaseName) {
        Select selectStatement = (Select) statement;
        PlainSelect plainSelect = (PlainSelect) selectStatement.getSelectBody();

        // Get the table name from the FROM clause
        String tableName = plainSelect.getFromItem().toString();

        // Get the table data
        Table tableData = getTableOrThrow(databaseName, tableName);

        // Handle WHERE clause conditions to filter rows
        Expression whereExpression = plainSelect.getWhere();
        if (whereExpression != null) {
            applyWhereClause(tableData, whereExpression);
        }

        // Handle JOIN
        List<Join> joins = plainSelect.getJoins();
        if (joins != null) {
            joins.forEach(join -> {
                String joinTableName = join.getRightItem().toString();
                Table joinTableData = getTableOrThrow(databaseName, joinTableName);
                applyInnerJoin(tableData, joinTableData, join.getOnExpression());
            });
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

    private void validatePrimaryKeyConstraints(String databaseName, String tableName, Object[] insertValues) {
        Table table = getTableOrThrow(databaseName, tableName);
        Column primaryKeyColumn = table.getPrimaryKeyColumn();

        if (primaryKeyColumn != null) {
            int primaryKeyIndex = table.getColumns().indexOf(primaryKeyColumn);

            if (table.isValueExistsInPrimaryKeyColumn(primaryKeyIndex, insertValues[primaryKeyIndex])) {
                throw new InvalidRequestException(String.format(
                        "The value of the primary key '%s' must be unique.", primaryKeyColumn.getName()));
            }
        }
    }

    private void validateForeignKeyConstraints(String databaseName, String tableName, Object[] insertValues) {
        Table table = getTableOrThrow(databaseName, tableName);
        Column foreignKeyColumn = table.getForeignKeyColumn();

        if (table.getForeignKeyColumn() != null) {
            int foreignKeyIndex = table.getColumns().indexOf(foreignKeyColumn);
            Table referencedTable = getTableOrThrow(databaseName, foreignKeyColumn.getReferencedTableName());
            int referencedKeyIndex = referencedTable.getColumns()
                    .indexOf(referencedTable.getColumnByName(foreignKeyColumn.getReferencedColumnName()));

            if (referencedKeyIndex == -1) {
                throw new InvalidRequestException(String.format("Column '%s' in Referenced Table '%s' does not exist",
                        foreignKeyColumn.getReferencedColumnName(), foreignKeyColumn.getReferencedTableName()));
            }

            if (!referencedTable.isValueExistsInReferencedColumn(referencedKeyIndex, insertValues[foreignKeyIndex])) {
                throw new InvalidRequestException(
                        "Value does not exist in the referenced column: " + foreignKeyColumn.getReferencedColumnName());
            }
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
            values[i] = SharedFunction.parseValue(trimmedValue);
        }

        validatePrimaryKeyConstraints(databaseName, tableName, values);
        validateForeignKeyConstraints(databaseName, tableName, values);

        // Add row into the table
        Row rowRequest = new Row(values);
        this.rowService.addRow(databaseName, tableName, rowRequest);
    }

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
            throw new ObjectNotFoundException(
                    "Table '" + tableName + "' not found in database '" + databaseName + "'.");
        }

        // Find the row based on the condition
        Row rowToUpdate = findRowByCondition(table, columnName, value);
        if (rowToUpdate == null) {
            throw new ObjectNotFoundException(
                    "Row with condition '" + columnName + " = " + value + "' not found in table '"
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
                Object newValue = SharedFunction.parseValue(updateExpression.toString());
                table.checkValidValueWithIndex(newValue, columnIndex);
                rowToUpdate.getValues()[columnIndex] = newValue;
            }
        }

        validatePrimaryKeyConstraints(databaseName, tableName, rowToUpdate.getValues());
        validateForeignKeyConstraints(databaseName, tableName, rowToUpdate.getValues());

        // Save the changes to the database
        this.databaseRepository.save(database);
    }

    public void handleUpdateStatement(String databaseName, Update updateStatement,
            HashMap<String, Expression> updates) {
        String tableName = updateStatement.getTable().getName();
        // Extract the conditions of the Update statement
        Expression where = updateStatement.getWhere();
        String condition = where.toString();
        String[] parts = condition.split("="); // Split the condition string into parts
        String columnName = parts[0].trim(); // Extract the column name
        Object value = SharedFunction.parseValue(parts[1].trim());

        // Gọi phương thức để thực thi truy vấn cập nhật
        updateRowByCondition(databaseName, tableName, columnName, value, updates);
    }

}
