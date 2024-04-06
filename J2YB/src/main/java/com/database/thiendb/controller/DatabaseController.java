package com.database.thiendb.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.database.thiendb.DataStructure.Database;
import com.database.thiendb.Service.DatabaseService;
import com.database.thiendb.Utils.SharedFunction;

@RestController
@RequestMapping(path = "/api")
public class DatabaseController {
    private final DatabaseService databaseService;

    public DatabaseController(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    // Get all databases
    @GetMapping("/databases")
    public List<Database> getAllDatabases() {
        return databaseService.getAllDatabases();
    }

    // Get database by ID
    @GetMapping("/databases/{name}")
    public Database getDatabaseByName(@PathVariable("name") String name) {

        Object databaseNameValue = SharedFunction.parseValue(name);
        if (!(databaseNameValue instanceof String)) {
            throw new MethodArgumentTypeMismatchException(databaseNameValue, String.class, name, null, null);
        }
        return databaseService.getDatabaseByName(name);
    }
}
