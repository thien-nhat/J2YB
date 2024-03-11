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

import com.database.thiendb.DataStructure.Table;
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

@RestController
@RequestMapping(path = "/api")
public class QueryController {
    private final TableService tableService;

    public QueryController(TableService tableService) {
        this.tableService = tableService;
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


            }
            if (statement instanceof Update) {
                System.out.println("Executing updateRow()");

            }

            if (statement instanceof Delete) {
                System.out.println("Executing deleteRow()");
                
            }

        } catch (JSQLParserException e) {
            e.printStackTrace();
        }
        // return query;
        return ResponseEntity.ok(query);

    }
}
