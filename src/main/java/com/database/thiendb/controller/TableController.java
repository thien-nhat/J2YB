package com.database.thiendb.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.database.thiendb.Service.TableService;

@RestController
@RequestMapping(path = "/api")
public class TableController {
    private final TableService tableService;

    public TableController(TableService tableService) {
        this.tableService = tableService;
    }
    @PostMapping("/database/{databaseName}/table/{tableName}")
    public void addTable(@PathVariable("databaseName") String databaseName, @PathVariable("tableName") String tableName) {
        // TODO
        this.tableService.addTable(databaseName, tableName);
    }

}
