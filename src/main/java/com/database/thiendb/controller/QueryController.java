package com.database.thiendb.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.database.thiendb.DataStructure.Row;
import com.database.thiendb.DataStructure.Table;
import com.database.thiendb.Service.RowService;
import com.database.thiendb.Service.TableService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.statement.values.ValuesStatement;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.ItemsList;

@RestController
@RequestMapping(path = "/api")
public class QueryController {
    private final TableService tableService;
    private final RowService rowService;

    public QueryController(TableService tableService, RowService rowService) {
        this.tableService = tableService;
        this.rowService = rowService;
    }

    @PostMapping("/parse-sql/{databaseName}")
    public ResponseEntity<Object> parseSQL(@RequestBody String query,
            @PathVariable("databaseName") String databaseName) {
        try {
            // Parse câu truy vấn SQL
            Statement statement = CCJSqlParserUtil.parse(query);

            if (statement instanceof Select) {
                Select selectStatement = (Select) statement;
                PlainSelect plainSelect = (PlainSelect) selectStatement.getSelectBody();

                // Kiểm tra các cột được chọn
                List<SelectItem> selectItems = plainSelect.getSelectItems();
                if (selectItems.size() == 1 && selectItems.get(0).toString().equals("*")) {
                    // Chọn tất cả các cột
                    Object table = (Object) plainSelect.getFromItem();
                    String tableName = table.toString();
                    Table tableData = this.tableService.getTable(databaseName, tableName);
                    // Check if the row data exists
                    if (tableData != null) {
                        // Convert the row data to JSON format
                        ObjectMapper objectMapper = new ObjectMapper();
                        try {
                            String jsonData = objectMapper.writeValueAsString(tableData);
                            // Return the JSON data as the response
                            return ResponseEntity.ok(jsonData);
                        } catch (JsonProcessingException e) { // Handle JSON processing exception
                            e.printStackTrace();
                            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                    .body("Error processing JSON data");
                        }
                    } else {
                        // Return 404 Not Found if row data does not exist
                        return ResponseEntity.notFound().build();
                    }
                } else {
                    // Trường hợp chọn các cột cụ thể
                    Object table = (Object) plainSelect.getFromItem();
                    String tableName = table.toString();
                    Table tableData = this.tableService.getTable(databaseName, tableName);

                    tableData.getSelectedElements(selectItems);
                    if (tableData != null) {
                        // Convert the row data to JSON format
                        ObjectMapper objectMapper = new ObjectMapper();
                        try {
                            String jsonData = objectMapper.writeValueAsString(tableData);
                            // Return the JSON data as the response
                            return ResponseEntity.ok(jsonData);
                        } catch (JsonProcessingException e) { // Handle JSON processing exception
                            e.printStackTrace();
                            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                    .body("Error processing JSON data");
                        }
                    }
                }
            }
            if (statement instanceof Insert) {
                System.out.println("Executing addRow()");
                Insert insertStatement = (Insert) statement;
                String tableName = insertStatement.getTable().getName();
                System.out.println("Get getItemsList");
                ItemsList valuesStatement = insertStatement.getItemsList();
                String valuesString = valuesStatement.toString();
                System.out.println(valuesString);
                // Remove parentheses and split by comma
                String[] valueStrings = valuesString.substring(1, valuesString.length() - 1).split(",");

                // Trim each value and remove single quotes if present
                Object[] values = new Object[valueStrings.length];
                for (int i = 0; i < valueStrings.length; i++) {
                    String trimmedValue = valueStrings[i].trim();
                    if (trimmedValue.startsWith("'") && trimmedValue.endsWith("'")) {
                        // Remove single quotes for string literals
                        values[i] = trimmedValue.substring(1, trimmedValue.length() - 1);
                    } else {
                        // Convert other values directly
                        if (trimmedValue.matches("-?\\d+")) {
                            // If the value consists of digits only, it's an integer
                            values[i] = Integer.parseInt(trimmedValue);
                        } else if (trimmedValue.matches("-?\\d+\\.\\d+")) {
                            // If the value is in decimal format, it's a double
                            values[i] = Double.parseDouble(trimmedValue);
                        } else {
                            // Otherwise, treat it as a string
                            values[i] = trimmedValue;
                        }

                    }
                }
                Row rowRequest = new Row(values);
                this.rowService.addRow(databaseName, tableName, rowRequest);
            }
            if (statement instanceof Update) {
                System.out.println("Executing updateRow()");

            }

            if (statement instanceof Delete) {
                Delete deleteStatement = (Delete) statement;
                String tableName = deleteStatement.getTable().getName();
                this.tableService.deleteTable(databaseName, tableName);
            }

        } catch (JSQLParserException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok(query);

    }
}
