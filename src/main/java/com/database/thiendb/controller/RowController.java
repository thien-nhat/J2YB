package com.database.thiendb.controller;

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

import com.database.thiendb.DataStructure.Row;
import com.database.thiendb.Service.RowService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping(path = "/api")
public class RowController {
    private final RowService rowService;

    public RowController(RowService rowService) {
        this.rowService = rowService;
    }

    @GetMapping("/database/{databaseName}/table/{tableName}/rows/{rowId}")
    public ResponseEntity<Object> getRow(@PathVariable("databaseName") String databaseName,
            @PathVariable("tableName") String tableName,
            @PathVariable("rowId") Integer rowId) {
        // Retrieve the row data from the database
        Row rowData = this.rowService.getRow(databaseName, tableName, rowId);


        // Check if the row data exists
        if (rowData != null) {
            // Convert the row data to JSON format
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonData = objectMapper.writeValueAsString(rowData);
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

    @PostMapping("/database/{databaseName}/table/{tableName}/rows")
    public void addRow(@PathVariable("databaseName") String databaseName,
                       @PathVariable("tableName") String tableName,
                       @RequestBody Row rowRequest) {
        // TODO
        this.rowService.addRow(databaseName, tableName, rowRequest);
    }

    @PutMapping("/database/{databaseName}/table/{tableName}/rows/{rowId}")
    public void updateRow(@PathVariable("databaseName") String databaseName,
                          @PathVariable("tableName") String tableName,
                          @PathVariable("rowId") Integer rowId,
                          @RequestBody Row rowRequest) {
        // TODO
        this.rowService.updateRow(databaseName, tableName, rowId, rowRequest);
    }

    @DeleteMapping("/database/{databaseName}/table/{tableName}/rows/{rowId}")
    public void deleteRow(@PathVariable("databaseName") String databaseName,
                          @PathVariable("tableName") String tableName,
                          @PathVariable("rowId") Integer rowId) {
        // TODO
        this.rowService.deleteRow(databaseName, tableName, rowId);
    }
}
