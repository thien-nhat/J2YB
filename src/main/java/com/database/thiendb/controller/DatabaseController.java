package com.database.thiendb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.database.thiendb.JsonDatabaseExporter;
import com.database.thiendb.DataStructure.Column;
import com.database.thiendb.DataStructure.Row;
import com.database.thiendb.Service.DatabaseService;

@RestController
@RequestMapping(path = "/api")
public class DatabaseController {
    private final DatabaseService databaseService;

    public DatabaseController(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @PostMapping("/database/{databaseName}/table/{tableName}")
    public void addTable(@PathVariable("databaseName") String databaseName, @PathVariable("tableName") String tableName,
            @RequestBody Column columnRequest) {
        // TODO
        this.databaseService.addTable(databaseName, databaseName);
    }

    @PostMapping("/database/{databaseName}/table/{tableName}/columns")
    public void addColumn(@PathVariable("databaseName") String databaseName,
            @PathVariable("tableName") String tableName, @RequestBody Column columnRequest) {
        // TODO
        this.databaseService.addColumn(databaseName, tableName, columnRequest);
    }

    @PostMapping("/database/{databaseName}/table/{tableName}/rows")
    public void addRow(@PathVariable("databaseName") String databaseName, @PathVariable("tableName") String tableName,
            @RequestBody Row rowRequest) {
        // TODO
        this.databaseService.addRow(databaseName, tableName, rowRequest);
        JsonDatabaseExporter.exportToJson(this.databaseService.getDatabase());
    }

    @PutMapping("/database/{databaseName}/table/{tableName}/rows/{rowId}")
    public void updateRow(@PathVariable("databaseName") String databaseName, @PathVariable("tableName") String tableName, @PathVariable("rowId") Integer rowId,
            @RequestBody Row rowRequest) {
        // TODO
        this.databaseService.updateRow(databaseName, tableName, rowId, rowRequest);
        JsonDatabaseExporter.exportToJson(this.databaseService.getDatabase());
    }

}
