package com.database.thiendb.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.database.thiendb.DataStructure.Table;
import com.database.thiendb.Service.TableService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping(path = "/api")
public class TableController {
    private final TableService tableService;

    public TableController(TableService tableService) {
        this.tableService = tableService;
    }

    @GetMapping("/database/{databaseName}/table/{tableName}")
    public ResponseEntity<Object> getTable(@PathVariable("databaseName") String databaseName,
            @PathVariable("tableName") String tableName) {
        // Retrieve the row data from the database
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
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing JSON data");
            }
        } else {
            // Return 404 Not Found if row data does not exist
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/database/{databaseName}/table")
    public void addTable(@PathVariable("databaseName") String databaseName,
            @RequestBody Map<String, String> requestBody) {
        // TODO
        String tableName = requestBody.get("tableName");
        this.tableService.addTable(databaseName, tableName);
    }

    // Edit table name
    @PutMapping("/database/{databaseName}/table/{tableName}")
    public void deleteRow(@PathVariable("databaseName") String databaseName, @PathVariable("tableName") String tableName,
            @RequestBody Map<String, String> requestBody) {
        String newTableName = requestBody.get("newTableName");
        this.tableService.updateTableName(databaseName, tableName, newTableName);
    }

    @DeleteMapping("/database/{databaseName}/table/{tableName}")
    public void deleteRow(@PathVariable("databaseName") String databaseName,
            @PathVariable("tableName") String tableName) {
        this.tableService.deleteTable(databaseName, tableName);
    }
}
