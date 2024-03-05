package com.database.thiendb.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.database.thiendb.DataStructure.Column;
import com.database.thiendb.Service.ColumnService;

@RestController
@RequestMapping(path = "/api")
public class ColumnController {
    private final ColumnService columnService;

    public ColumnController(ColumnService columnService) {
        this.columnService = columnService;
    }
    @PostMapping("/database/{databaseName}/table/{tableName}/columns")
    public void addColumn(@PathVariable("databaseName") String databaseName,
            @PathVariable("tableName") String tableName, @RequestBody Column columnRequest) {
        // TODO        
        this.columnService.addColumn(databaseName, tableName, columnRequest);
    }
}
